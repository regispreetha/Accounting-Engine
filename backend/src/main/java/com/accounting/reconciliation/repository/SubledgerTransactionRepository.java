package com.accounting.reconciliation.repository;

import com.accounting.reconciliation.model.SubledgerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Subledger Transactions
 */
@Repository
public interface SubledgerTransactionRepository extends JpaRepository<SubledgerTransaction, Long> {

    List<SubledgerTransaction> findBySubledgerId(Long subledgerId);

    List<SubledgerTransaction> findByReconciliationStatus(String status);

    @Query("SELECT st FROM SubledgerTransaction st WHERE st.subledgerId = :subledgerId " +
           "AND st.transactionDate BETWEEN :startDate AND :endDate " +
           "AND st.reconciliationStatus = :status")
    List<SubledgerTransaction> findUnreconciledTransactions(
        @Param("subledgerId") Long subledgerId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("status") String status
    );

    @Query("SELECT st FROM SubledgerTransaction st WHERE st.subledgerId = :subledgerId " +
           "AND st.transactionDate BETWEEN :startDate AND :endDate " +
           "AND st.isReconciled = 'N'")
    List<SubledgerTransaction> findPendingReconciliation(
        @Param("subledgerId") Long subledgerId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    List<SubledgerTransaction> findByReferenceNumber(String referenceNumber);

    List<SubledgerTransaction> findByInvoiceNumber(String invoiceNumber);

    List<SubledgerTransaction> findByAccountCode(String accountCode);
}
