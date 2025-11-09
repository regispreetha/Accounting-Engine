package com.accounting.reconciliation.repository;

import com.accounting.reconciliation.model.ReconciliationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Reconciliation Exceptions
 */
@Repository
public interface ReconciliationExceptionRepository extends JpaRepository<ReconciliationException, Long> {

    List<ReconciliationException> findByRunId(Long runId);

    List<ReconciliationException> findByStatus(String status);

    List<ReconciliationException> findBySeverity(String severity);

    List<ReconciliationException> findByRunIdAndStatus(Long runId, String status);

    List<ReconciliationException> findByExceptionType(String exceptionType);
}
