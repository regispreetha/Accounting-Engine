package com.accounting.reconciliation.repository;

import com.accounting.reconciliation.model.ReconciliationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Reconciliation Rules
 */
@Repository
public interface ReconciliationRuleRepository extends JpaRepository<ReconciliationRule, Long> {

    Optional<ReconciliationRule> findByRuleCode(String ruleCode);

    List<ReconciliationRule> findByRuleGroupId(Long ruleGroupId);

    @Query("SELECT r FROM ReconciliationRule r " +
           "WHERE r.ruleGroupId = :ruleGroupId AND r.isActive = 'Y' " +
           "ORDER BY r.priority ASC")
    List<ReconciliationRule> findActiveRulesByGroupId(@Param("ruleGroupId") Long ruleGroupId);

    List<ReconciliationRule> findByIsActive(String isActive);

    @Query("SELECT r FROM ReconciliationRule r " +
           "JOIN FETCH r.matchingCriteria " +
           "WHERE r.ruleId = :ruleId")
    Optional<ReconciliationRule> findByIdWithCriteria(@Param("ruleId") Long ruleId);
}
