package com.accounting.reconciliation.service;

import com.accounting.reconciliation.engine.ReconciliationEngine;
import com.accounting.reconciliation.model.*;
import com.accounting.reconciliation.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for Reconciliation operations
 */
@Service
public class ReconciliationService {

    private static final Logger logger = LoggerFactory.getLogger(ReconciliationService.class);

    @Autowired
    private ReconciliationRunRepository runRepository;

    @Autowired
    private ReconciliationRuleGroupRepository ruleGroupRepository;

    @Autowired
    private ReconciliationMatchRepository matchRepository;

    @Autowired
    private ReconciliationExceptionRepository exceptionRepository;

    @Autowired
    private ReconciliationEngine reconciliationEngine;

    /**
     * Create and execute a new reconciliation run
     */
    @Transactional
    public ReconciliationRun createAndExecuteReconciliation(
        String runName,
        Long ruleGroupId,
        LocalDate periodStartDate,
        LocalDate periodEndDate,
        String createdBy
    ) {
        logger.info("Creating reconciliation run: {}", runName);

        // Validate rule group exists
        ReconciliationRuleGroup ruleGroup = ruleGroupRepository.findById(ruleGroupId)
            .orElseThrow(() -> new RuntimeException("Rule group not found: " + ruleGroupId));

        // Create run
        ReconciliationRun run = new ReconciliationRun();
        run.setRunName(runName);
        run.setRuleGroupId(ruleGroupId);
        run.setPeriodStartDate(periodStartDate);
        run.setPeriodEndDate(periodEndDate);
        run.setStatus("INITIATED");
        run.setCreatedBy(createdBy);
        run.setRunDate(LocalDateTime.now());

        run = runRepository.save(run);

        // Execute reconciliation
        run = reconciliationEngine.executeReconciliation(run);

        return run;
    }

    /**
     * Get all reconciliation runs
     */
    public List<ReconciliationRun> getAllRuns() {
        return runRepository.findAllOrderByRunDateDesc();
    }

    /**
     * Get reconciliation run by ID
     */
    public ReconciliationRun getRunById(Long runId) {
        return runRepository.findById(runId)
            .orElseThrow(() -> new RuntimeException("Reconciliation run not found: " + runId));
    }

    /**
     * Get matches for a run
     */
    public List<ReconciliationMatch> getMatchesForRun(Long runId) {
        return matchRepository.findByRunId(runId);
    }

    /**
     * Get exceptions for a run
     */
    public List<ReconciliationException> getExceptionsForRun(Long runId) {
        return exceptionRepository.findByRunId(runId);
    }

    /**
     * Get open exceptions
     */
    public List<ReconciliationException> getOpenExceptions() {
        return exceptionRepository.findByStatus("OPEN");
    }

    /**
     * Update exception status
     */
    @Transactional
    public ReconciliationException updateExceptionStatus(
        Long exceptionId,
        String status,
        String resolutionComments,
        String updatedBy
    ) {
        ReconciliationException exception = exceptionRepository.findById(exceptionId)
            .orElseThrow(() -> new RuntimeException("Exception not found: " + exceptionId));

        exception.setStatus(status);
        exception.setResolutionComments(resolutionComments);

        if ("RESOLVED".equals(status) || "CLOSED".equals(status)) {
            exception.setResolvedDate(LocalDateTime.now());
        }

        return exceptionRepository.save(exception);
    }
}
