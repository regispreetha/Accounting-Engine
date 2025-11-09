package com.accounting.reconciliation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Ledger Transaction Entity
 */
@Entity
@Table(name = "LEDGER_TRANSACTIONS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LedgerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ledger_trans_seq")
    @SequenceGenerator(name = "ledger_trans_seq", sequenceName = "SEQ_LEDGER_TRANSACTIONS", allocationSize = 1)
    @Column(name = "TRANSACTION_ID")
    private Long transactionId;

    @Column(name = "LEDGER_ID", nullable = false)
    private Long ledgerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEDGER_ID", insertable = false, updatable = false)
    private LedgerMaster ledger;

    @Column(name = "TRANSACTION_NUMBER", nullable = false, length = 100)
    private String transactionNumber;

    @Column(name = "TRANSACTION_DATE", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "POSTING_DATE", nullable = false)
    private LocalDate postingDate;

    @Column(name = "ACCOUNT_CODE", nullable = false, length = 50)
    private String accountCode;

    @Column(name = "COST_CENTER", length = 50)
    private String costCenter;

    @Column(name = "DEBIT_AMOUNT", precision = 18, scale = 2)
    private BigDecimal debitAmount = BigDecimal.ZERO;

    @Column(name = "CREDIT_AMOUNT", precision = 18, scale = 2)
    private BigDecimal creditAmount = BigDecimal.ZERO;

    @Column(name = "NET_AMOUNT", nullable = false, precision = 18, scale = 2)
    private BigDecimal netAmount;

    @Column(name = "CURRENCY_CODE", length = 10)
    private String currencyCode = "USD";

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "REFERENCE_NUMBER", length = 100)
    private String referenceNumber;

    @Column(name = "SOURCE_SYSTEM", length = 50)
    private String sourceSystem;

    @Column(name = "BATCH_ID", length = 100)
    private String batchId;

    @Column(name = "PERIOD_NAME", length = 50)
    private String periodName;

    @Column(name = "FISCAL_YEAR")
    private Integer fiscalYear;

    @Column(name = "RECONCILIATION_STATUS", length = 20)
    private String reconciliationStatus = "PENDING";

    @Column(name = "RECONCILIATION_ID")
    private Long reconciliationId;

    @Column(name = "IS_RECONCILED", length = 1)
    private String isReconciled = "N";

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate = LocalDateTime.now();
}
