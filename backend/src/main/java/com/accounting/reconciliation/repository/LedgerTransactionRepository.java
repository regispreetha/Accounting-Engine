package com.accounting.reconciliation.repository;

import com.accounting.reconciliation.model.LedgerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Ledger Transactions
 */
@Repository
public interface LedgerTransactionRepository extends JpaRepository<LedgerTransaction, Long> {

    List<LedgerTransaction> findByLedgerId(Long ledgerId);

    List<LedgerTransaction> findByReconciliationStatus(String status);

    @Query("SELECT lt FROM LedgerTransaction lt WHERE lt.ledgerId = :ledgerId " +
           "AND lt.transactionDate BETWEEN :startDate AND :endDate " +
           "AND lt.reconciliationStatus = :status")
    List<LedgerTransaction> findUnreconciledTransactions(
        @Param("ledgerId") Long ledgerId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("status") String status
    );

    @Query("SELECT lt FROM LedgerTransaction lt WHERE lt.ledgerId = :ledgerId " +
           "AND lt.transactionDate BETWEEN :startDate AND :endDate " +
           "AND lt.isReconciled = 'N'")
    List<LedgerTransaction> findPendingReconciliation(
        @Param("ledgerId") Long ledgerId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    List<LedgerTransaction> findByReferenceNumber(String referenceNumber);

    List<LedgerTransaction> findByAccountCode(String accountCode);
}
