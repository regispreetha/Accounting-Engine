package com.accounting.reconciliation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Subledger Master Entity
 */
@Entity
@Table(name = "SUBLEDGER_MASTER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubledgerMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subledger_master_seq")
    @SequenceGenerator(name = "subledger_master_seq", sequenceName = "SEQ_SUBLEDGER_MASTER", allocationSize = 1)
    @Column(name = "SUBLEDGER_ID")
    private Long subledgerId;

    @Column(name = "SUBLEDGER_CODE", nullable = false, unique = true, length = 50)
    private String subledgerCode;

    @Column(name = "SUBLEDGER_NAME", nullable = false, length = 200)
    private String subledgerName;

    @Column(name = "SUBLEDGER_TYPE", nullable = false, length = 50)
    private String subledgerType;

    @Column(name = "PARENT_LEDGER_ID")
    private Long parentLedgerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_LEDGER_ID", insertable = false, updatable = false)
    private LedgerMaster parentLedger;

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
