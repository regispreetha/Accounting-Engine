package com.accounting.reconciliation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Application Class for Reconciliation Engine
 *
 * @author Reconciliation Engine Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
public class ReconciliationEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReconciliationEngineApplication.class, args);
    }
}
