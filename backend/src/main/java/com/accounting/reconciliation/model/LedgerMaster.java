package com.accounting.reconciliation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Ledger Master Entity
 */
@Entity
@Table(name = "LEDGER_MASTER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LedgerMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ledger_master_seq")
    @SequenceGenerator(name = "ledger_master_seq", sequenceName = "SEQ_LEDGER_MASTER", allocationSize = 1)
    @Column(name = "LEDGER_ID")
    private Long ledgerId;

    @Column(name = "LEDGER_CODE", nullable = false, unique = true, length = 50)
    private String ledgerCode;

    @Column(name = "LEDGER_NAME", nullable = false, length = 200)
    private String ledgerName;

    @Column(name = "LEDGER_TYPE", nullable = false, length = 50)
    private String ledgerType;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "IS_ACTIVE", length = 1)
    private String isActive = "Y";

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "MODIFIED_BY", length = 100)
    private String modifiedBy;

    @Column(name = "MODIFIED_DATE")
    private LocalDateTime modifiedDate;
}
