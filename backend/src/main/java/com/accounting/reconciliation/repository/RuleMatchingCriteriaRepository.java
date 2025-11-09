package com.accounting.reconciliation.repository;

import com.accounting.reconciliation.model.RuleMatchingCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Rule Matching Criteria
 */
@Repository
public interface RuleMatchingCriteriaRepository extends JpaRepository<RuleMatchingCriteria, Long> {

    List<RuleMatchingCriteria> findByRuleId(Long ruleId);

    List<RuleMatchingCriteria> findByRuleIdOrderBySequenceNumberAsc(Long ruleId);
}
