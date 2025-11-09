package com.accounting.reconciliation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Reconciliation Match Entity
 */
@Entity
@Table(name = "RECONCILIATION_MATCHES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recon_matches_seq")
    @SequenceGenerator(name = "recon_matches_seq", sequenceName = "SEQ_RECON_MATCHES", allocationSize = 1)
    @Column(name = "MATCH_ID")
    private Long matchId;

    @Column(name = "RUN_ID", nullable = false)
    private Long runId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RUN_ID", insertable = false, updatable = false)
    private ReconciliationRun run;

    @Column(name = "RULE_ID")
    private Long ruleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RULE_ID", insertable = false, updatable = false)
    private ReconciliationRule rule;

    @Column(name = "MATCH_TYPE", nullable = false, length = 50)
    private String matchType;

    @Column(name = "MATCH_STATUS", length = 50)
    private String matchStatus = "MATCHED";

    @Column(name = "LEDGER_AMOUNT", precision = 18, scale = 2)
    private BigDecimal ledgerAmount;

    @Column(name = "SUBLEDGER_AMOUNT", precision = 18, scale = 2)
    private BigDecimal subledgerAmount;

    @Column(name = "VARIANCE_AMOUNT", precision = 18, scale = 2)
    private BigDecimal varianceAmount;

    @Column(name = "MATCH_CONFIDENCE")
    private Integer matchConfidence = 100;

    @Column(name = "MATCH_DATE")
    private LocalDateTime matchDate = LocalDateTime.now();

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate = LocalDateTime.now();

    @OneToMany(mappedBy = "match", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ReconciliationMatchDetail> matchDetails;
}
