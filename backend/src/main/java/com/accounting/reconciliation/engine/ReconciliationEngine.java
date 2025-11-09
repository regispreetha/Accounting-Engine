package com.accounting.reconciliation.engine;

import com.accounting.reconciliation.model.*;
import com.accounting.reconciliation.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Core Reconciliation Engine
 * Handles the reconciliation logic between ledger and subledger transactions
 */
@Component
public class ReconciliationEngine {

    private static final Logger logger = LoggerFactory.getLogger(ReconciliationEngine.class);

    @Autowired
    private LedgerTransactionRepository ledgerTransactionRepository;

    @Autowired
    private SubledgerTransactionRepository subledgerTransactionRepository;

    @Autowired
    private ReconciliationRuleRepository ruleRepository;

    @Autowired
    private ReconciliationRunRepository runRepository;

    @Autowired
    private ReconciliationMatchRepository matchRepository;

    @Autowired
    private ReconciliationMatchDetailRepository matchDetailRepository;

    @Autowired
    private ReconciliationExceptionRepository exceptionRepository;

    @Autowired
    private RulesEngine rulesEngine;

    /**
     * Execute reconciliation for a given rule group
     */
    @Transactional
    public ReconciliationRun executeReconciliation(ReconciliationRun run) {
        logger.info("Starting reconciliation run: {}", run.getRunName());

        try {
            // Update run status
            run.setStatus("IN_PROGRESS");
            run.setExecutionStartTime(LocalDateTime.now());
            run = runRepository.save(run);

            // Get active rules for the rule group
            List<ReconciliationRule> rules = ruleRepository.findActiveRulesByGroupId(run.getRuleGroupId());
            if (rules.isEmpty()) {
                throw new RuntimeException("No active rules found for rule group: " + run.getRuleGroupId());
            }

            // Get transactions to reconcile
            List<LedgerTransaction> ledgerTransactions = ledgerTransactionRepository.findPendingReconciliation(
                run.getRuleGroup().getLedgerId(),
                run.getPeriodStartDate(),
                run.getPeriodEndDate()
            );

            List<SubledgerTransaction> subledgerTransactions = subledgerTransactionRepository.findPendingReconciliation(
                run.getRuleGroup().getSubledgerId(),
                run.getPeriodStartDate(),
                run.getPeriodEndDate()
            );

            logger.info("Found {} ledger transactions and {} subledger transactions to reconcile",
                ledgerTransactions.size(), subledgerTransactions.size());

            // Update run statistics
            run.setTotalLedgerRecords(ledgerTransactions.size());
            run.setTotalSubledgerRecords(subledgerTransactions.size());

            // Track matched transactions
            Set<Long> matchedLedgerIds = new HashSet<>();
            Set<Long> matchedSubledgerIds = new HashSet<>();

            // Execute matching for each rule in priority order
            for (ReconciliationRule rule : rules) {
                logger.info("Applying rule: {} ({})", rule.getRuleName(), rule.getRuleCode());

                // Get unmatched transactions
                List<LedgerTransaction> unmatchedLedger = ledgerTransactions.stream()
                    .filter(lt -> !matchedLedgerIds.contains(lt.getTransactionId()))
                    .toList();

                List<SubledgerTransaction> unmatchedSubledger = subledgerTransactions.stream()
                    .filter(st -> !matchedSubledgerIds.contains(st.getTransactionId()))
                    .toList();

                // Perform matching based on rule type
                List<ReconciliationMatch> matches = performMatching(
                    run, rule, unmatchedLedger, unmatchedSubledger
                );

                // Save matches and update tracking sets
                for (ReconciliationMatch match : matches) {
                    matchRepository.save(match);

                    for (ReconciliationMatchDetail detail : match.getMatchDetails()) {
                        matchDetailRepository.save(detail);

                        if (detail.getLedgerTransactionId() != null) {
                            matchedLedgerIds.add(detail.getLedgerTransactionId());
                            updateLedgerTransactionStatus(detail.getLedgerTransactionId(), run.getRunId());
                        }
                        if (detail.getSubledgerTransactionId() != null) {
                            matchedSubledgerIds.add(detail.getSubledgerTransactionId());
                            updateSubledgerTransactionStatus(detail.getSubledgerTransactionId(), run.getRunId());
                        }
                    }
                }
            }

            // Process unmatched transactions as exceptions
            processUnmatchedTransactions(run, ledgerTransactions, subledgerTransactions,
                matchedLedgerIds, matchedSubledgerIds);

            // Finalize run statistics
            finalizeRunStatistics(run, matchedLedgerIds.size(), matchedSubledgerIds.size());

            // Update run status
            run.setStatus("COMPLETED");
            run.setExecutionEndTime(LocalDateTime.now());
            run.setExecutionDurationSeconds(
                (int) Duration.between(run.getExecutionStartTime(), run.getExecutionEndTime()).getSeconds()
            );

            run = runRepository.save(run);
            logger.info("Reconciliation run completed successfully: {}", run.getRunName());

            return run;

        } catch (Exception e) {
            logger.error("Error executing reconciliation run: {}", e.getMessage(), e);
            run.setStatus("FAILED");
            run.setComments("Error: " + e.getMessage());
            run.setExecutionEndTime(LocalDateTime.now());
            return runRepository.save(run);
        }
    }

    /**
     * Perform matching based on rule type
     */
    private List<ReconciliationMatch> performMatching(
        ReconciliationRun run,
        ReconciliationRule rule,
        List<LedgerTransaction> ledgerTransactions,
        List<SubledgerTransaction> subledgerTransactions
    ) {
        List<ReconciliationMatch> matches = new ArrayList<>();

        switch (rule.getRuleType()) {
            case "ONE_TO_ONE":
                matches.addAll(performOneToOneMatching(run, rule, ledgerTransactions, subledgerTransactions));
                break;
            case "ONE_TO_MANY":
                matches.addAll(performOneToManyMatching(run, rule, ledgerTransactions, subledgerTransactions));
                break;
            case "MANY_TO_ONE":
                matches.addAll(performManyToOneMatching(run, rule, ledgerTransactions, subledgerTransactions));
                break;
            case "MANY_TO_MANY":
                matches.addAll(performManyToManyMatching(run, rule, ledgerTransactions, subledgerTransactions));
                break;
        }

        return matches;
    }

    /**
     * One-to-One matching
     */
    private List<ReconciliationMatch> performOneToOneMatching(
        ReconciliationRun run,
        ReconciliationRule rule,
        List<LedgerTransaction> ledgerTransactions,
        List<SubledgerTransaction> subledgerTransactions
    ) {
        List<ReconciliationMatch> matches = new ArrayList<>();

        for (LedgerTransaction ledgerTrans : ledgerTransactions) {
            for (SubledgerTransaction subledgerTrans : subledgerTransactions) {
                if (rulesEngine.evaluateMatch(ledgerTrans, subledgerTrans, rule)) {
                    ReconciliationMatch match = createMatch(run, rule, "ONE_TO_ONE");

                    ReconciliationMatchDetail detail = new ReconciliationMatchDetail();
                    detail.setMatchId(match.getMatchId());
                    detail.setLedgerTransactionId(ledgerTrans.getTransactionId());
                    detail.setSubledgerTransactionId(subledgerTrans.getTransactionId());
                    detail.setMatchedAmount(ledgerTrans.getNetAmount());
                    detail.setVarianceAmount(
                        ledgerTrans.getNetAmount().subtract(subledgerTrans.getNetAmount()).abs()
                    );
                    detail.setCreatedBy(run.getCreatedBy());

                    match.setMatchDetails(Collections.singletonList(detail));
                    match.setLedgerAmount(ledgerTrans.getNetAmount());
                    match.setSubledgerAmount(subledgerTrans.getNetAmount());
                    match.setVarianceAmount(detail.getVarianceAmount());

                    matches.add(match);
                    break; // One-to-one, so break after first match
                }
            }
        }

        return matches;
    }

    /**
     * One-to-Many matching (1 ledger to multiple subledger)
     */
    private List<ReconciliationMatch> performOneToManyMatching(
        ReconciliationRun run,
        ReconciliationRule rule,
        List<LedgerTransaction> ledgerTransactions,
        List<SubledgerTransaction> subledgerTransactions
    ) {
        List<ReconciliationMatch> matches = new ArrayList<>();

        for (LedgerTransaction ledgerTrans : ledgerTransactions) {
            List<SubledgerTransaction> matchingSubledgers = new ArrayList<>();
            BigDecimal totalSubledgerAmount = BigDecimal.ZERO;

            for (SubledgerTransaction subledgerTrans : subledgerTransactions) {
                if (rulesEngine.evaluateMatch(ledgerTrans, subledgerTrans, rule)) {
                    matchingSubledgers.add(subledgerTrans);
                    totalSubledgerAmount = totalSubledgerAmount.add(subledgerTrans.getNetAmount());
                }
            }

            if (!matchingSubledgers.isEmpty() &&
                totalSubledgerAmount.compareTo(ledgerTrans.getNetAmount()) == 0) {

                ReconciliationMatch match = createMatch(run, rule, "ONE_TO_MANY");
                List<ReconciliationMatchDetail> details = new ArrayList<>();

                for (SubledgerTransaction subledgerTrans : matchingSubledgers) {
                    ReconciliationMatchDetail detail = new ReconciliationMatchDetail();
                    detail.setMatchId(match.getMatchId());
                    detail.setLedgerTransactionId(ledgerTrans.getTransactionId());
                    detail.setSubledgerTransactionId(subledgerTrans.getTransactionId());
                    detail.setMatchedAmount(subledgerTrans.getNetAmount());
                    detail.setVarianceAmount(BigDecimal.ZERO);
                    detail.setCreatedBy(run.getCreatedBy());
                    details.add(detail);
                }

                match.setMatchDetails(details);
                match.setLedgerAmount(ledgerTrans.getNetAmount());
                match.setSubledgerAmount(totalSubledgerAmount);
                match.setVarianceAmount(BigDecimal.ZERO);

                matches.add(match);
            }
        }

        return matches;
    }

    /**
     * Many-to-One matching (multiple ledger to 1 subledger)
     */
    private List<ReconciliationMatch> performManyToOneMatching(
        ReconciliationRun run,
        ReconciliationRule rule,
        List<LedgerTransaction> ledgerTransactions,
        List<SubledgerTransaction> subledgerTransactions
    ) {
        // Similar to one-to-many but reversed
        List<ReconciliationMatch> matches = new ArrayList<>();
        // Implementation would be similar to one-to-many but with ledger and subledger roles reversed
        return matches;
    }

    /**
     * Many-to-Many matching
     */
    private List<ReconciliationMatch> performManyToManyMatching(
        ReconciliationRun run,
        ReconciliationRule rule,
        List<LedgerTransaction> ledgerTransactions,
        List<SubledgerTransaction> subledgerTransactions
    ) {
        // Complex matching logic for many-to-many
        List<ReconciliationMatch> matches = new ArrayList<>();
        // Would require more sophisticated grouping and matching algorithms
        return matches;
    }

    /**
     * Create a reconciliation match
     */
    private ReconciliationMatch createMatch(ReconciliationRun run, ReconciliationRule rule, String matchType) {
        ReconciliationMatch match = new ReconciliationMatch();
        match.setRunId(run.getRunId());
        match.setRuleId(rule.getRuleId());
        match.setMatchType(matchType);
        match.setMatchStatus("MATCHED");
        match.setMatchConfidence(100);
        match.setCreatedBy(run.getCreatedBy());
        return match;
    }

    /**
     * Update ledger transaction status
     */
    private void updateLedgerTransactionStatus(Long transactionId, Long runId) {
        LedgerTransaction transaction = ledgerTransactionRepository.findById(transactionId).orElse(null);
        if (transaction != null) {
            transaction.setReconciliationStatus("MATCHED");
            transaction.setIsReconciled("Y");
            transaction.setReconciliationId(runId);
            ledgerTransactionRepository.save(transaction);
        }
    }

    /**
     * Update subledger transaction status
     */
    private void updateSubledgerTransactionStatus(Long transactionId, Long runId) {
        SubledgerTransaction transaction = subledgerTransactionRepository.findById(transactionId).orElse(null);
        if (transaction != null) {
            transaction.setReconciliationStatus("MATCHED");
            transaction.setIsReconciled("Y");
            transaction.setReconciliationId(runId);
            subledgerTransactionRepository.save(transaction);
        }
    }

    /**
     * Process unmatched transactions as exceptions
     */
    private void processUnmatchedTransactions(
        ReconciliationRun run,
        List<LedgerTransaction> allLedger,
        List<SubledgerTransaction> allSubledger,
        Set<Long> matchedLedgerIds,
        Set<Long> matchedSubledgerIds
    ) {
        // Create exceptions for unmatched ledger transactions
        for (LedgerTransaction ledgerTrans : allLedger) {
            if (!matchedLedgerIds.contains(ledgerTrans.getTransactionId())) {
                ReconciliationException exception = new ReconciliationException();
                exception.setRunId(run.getRunId());
                exception.setExceptionType("UNMATCHED_LEDGER");
                exception.setLedgerTransactionId(ledgerTrans.getTransactionId());
                exception.setExceptionDescription("Ledger transaction not matched with any subledger transaction");
                exception.setExceptionAmount(ledgerTrans.getNetAmount());
                exception.setSeverity("MEDIUM");
                exception.setStatus("OPEN");
                exception.setCreatedBy(run.getCreatedBy());
                exceptionRepository.save(exception);
            }
        }

        // Create exceptions for unmatched subledger transactions
        for (SubledgerTransaction subledgerTrans : allSubledger) {
            if (!matchedSubledgerIds.contains(subledgerTrans.getTransactionId())) {
                ReconciliationException exception = new ReconciliationException();
                exception.setRunId(run.getRunId());
                exception.setExceptionType("UNMATCHED_SUBLEDGER");
                exception.setSubledgerTransactionId(subledgerTrans.getTransactionId());
                exception.setExceptionDescription("Subledger transaction not matched with any ledger transaction");
                exception.setExceptionAmount(subledgerTrans.getNetAmount());
                exception.setSeverity("MEDIUM");
                exception.setStatus("OPEN");
                exception.setCreatedBy(run.getCreatedBy());
                exceptionRepository.save(exception);
            }
        }
    }

    /**
     * Finalize run statistics
     */
    private void finalizeRunStatistics(ReconciliationRun run, int matchedLedger, int matchedSubledger) {
        run.setMatchedRecords(matchedLedger);
        run.setUnmatchedLedgerRecords(run.getTotalLedgerRecords() - matchedLedger);
        run.setUnmatchedSubledgerRecords(run.getTotalSubledgerRecords() - matchedSubledger);

        // Calculate matched and unmatched amounts
        List<ReconciliationMatch> matches = matchRepository.findByRunId(run.getRunId());
        BigDecimal totalMatched = matches.stream()
            .map(ReconciliationMatch::getLedgerAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        run.setTotalMatchedAmount(totalMatched);

        List<ReconciliationException> exceptions = exceptionRepository.findByRunId(run.getRunId());
        run.setExceptionRecords(exceptions.size());

        BigDecimal totalUnmatched = exceptions.stream()
            .map(ReconciliationException::getExceptionAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        run.setTotalUnmatchedAmount(totalUnmatched);
        run.setVarianceAmount(totalUnmatched);
    }
}
