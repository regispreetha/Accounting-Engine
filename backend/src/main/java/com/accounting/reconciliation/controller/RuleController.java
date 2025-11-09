package com.accounting.reconciliation.controller;

import com.accounting.reconciliation.model.ReconciliationRule;
import com.accounting.reconciliation.model.ReconciliationRuleGroup;
import com.accounting.reconciliation.model.RuleMatchingCriteria;
import com.accounting.reconciliation.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Reconciliation Rules management
 */
@RestController
@RequestMapping("/api/rules")
@CrossOrigin(origins = "*")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    // ========== Rule Group Endpoints ==========

    /**
     * Get all rule groups
     */
    @GetMapping("/groups")
    public ResponseEntity<List<ReconciliationRuleGroup>> getAllRuleGroups() {
        List<ReconciliationRuleGroup> ruleGroups = ruleService.getAllRuleGroups();
        return ResponseEntity.ok(ruleGroups);
    }

    /**
     * Get active rule groups
     */
    @GetMapping("/groups/active")
    public ResponseEntity<List<ReconciliationRuleGroup>> getActiveRuleGroups() {
        List<ReconciliationRuleGroup> ruleGroups = ruleService.getActiveRuleGroups();
        return ResponseEntity.ok(ruleGroups);
    }

    /**
     * Get rule group by ID
     */
    @GetMapping("/groups/{ruleGroupId}")
    public ResponseEntity<ReconciliationRuleGroup> getRuleGroupById(@PathVariable Long ruleGroupId) {
        ReconciliationRuleGroup ruleGroup = ruleService.getRuleGroupById(ruleGroupId);
        return ResponseEntity.ok(ruleGroup);
    }

    /**
     * Create rule group
     */
    @PostMapping("/groups")
    public ResponseEntity<ReconciliationRuleGroup> createRuleGroup(
        @RequestBody ReconciliationRuleGroup ruleGroup
    ) {
        ReconciliationRuleGroup created = ruleService.createRuleGroup(ruleGroup);
        return ResponseEntity.ok(created);
    }

    /**
     * Update rule group
     */
    @PutMapping("/groups/{ruleGroupId}")
    public ResponseEntity<ReconciliationRuleGroup> updateRuleGroup(
        @PathVariable Long ruleGroupId,
        @RequestBody ReconciliationRuleGroup ruleGroup
    ) {
        ReconciliationRuleGroup updated = ruleService.updateRuleGroup(ruleGroupId, ruleGroup);
        return ResponseEntity.ok(updated);
    }

    // ========== Rule Endpoints ==========

    /**
     * Get all rules
     */
    @GetMapping
    public ResponseEntity<List<ReconciliationRule>> getAllRules() {
        List<ReconciliationRule> rules = ruleService.getAllRules();
        return ResponseEntity.ok(rules);
    }

    /**
     * Get rules by rule group
     */
    @GetMapping("/groups/{ruleGroupId}/rules")
    public ResponseEntity<List<ReconciliationRule>> getRulesByGroupId(@PathVariable Long ruleGroupId) {
        List<ReconciliationRule> rules = ruleService.getRulesByGroupId(ruleGroupId);
        return ResponseEntity.ok(rules);
    }

    /**
     * Get rule by ID
     */
    @GetMapping("/{ruleId}")
    public ResponseEntity<ReconciliationRule> getRuleById(@PathVariable Long ruleId) {
        ReconciliationRule rule = ruleService.getRuleById(ruleId);
        return ResponseEntity.ok(rule);
    }

    /**
     * Create rule
     */
    @PostMapping
    public ResponseEntity<ReconciliationRule> createRule(@RequestBody ReconciliationRule rule) {
        ReconciliationRule created = ruleService.createRule(rule);
        return ResponseEntity.ok(created);
    }

    /**
     * Update rule
     */
    @PutMapping("/{ruleId}")
    public ResponseEntity<ReconciliationRule> updateRule(
        @PathVariable Long ruleId,
        @RequestBody ReconciliationRule rule
    ) {
        ReconciliationRule updated = ruleService.updateRule(ruleId, rule);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete rule
     */
    @DeleteMapping("/{ruleId}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long ruleId) {
        ruleService.deleteRule(ruleId);
        return ResponseEntity.ok().build();
    }

    // ========== Matching Criteria Endpoints ==========

    /**
     * Get matching criteria for a rule
     */
    @GetMapping("/{ruleId}/criteria")
    public ResponseEntity<List<RuleMatchingCriteria>> getCriteriaByRuleId(@PathVariable Long ruleId) {
        List<RuleMatchingCriteria> criteria = ruleService.getCriteriaByRuleId(ruleId);
        return ResponseEntity.ok(criteria);
    }

    /**
     * Create matching criteria
     */
    @PostMapping("/{ruleId}/criteria")
    public ResponseEntity<RuleMatchingCriteria> createCriteria(
        @PathVariable Long ruleId,
        @RequestBody RuleMatchingCriteria criteria
    ) {
        criteria.setRuleId(ruleId);
        RuleMatchingCriteria created = ruleService.createCriteria(criteria);
        return ResponseEntity.ok(created);
    }

    /**
     * Delete matching criteria
     */
    @DeleteMapping("/criteria/{criteriaId}")
    public ResponseEntity<Void> deleteCriteria(@PathVariable Long criteriaId) {
        ruleService.deleteCriteria(criteriaId);
        return ResponseEntity.ok().build();
    }
}
