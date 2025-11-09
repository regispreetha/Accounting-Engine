package com.accounting.reconciliation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Reconciliation Rule Group Entity
 */
@Entity
@Table(name = "RECONCILIATION_RULE_GROUPS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationRuleGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rule_group_seq")
    @SequenceGenerator(name = "rule_group_seq", sequenceName = "SEQ_RULE_GROUPS", allocationSize = 1)
    @Column(name = "RULE_GROUP_ID")
    private Long ruleGroupId;

    @Column(name = "RULE_GROUP_NAME", nullable = false, unique = true, length = 200)
    private String ruleGroupName;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "LEDGER_ID", nullable = false)
    private Long ledgerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEDGER_ID", insertable = false, updatable = false)
    private LedgerMaster ledger;

    @Column(name = "SUBLEDGER_ID", nullable = false)
    private Long subledgerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBLEDGER_ID", insertable = false, updatable = false)
    private SubledgerMaster subledger;

    @Column(name = "IS_ACTIVE", length = 1)
    private String isActive = "Y";

    @Column(name = "PRIORITY")
    private Integer priority = 1;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "MODIFIED_BY", length = 100)
    private String modifiedBy;

    @Column(name = "MODIFIED_DATE")
    private LocalDateTime modifiedDate;

    @OneToMany(mappedBy = "ruleGroup", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ReconciliationRule> rules;
}
