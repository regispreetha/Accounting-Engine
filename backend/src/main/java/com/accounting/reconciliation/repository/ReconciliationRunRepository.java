package com.accounting.reconciliation.repository;

import com.accounting.reconciliation.model.ReconciliationRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Reconciliation Runs
 */
@Repository
public interface ReconciliationRunRepository extends JpaRepository<ReconciliationRun, Long> {

    List<ReconciliationRun> findByStatus(String status);

    List<ReconciliationRun> findByRuleGroupId(Long ruleGroupId);

    @Query("SELECT rr FROM ReconciliationRun rr " +
           "WHERE rr.runDate BETWEEN :startDate AND :endDate " +
           "ORDER BY rr.runDate DESC")
    List<ReconciliationRun> findRunsBetweenDates(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT rr FROM ReconciliationRun rr " +
           "ORDER BY rr.runDate DESC")
    List<ReconciliationRun> findAllOrderByRunDateDesc();

    @Query("SELECT rr FROM ReconciliationRun rr " +
           "WHERE rr.status = :status " +
           "ORDER BY rr.runDate DESC")
    List<ReconciliationRun> findByStatusOrderByRunDateDesc(@Param("status") String status);
}
