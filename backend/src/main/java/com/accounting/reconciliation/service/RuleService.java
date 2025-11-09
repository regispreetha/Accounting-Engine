package com.accounting.reconciliation.service;

import com.accounting.reconciliation.model.ReconciliationRule;
import com.accounting.reconciliation.model.ReconciliationRuleGroup;
import com.accounting.reconciliation.model.RuleMatchingCriteria;
import com.accounting.reconciliation.repository.ReconciliationRuleGroupRepository;
import com.accounting.reconciliation.repository.ReconciliationRuleRepository;
import com.accounting.reconciliation.repository.RuleMatchingCriteriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for Reconciliation Rules management
 */
@Service
public class RuleService {

    @Autowired
    private ReconciliationRuleGroupRepository ruleGroupRepository;

    @Autowired
    private ReconciliationRuleRepository ruleRepository;

    @Autowired
    private RuleMatchingCriteriaRepository criteriaRepository;

    /**
     * Get all rule groups
     */
    public List<ReconciliationRuleGroup> getAllRuleGroups() {
        return ruleGroupRepository.findAll();
    }

    /**
     * Get active rule groups
     */
    public List<ReconciliationRuleGroup> getActiveRuleGroups() {
        return ruleGroupRepository.findByIsActive("Y");
    }

    /**
     * Get rule group by ID
     */
    public ReconciliationRuleGroup getRuleGroupById(Long ruleGroupId) {
        return ruleGroupRepository.findById(ruleGroupId)
            .orElseThrow(() -> new RuntimeException("Rule group not found: " + ruleGroupId));
    }

    /**
     * Create rule group
     */
    @Transactional
    public ReconciliationRuleGroup createRuleGroup(ReconciliationRuleGroup ruleGroup) {
        ruleGroup.setCreatedDate(LocalDateTime.now());
        return ruleGroupRepository.save(ruleGroup);
    }

    /**
     * Update rule group
     */
    @Transactional
    public ReconciliationRuleGroup updateRuleGroup(Long ruleGroupId, ReconciliationRuleGroup ruleGroup) {
        ReconciliationRuleGroup existing = getRuleGroupById(ruleGroupId);
        existing.setRuleGroupName(ruleGroup.getRuleGroupName());
        existing.setDescription(ruleGroup.getDescription());
        existing.setIsActive(ruleGroup.getIsActive());
        existing.setPriority(ruleGroup.getPriority());
        existing.setModifiedDate(LocalDateTime.now());
        existing.setModifiedBy(ruleGroup.getModifiedBy());
        return ruleGroupRepository.save(existing);
    }

    /**
     * Get all rules
     */
    public List<ReconciliationRule> getAllRules() {
        return ruleRepository.findAll();
    }

    /**
     * Get rules by rule group
     */
    public List<ReconciliationRule> getRulesByGroupId(Long ruleGroupId) {
        return ruleRepository.findByRuleGroupId(ruleGroupId);
    }

    /**
     * Get rule by ID
     */
    public ReconciliationRule getRuleById(Long ruleId) {
        return ruleRepository.findById(ruleId)
            .orElseThrow(() -> new RuntimeException("Rule not found: " + ruleId));
    }

    /**
     * Create rule
     */
    @Transactional
    public ReconciliationRule createRule(ReconciliationRule rule) {
        rule.setCreatedDate(LocalDateTime.now());
        return ruleRepository.save(rule);
    }

    /**
     * Update rule
     */
    @Transactional
    public ReconciliationRule updateRule(Long ruleId, ReconciliationRule rule) {
        ReconciliationRule existing = getRuleById(ruleId);
        existing.setRuleName(rule.getRuleName());
        existing.setRuleType(rule.getRuleType());
        existing.setMatchingType(rule.getMatchingType());
        existing.setToleranceAmount(rule.getToleranceAmount());
        existing.setTolerancePercentage(rule.getTolerancePercentage());
        existing.setDescription(rule.getDescription());
        existing.setIsActive(rule.getIsActive());
        existing.setPriority(rule.getPriority());
        existing.setModifiedDate(LocalDateTime.now());
        existing.setModifiedBy(rule.getModifiedBy());
        return ruleRepository.save(existing);
    }

    /**
     * Delete rule
     */
    @Transactional
    public void deleteRule(Long ruleId) {
        ruleRepository.deleteById(ruleId);
    }

    /**
     * Get matching criteria for a rule
     */
    public List<RuleMatchingCriteria> getCriteriaByRuleId(Long ruleId) {
        return criteriaRepository.findByRuleIdOrderBySequenceNumberAsc(ruleId);
    }

    /**
     * Create matching criteria
     */
    @Transactional
    public RuleMatchingCriteria createCriteria(RuleMatchingCriteria criteria) {
        criteria.setCreatedDate(LocalDateTime.now());
        return criteriaRepository.save(criteria);
    }

    /**
     * Delete matching criteria
     */
    @Transactional
    public void deleteCriteria(Long criteriaId) {
        criteriaRepository.deleteById(criteriaId);
    }
}
