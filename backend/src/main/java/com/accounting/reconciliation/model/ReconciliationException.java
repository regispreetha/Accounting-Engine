package com.accounting.reconciliation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Reconciliation Exception Entity
 */
@Entity
@Table(name = "RECONCILIATION_EXCEPTIONS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationException {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exceptions_seq")
    @SequenceGenerator(name = "exceptions_seq", sequenceName = "SEQ_EXCEPTIONS", allocationSize = 1)
    @Column(name = "EXCEPTION_ID")
    private Long exceptionId;

    @Column(name = "RUN_ID", nullable = false)
    private Long runId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RUN_ID", insertable = false, updatable = false)
    private ReconciliationRun run;

    @Column(name = "EXCEPTION_TYPE", nullable = false, length = 50)
    private String exceptionType;

    @Column(name = "LEDGER_TRANSACTION_ID")
    private Long ledgerTransactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEDGER_TRANSACTION_ID", insertable = false, updatable = false)
    private LedgerTransaction ledgerTransaction;

    @Column(name = "SUBLEDGER_TRANSACTION_ID")
    private Long subledgerTransactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBLEDGER_TRANSACTION_ID", insertable = false, updatable = false)
    private SubledgerTransaction subledgerTransaction;

    @Column(name = "EXCEPTION_DESCRIPTION", length = 1000)
    private String exceptionDescription;

    @Column(name = "EXCEPTION_AMOUNT", precision = 18, scale = 2)
    private BigDecimal exceptionAmount;

    @Column(name = "SEVERITY", length = 20)
    private String severity = "MEDIUM";

    @Column(name = "STATUS", length = 50)
    private String status = "OPEN";

    @Column(name = "ASSIGNED_TO", length = 100)
    private String assignedTo;

    @Column(name = "RESOLUTION_COMMENTS", length = 1000)
    private String resolutionComments;

    @Column(name = "RESOLVED_DATE")
    private LocalDateTime resolvedDate;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate = LocalDateTime.now();
}
