package com.accounting.reconciliation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Reconciliation Match Detail Entity
 */
@Entity
@Table(name = "RECONCILIATION_MATCH_DETAILS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationMatchDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "match_details_seq")
    @SequenceGenerator(name = "match_details_seq", sequenceName = "SEQ_MATCH_DETAILS", allocationSize = 1)
    @Column(name = "MATCH_DETAIL_ID")
    private Long matchDetailId;

    @Column(name = "MATCH_ID", nullable = false)
    private Long matchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MATCH_ID", insertable = false, updatable = false)
    private ReconciliationMatch match;

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

    @Column(name = "MATCHED_AMOUNT", precision = 18, scale = 2)
    private BigDecimal matchedAmount;

    @Column(name = "VARIANCE_AMOUNT", precision = 18, scale = 2)
    private BigDecimal varianceAmount;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate = LocalDateTime.now();
}
