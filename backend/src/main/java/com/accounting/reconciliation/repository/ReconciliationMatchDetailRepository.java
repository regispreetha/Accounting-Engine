package com.accounting.reconciliation.repository;

import com.accounting.reconciliation.model.ReconciliationMatchDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Reconciliation Match Details
 */
@Repository
public interface ReconciliationMatchDetailRepository extends JpaRepository<ReconciliationMatchDetail, Long> {

    List<ReconciliationMatchDetail> findByMatchId(Long matchId);

    List<ReconciliationMatchDetail> findByLedgerTransactionId(Long ledgerTransactionId);

    List<ReconciliationMatchDetail> findBySubledgerTransactionId(Long subledgerTransactionId);
}
