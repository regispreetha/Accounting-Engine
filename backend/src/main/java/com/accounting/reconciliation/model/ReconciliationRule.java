package com.accounting.reconciliation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Reconciliation Rule Entity
 */
@Entity
@Table(name = "RECONCILIATION_RULES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rules_seq")
    @SequenceGenerator(name = "rules_seq", sequenceName = "SEQ_RULES", allocationSize = 1)
    @Column(name = "RULE_ID")
    private Long ruleId;

    @Column(name = "RULE_GROUP_ID", nullable = false)
    private Long ruleGroupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RULE_GROUP_ID", insertable = false, updatable = false)
    private ReconciliationRuleGroup ruleGroup;

    @Column(name = "RULE_NAME", nullable = false, length = 200)
    private String ruleName;

    @Column(name = "RULE_CODE", nullable = false, unique = true, length = 50)
    private String ruleCode;

    @Column(name = "RULE_TYPE", nullable = false, length = 50)
    private String ruleType; // ONE_TO_ONE, ONE_TO_MANY, MANY_TO_ONE, MANY_TO_MANY

    @Column(name = "MATCHING_TYPE", nullable = false, length = 50)
    private String matchingType; // EXACT, TOLERANCE, CUSTOM

    @Column(name = "TOLERANCE_AMOUNT", precision = 18, scale = 2)
    private BigDecimal toleranceAmount;

    @Column(name = "TOLERANCE_PERCENTAGE", precision = 5, scale = 2)
    private BigDecimal tolerancePercentage;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

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

    @OneToMany(mappedBy = "rule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RuleMatchingCriteria> matchingCriteria;
}
