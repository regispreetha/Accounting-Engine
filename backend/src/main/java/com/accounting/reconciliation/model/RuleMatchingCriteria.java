package com.accounting.reconciliation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Rule Matching Criteria Entity
 */
@Entity
@Table(name = "RULE_MATCHING_CRITERIA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleMatchingCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "matching_criteria_seq")
    @SequenceGenerator(name = "matching_criteria_seq", sequenceName = "SEQ_MATCHING_CRITERIA", allocationSize = 1)
    @Column(name = "CRITERIA_ID")
    private Long criteriaId;

    @Column(name = "RULE_ID", nullable = false)
    private Long ruleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RULE_ID", insertable = false, updatable = false)
    private ReconciliationRule rule;

    @Column(name = "FIELD_NAME", nullable = false, length = 100)
    private String fieldName;

    @Column(name = "OPERATOR", nullable = false, length = 20)
    private String operator; // EQUALS, NOT_EQUALS, GREATER_THAN, etc.

    @Column(name = "VALUE_SOURCE", nullable = false, length = 50)
    private String valueSource; // LEDGER, SUBLEDGER

    @Column(name = "COMPARISON_FIELD", length = 100)
    private String comparisonField;

    @Column(name = "STATIC_VALUE", length = 500)
    private String staticValue;

    @Column(name = "IS_MANDATORY", length = 1)
    private String isMandatory = "Y";

    @Column(name = "SEQUENCE_NUMBER")
    private Integer sequenceNumber;

    @Column(name = "LOGICAL_OPERATOR", length = 10)
    private String logicalOperator; // AND, OR

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate = LocalDateTime.now();
}
