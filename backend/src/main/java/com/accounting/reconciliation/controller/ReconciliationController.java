package com.accounting.reconciliation.controller;

import com.accounting.reconciliation.model.ReconciliationException;
import com.accounting.reconciliation.model.ReconciliationMatch;
import com.accounting.reconciliation.model.ReconciliationRun;
import com.accounting.reconciliation.service.ReconciliationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Reconciliation operations
 */
@RestController
@RequestMapping("/api/reconciliation")
@CrossOrigin(origins = "*")
public class ReconciliationController {

    @Autowired
    private ReconciliationService reconciliationService;

    /**
     * Create and execute a new reconciliation run
     */
    @PostMapping("/execute")
    public ResponseEntity<ReconciliationRun> executeReconciliation(
        @RequestBody Map<String, Object> request
    ) {
        String runName = (String) request.get("runName");
        Long ruleGroupId = Long.valueOf(request.get("ruleGroupId").toString());
        LocalDate periodStartDate = LocalDate.parse((String) request.get("periodStartDate"));
        LocalDate periodEndDate = LocalDate.parse((String) request.get("periodEndDate"));
        String createdBy = (String) request.getOrDefault("createdBy", "SYSTEM");

        ReconciliationRun run = reconciliationService.createAndExecuteReconciliation(
            runName, ruleGroupId, periodStartDate, periodEndDate, createdBy
        );

        return ResponseEntity.ok(run);
    }

    /**
     * Get all reconciliation runs
     */
    @GetMapping("/runs")
    public ResponseEntity<List<ReconciliationRun>> getAllRuns() {
        List<ReconciliationRun> runs = reconciliationService.getAllRuns();
        return ResponseEntity.ok(runs);
    }

    /**
     * Get reconciliation run by ID
     */
    @GetMapping("/runs/{runId}")
    public ResponseEntity<ReconciliationRun> getRunById(@PathVariable Long runId) {
        ReconciliationRun run = reconciliationService.getRunById(runId);
        return ResponseEntity.ok(run);
    }

    /**
     * Get matches for a reconciliation run
     */
    @GetMapping("/runs/{runId}/matches")
    public ResponseEntity<List<ReconciliationMatch>> getMatchesForRun(@PathVariable Long runId) {
        List<ReconciliationMatch> matches = reconciliationService.getMatchesForRun(runId);
        return ResponseEntity.ok(matches);
    }

    /**
     * Get exceptions for a reconciliation run
     */
    @GetMapping("/runs/{runId}/exceptions")
    public ResponseEntity<List<ReconciliationException>> getExceptionsForRun(@PathVariable Long runId) {
        List<ReconciliationException> exceptions = reconciliationService.getExceptionsForRun(runId);
        return ResponseEntity.ok(exceptions);
    }

    /**
     * Get all open exceptions
     */
    @GetMapping("/exceptions/open")
    public ResponseEntity<List<ReconciliationException>> getOpenExceptions() {
        List<ReconciliationException> exceptions = reconciliationService.getOpenExceptions();
        return ResponseEntity.ok(exceptions);
    }

    /**
     * Update exception status
     */
    @PutMapping("/exceptions/{exceptionId}")
    public ResponseEntity<ReconciliationException> updateExceptionStatus(
        @PathVariable Long exceptionId,
        @RequestBody Map<String, String> request
    ) {
        String status = request.get("status");
        String resolutionComments = request.get("resolutionComments");
        String updatedBy = request.getOrDefault("updatedBy", "SYSTEM");

        ReconciliationException exception = reconciliationService.updateExceptionStatus(
            exceptionId, status, resolutionComments, updatedBy
        );

        return ResponseEntity.ok(exception);
    }

    /**
     * Get reconciliation statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        // Implementation for dashboard statistics
        return ResponseEntity.ok(Map.of(
            "totalRuns", reconciliationService.getAllRuns().size(),
            "openExceptions", reconciliationService.getOpenExceptions().size()
        ));
    }
}
