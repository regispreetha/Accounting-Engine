package com.accounting.reconciliation.repository;

import com.accounting.reconciliation.model.ReconciliationMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Reconciliation Matches
 */
@Repository
public interface ReconciliationMatchRepository extends JpaRepository<ReconciliationMatch, Long> {

    List<ReconciliationMatch> findByRunId(Long runId);

    List<ReconciliationMatch> findByRuleId(Long ruleId);

    List<ReconciliationMatch> findByMatchStatus(String matchStatus);

    List<ReconciliationMatch> findByRunIdAndMatchStatus(Long runId, String matchStatus);
}
