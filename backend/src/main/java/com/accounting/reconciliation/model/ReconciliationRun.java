package com.accounting.reconciliation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Reconciliation Run Entity
 */
@Entity
@Table(name = "RECONCILIATION_RUN")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationRun {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recon_run_seq")
    @SequenceGenerator(name = "recon_run_seq", sequenceName = "SEQ_RECONCILIATION_RUN", allocationSize = 1)
    @Column(name = "RUN_ID")
    private Long runId;

    @Column(name = "RUN_NAME", nullable = false, length = 200)
    private String runName;

    @Column(name = "RULE_GROUP_ID", nullable = false)
    private Long ruleGroupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RULE_GROUP_ID", insertable = false, updatable = false)
    private ReconciliationRuleGroup ruleGroup;

    @Column(name = "RUN_DATE")
    private LocalDateTime runDate = LocalDateTime.now();

    @Column(name = "PERIOD_START_DATE", nullable = false)
    private LocalDate periodStartDate;

    @Column(name = "PERIOD_END_DATE", nullable = false)
    private LocalDate periodEndDate;

    @Column(name = "STATUS", length = 50)
    private String status = "INITIATED";

    @Column(name = "TOTAL_LEDGER_RECORDS")
    private Integer totalLedgerRecords = 0;

    @Column(name = "TOTAL_SUBLEDGER_RECORDS")
    private Integer totalSubledgerRecords = 0;

    @Column(name = "MATCHED_RECORDS")
    private Integer matchedRecords = 0;

    @Column(name = "UNMATCHED_LEDGER_RECORDS")
    private Integer unmatchedLedgerRecords = 0;

    @Column(name = "UNMATCHED_SUBLEDGER_RECORDS")
    private Integer unmatchedSubledgerRecords = 0;

    @Column(name = "EXCEPTION_RECORDS")
    private Integer exceptionRecords = 0;

    @Column(name = "TOTAL_MATCHED_AMOUNT", precision = 18, scale = 2)
    private BigDecimal totalMatchedAmount = BigDecimal.ZERO;

    @Column(name = "TOTAL_UNMATCHED_AMOUNT", precision = 18, scale = 2)
    private BigDecimal totalUnmatchedAmount = BigDecimal.ZERO;

    @Column(name = "VARIANCE_AMOUNT", precision = 18, scale = 2)
    private BigDecimal varianceAmount = BigDecimal.ZERO;

    @Column(name = "EXECUTION_START_TIME")
    private LocalDateTime executionStartTime;

    @Column(name = "EXECUTION_END_TIME")
    private LocalDateTime executionEndTime;

    @Column(name = "EXECUTION_DURATION_SECONDS")
    private Integer executionDurationSeconds;

    @Column(name = "COMMENTS", length = 1000)
    private String comments;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate = LocalDateTime.now();

    @OneToMany(mappedBy = "run", fetch = FetchType.LAZY)
    private List<ReconciliationMatch> matches;

    @OneToMany(mappedBy = "run", fetch = FetchType.LAZY)
    private List<ReconciliationException> exceptions;
}
