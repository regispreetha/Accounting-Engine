package com.accounting.reconciliation.repository;

import com.accounting.reconciliation.model.ReconciliationRuleGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Reconciliation Rule Groups
 */
@Repository
public interface ReconciliationRuleGroupRepository extends JpaRepository<ReconciliationRuleGroup, Long> {

    Optional<ReconciliationRuleGroup> findByRuleGroupName(String ruleGroupName);

    List<ReconciliationRuleGroup> findByIsActive(String isActive);

    @Query("SELECT rg FROM ReconciliationRuleGroup rg " +
           "WHERE rg.ledgerId = :ledgerId AND rg.subledgerId = :subledgerId " +
           "AND rg.isActive = 'Y'")
    List<ReconciliationRuleGroup> findActiveRuleGroups(
        @Param("ledgerId") Long ledgerId,
        @Param("subledgerId") Long subledgerId
    );

    List<ReconciliationRuleGroup> findByLedgerId(Long ledgerId);

    List<ReconciliationRuleGroup> findBySubledgerId(Long subledgerId);
}
