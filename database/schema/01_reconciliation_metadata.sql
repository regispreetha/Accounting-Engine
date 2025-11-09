-- =====================================================
-- RECONCILIATION ENGINE - DATABASE SCHEMA
-- Oracle Database DDL Scripts
-- =====================================================

-- =====================================================
-- 1. LEDGER AND SUBLEDGER MASTER TABLES
-- =====================================================

-- Ledger Master Table
CREATE TABLE ledger_master (
    ledger_id NUMBER PRIMARY KEY,
    ledger_code VARCHAR2(50) NOT NULL UNIQUE,
    ledger_name VARCHAR2(200) NOT NULL,
    ledger_type VARCHAR2(50) NOT NULL, -- GL, AP, AR, INVENTORY, etc.
    description VARCHAR2(500),
    is_active CHAR(1) DEFAULT 'Y' CHECK (is_active IN ('Y', 'N')),
    created_by VARCHAR2(100),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR2(100),
    modified_date TIMESTAMP,
    CONSTRAINT chk_ledger_type CHECK (ledger_type IN ('GL', 'AP', 'AR', 'INVENTORY', 'CASH', 'BANK', 'FIXED_ASSET', 'PAYROLL'))
);

-- Subledger Master Table
CREATE TABLE subledger_master (
    subledger_id NUMBER PRIMARY KEY,
    subledger_code VARCHAR2(50) NOT NULL UNIQUE,
    subledger_name VARCHAR2(200) NOT NULL,
    subledger_type VARCHAR2(50) NOT NULL,
    parent_ledger_id NUMBER,
    description VARCHAR2(500),
    is_active CHAR(1) DEFAULT 'Y' CHECK (is_active IN ('Y', 'N')),
    created_by VARCHAR2(100),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR2(100),
    modified_date TIMESTAMP,
    CONSTRAINT fk_subledger_ledger FOREIGN KEY (parent_ledger_id) REFERENCES ledger_master(ledger_id)
);

-- =====================================================
-- 2. LEDGER AND SUBLEDGER TRANSACTION TABLES
-- =====================================================

-- Ledger Transactions
CREATE TABLE ledger_transactions (
    transaction_id NUMBER PRIMARY KEY,
    ledger_id NUMBER NOT NULL,
    transaction_number VARCHAR2(100) NOT NULL,
    transaction_date DATE NOT NULL,
    posting_date DATE NOT NULL,
    account_code VARCHAR2(50) NOT NULL,
    cost_center VARCHAR2(50),
    debit_amount NUMBER(18,2) DEFAULT 0,
    credit_amount NUMBER(18,2) DEFAULT 0,
    net_amount NUMBER(18,2) NOT NULL,
    currency_code VARCHAR2(10) DEFAULT 'USD',
    description VARCHAR2(500),
    reference_number VARCHAR2(100),
    source_system VARCHAR2(50),
    batch_id VARCHAR2(100),
    period_name VARCHAR2(50),
    fiscal_year NUMBER(4),
    reconciliation_status VARCHAR2(20) DEFAULT 'PENDING',
    reconciliation_id NUMBER,
    is_reconciled CHAR(1) DEFAULT 'N' CHECK (is_reconciled IN ('Y', 'N')),
    created_by VARCHAR2(100),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ledger_trans_ledger FOREIGN KEY (ledger_id) REFERENCES ledger_master(ledger_id),
    CONSTRAINT chk_recon_status CHECK (reconciliation_status IN ('PENDING', 'MATCHED', 'UNMATCHED', 'PARTIALLY_MATCHED', 'EXCEPTION'))
);

-- Subledger Transactions
CREATE TABLE subledger_transactions (
    transaction_id NUMBER PRIMARY KEY,
    subledger_id NUMBER NOT NULL,
    transaction_number VARCHAR2(100) NOT NULL,
    transaction_date DATE NOT NULL,
    posting_date DATE NOT NULL,
    account_code VARCHAR2(50) NOT NULL,
    cost_center VARCHAR2(50),
    debit_amount NUMBER(18,2) DEFAULT 0,
    credit_amount NUMBER(18,2) DEFAULT 0,
    net_amount NUMBER(18,2) NOT NULL,
    currency_code VARCHAR2(10) DEFAULT 'USD',
    description VARCHAR2(500),
    reference_number VARCHAR2(100),
    source_system VARCHAR2(50),
    batch_id VARCHAR2(100),
    vendor_id VARCHAR2(50),
    customer_id VARCHAR2(50),
    invoice_number VARCHAR2(100),
    period_name VARCHAR2(50),
    fiscal_year NUMBER(4),
    reconciliation_status VARCHAR2(20) DEFAULT 'PENDING',
    reconciliation_id NUMBER,
    is_reconciled CHAR(1) DEFAULT 'N' CHECK (is_reconciled IN ('Y', 'N')),
    created_by VARCHAR2(100),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_subledger_trans_subledger FOREIGN KEY (subledger_id) REFERENCES subledger_master(subledger_id),
    CONSTRAINT chk_subledger_recon_status CHECK (reconciliation_status IN ('PENDING', 'MATCHED', 'UNMATCHED', 'PARTIALLY_MATCHED', 'EXCEPTION'))
);

-- =====================================================
-- 3. RECONCILIATION RULES ENGINE TABLES
-- =====================================================

-- Reconciliation Rule Groups
CREATE TABLE reconciliation_rule_groups (
    rule_group_id NUMBER PRIMARY KEY,
    rule_group_name VARCHAR2(200) NOT NULL UNIQUE,
    description VARCHAR2(500),
    ledger_id NUMBER NOT NULL,
    subledger_id NUMBER NOT NULL,
    is_active CHAR(1) DEFAULT 'Y' CHECK (is_active IN ('Y', 'N')),
    priority NUMBER DEFAULT 1,
    created_by VARCHAR2(100),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR2(100),
    modified_date TIMESTAMP,
    CONSTRAINT fk_rule_group_ledger FOREIGN KEY (ledger_id) REFERENCES ledger_master(ledger_id),
    CONSTRAINT fk_rule_group_subledger FOREIGN KEY (subledger_id) REFERENCES subledger_master(subledger_id)
);

-- Reconciliation Rules
CREATE TABLE reconciliation_rules (
    rule_id NUMBER PRIMARY KEY,
    rule_group_id NUMBER NOT NULL,
    rule_name VARCHAR2(200) NOT NULL,
    rule_code VARCHAR2(50) NOT NULL UNIQUE,
    rule_type VARCHAR2(50) NOT NULL, -- ONE_TO_ONE, ONE_TO_MANY, MANY_TO_ONE, MANY_TO_MANY
    matching_type VARCHAR2(50) NOT NULL, -- EXACT, TOLERANCE, CUSTOM
    tolerance_amount NUMBER(18,2),
    tolerance_percentage NUMBER(5,2),
    description VARCHAR2(500),
    is_active CHAR(1) DEFAULT 'Y' CHECK (is_active IN ('Y', 'N')),
    priority NUMBER DEFAULT 1,
    created_by VARCHAR2(100),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR2(100),
    modified_date TIMESTAMP,
    CONSTRAINT fk_rule_rule_group FOREIGN KEY (rule_group_id) REFERENCES reconciliation_rule_groups(rule_group_id),
    CONSTRAINT chk_rule_type CHECK (rule_type IN ('ONE_TO_ONE', 'ONE_TO_MANY', 'MANY_TO_ONE', 'MANY_TO_MANY')),
    CONSTRAINT chk_matching_type CHECK (matching_type IN ('EXACT', 'TOLERANCE', 'CUSTOM', 'DATE_RANGE', 'AMOUNT_RANGE'))
);

-- Rule Matching Criteria
CREATE TABLE rule_matching_criteria (
    criteria_id NUMBER PRIMARY KEY,
    rule_id NUMBER NOT NULL,
    field_name VARCHAR2(100) NOT NULL, -- transaction_date, amount, account_code, reference_number, etc.
    operator VARCHAR2(20) NOT NULL, -- EQUALS, NOT_EQUALS, GREATER_THAN, LESS_THAN, BETWEEN, CONTAINS, etc.
    value_source VARCHAR2(50) NOT NULL, -- LEDGER, SUBLEDGER
    comparison_field VARCHAR2(100), -- Field to compare from other source
    static_value VARCHAR2(500), -- Static value for comparison
    is_mandatory CHAR(1) DEFAULT 'Y' CHECK (is_mandatory IN ('Y', 'N')),
    sequence_number NUMBER,
    logical_operator VARCHAR2(10), -- AND, OR
    created_by VARCHAR2(100),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_criteria_rule FOREIGN KEY (rule_id) REFERENCES reconciliation_rules(rule_id),
    CONSTRAINT chk_operator CHECK (operator IN ('EQUALS', 'NOT_EQUALS', 'GREATER_THAN', 'LESS_THAN', 'GREATER_EQUAL', 'LESS_EQUAL', 'BETWEEN', 'CONTAINS', 'STARTS_WITH', 'ENDS_WITH', 'IN', 'NOT_IN')),
    CONSTRAINT chk_logical_operator CHECK (logical_operator IN ('AND', 'OR'))
);

-- =====================================================
-- 4. RECONCILIATION EXECUTION TABLES
-- =====================================================

-- Reconciliation Run Header
CREATE TABLE reconciliation_run (
    run_id NUMBER PRIMARY KEY,
    run_name VARCHAR2(200) NOT NULL,
    rule_group_id NUMBER NOT NULL,
    run_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    period_start_date DATE NOT NULL,
    period_end_date DATE NOT NULL,
    status VARCHAR2(50) DEFAULT 'INITIATED',
    total_ledger_records NUMBER DEFAULT 0,
    total_subledger_records NUMBER DEFAULT 0,
    matched_records NUMBER DEFAULT 0,
    unmatched_ledger_records NUMBER DEFAULT 0,
    unmatched_subledger_records NUMBER DEFAULT 0,
    exception_records NUMBER DEFAULT 0,
    total_matched_amount NUMBER(18,2) DEFAULT 0,
    total_unmatched_amount NUMBER(18,2) DEFAULT 0,
    variance_amount NUMBER(18,2) DEFAULT 0,
    execution_start_time TIMESTAMP,
    execution_end_time TIMESTAMP,
    execution_duration_seconds NUMBER,
    comments VARCHAR2(1000),
    created_by VARCHAR2(100),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_run_rule_group FOREIGN KEY (rule_group_id) REFERENCES reconciliation_rule_groups(rule_group_id),
    CONSTRAINT chk_run_status CHECK (status IN ('INITIATED', 'IN_PROGRESS', 'COMPLETED', 'FAILED', 'CANCELLED'))
);

-- Reconciliation Matches
CREATE TABLE reconciliation_matches (
    match_id NUMBER PRIMARY KEY,
    run_id NUMBER NOT NULL,
    rule_id NUMBER,
    match_type VARCHAR2(50) NOT NULL, -- ONE_TO_ONE, ONE_TO_MANY, MANY_TO_ONE, MANY_TO_MANY
    match_status VARCHAR2(50) DEFAULT 'MATCHED',
    ledger_amount NUMBER(18,2),
    subledger_amount NUMBER(18,2),
    variance_amount NUMBER(18,2),
    match_confidence NUMBER(3) DEFAULT 100, -- Percentage 0-100
    match_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR2(100),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_match_run FOREIGN KEY (run_id) REFERENCES reconciliation_run(run_id),
    CONSTRAINT fk_match_rule FOREIGN KEY (rule_id) REFERENCES reconciliation_rules(rule_id),
    CONSTRAINT chk_match_type CHECK (match_type IN ('ONE_TO_ONE', 'ONE_TO_MANY', 'MANY_TO_ONE', 'MANY_TO_MANY')),
    CONSTRAINT chk_match_status CHECK (match_status IN ('MATCHED', 'PARTIALLY_MATCHED', 'UNMATCHED', 'EXCEPTION', 'MANUAL'))
);

-- Reconciliation Match Details (linking transactions)
CREATE TABLE reconciliation_match_details (
    match_detail_id NUMBER PRIMARY KEY,
    match_id NUMBER NOT NULL,
    ledger_transaction_id NUMBER,
    subledger_transaction_id NUMBER,
    matched_amount NUMBER(18,2),
    variance_amount NUMBER(18,2),
    created_by VARCHAR2(100),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_match_detail_match FOREIGN KEY (match_id) REFERENCES reconciliation_matches(match_id),
    CONSTRAINT fk_match_detail_ledger FOREIGN KEY (ledger_transaction_id) REFERENCES ledger_transactions(transaction_id),
    CONSTRAINT fk_match_detail_subledger FOREIGN KEY (subledger_transaction_id) REFERENCES subledger_transactions(transaction_id)
);

-- =====================================================
-- 5. EXCEPTION AND ADJUSTMENT TABLES
-- =====================================================

-- Reconciliation Exceptions
CREATE TABLE reconciliation_exceptions (
    exception_id NUMBER PRIMARY KEY,
    run_id NUMBER NOT NULL,
    exception_type VARCHAR2(50) NOT NULL, -- UNMATCHED_LEDGER, UNMATCHED_SUBLEDGER, VARIANCE, DATA_QUALITY
    ledger_transaction_id NUMBER,
    subledger_transaction_id NUMBER,
    exception_description VARCHAR2(1000),
    exception_amount NUMBER(18,2),
    severity VARCHAR2(20) DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, CRITICAL
    status VARCHAR2(50) DEFAULT 'OPEN',
    assigned_to VARCHAR2(100),
    resolution_comments VARCHAR2(1000),
    resolved_date TIMESTAMP,
    created_by VARCHAR2(100),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_exception_run FOREIGN KEY (run_id) REFERENCES reconciliation_run(run_id),
    CONSTRAINT fk_exception_ledger FOREIGN KEY (ledger_transaction_id) REFERENCES ledger_transactions(transaction_id),
    CONSTRAINT fk_exception_subledger FOREIGN KEY (subledger_transaction_id) REFERENCES subledger_transactions(transaction_id),
    CONSTRAINT chk_exception_type CHECK (exception_type IN ('UNMATCHED_LEDGER', 'UNMATCHED_SUBLEDGER', 'VARIANCE', 'DATA_QUALITY', 'DUPLICATE', 'MISSING_DATA')),
    CONSTRAINT chk_severity CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    CONSTRAINT chk_exception_status CHECK (status IN ('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED', 'WAIVED'))
);

-- Manual Adjustments
CREATE TABLE reconciliation_adjustments (
    adjustment_id NUMBER PRIMARY KEY,
    run_id NUMBER NOT NULL,
    exception_id NUMBER,
    adjustment_type VARCHAR2(50) NOT NULL,
    ledger_transaction_id NUMBER,
    subledger_transaction_id NUMBER,
    adjustment_amount NUMBER(18,2) NOT NULL,
    adjustment_description VARCHAR2(1000),
    justification VARCHAR2(1000),
    status VARCHAR2(50) DEFAULT 'PENDING',
    approved_by VARCHAR2(100),
    approval_date TIMESTAMP,
    posted_date TIMESTAMP,
    created_by VARCHAR2(100),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_adjustment_run FOREIGN KEY (run_id) REFERENCES reconciliation_run(run_id),
    CONSTRAINT fk_adjustment_exception FOREIGN KEY (exception_id) REFERENCES reconciliation_exceptions(exception_id),
    CONSTRAINT chk_adjustment_type CHECK (adjustment_type IN ('LEDGER_ADJUSTMENT', 'SUBLEDGER_ADJUSTMENT', 'RECLASSIFICATION', 'REVERSAL', 'MANUAL_MATCH')),
    CONSTRAINT chk_adjustment_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'POSTED', 'CANCELLED'))
);

-- =====================================================
-- 6. AUDIT AND CONFIGURATION TABLES
-- =====================================================

-- Reconciliation Audit Trail
CREATE TABLE reconciliation_audit_log (
    audit_id NUMBER PRIMARY KEY,
    run_id NUMBER,
    table_name VARCHAR2(100),
    record_id NUMBER,
    action_type VARCHAR2(50) NOT NULL,
    old_value CLOB,
    new_value CLOB,
    performed_by VARCHAR2(100),
    performed_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR2(50),
    session_id VARCHAR2(100),
    CONSTRAINT chk_action_type CHECK (action_type IN ('INSERT', 'UPDATE', 'DELETE', 'MATCH', 'UNMATCH', 'APPROVE', 'REJECT'))
);

-- System Configuration
CREATE TABLE reconciliation_config (
    config_id NUMBER PRIMARY KEY,
    config_key VARCHAR2(100) NOT NULL UNIQUE,
    config_value VARCHAR2(500) NOT NULL,
    config_type VARCHAR2(50),
    description VARCHAR2(500),
    is_active CHAR(1) DEFAULT 'Y' CHECK (is_active IN ('Y', 'N')),
    created_by VARCHAR2(100),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR2(100),
    modified_date TIMESTAMP
);

-- =====================================================
-- 7. SEQUENCES FOR PRIMARY KEYS
-- =====================================================

CREATE SEQUENCE seq_ledger_master START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_subledger_master START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_ledger_transactions START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_subledger_transactions START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_rule_groups START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_rules START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_matching_criteria START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_reconciliation_run START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_recon_matches START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_match_details START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_exceptions START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_adjustments START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_audit_log START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_config START WITH 1 INCREMENT BY 1;

-- =====================================================
-- 8. INDEXES FOR PERFORMANCE
-- =====================================================

-- Ledger Transactions Indexes
CREATE INDEX idx_ledger_trans_ledger_id ON ledger_transactions(ledger_id);
CREATE INDEX idx_ledger_trans_date ON ledger_transactions(transaction_date);
CREATE INDEX idx_ledger_trans_posting_date ON ledger_transactions(posting_date);
CREATE INDEX idx_ledger_trans_account ON ledger_transactions(account_code);
CREATE INDEX idx_ledger_trans_reference ON ledger_transactions(reference_number);
CREATE INDEX idx_ledger_trans_status ON ledger_transactions(reconciliation_status);
CREATE INDEX idx_ledger_trans_recon_id ON ledger_transactions(reconciliation_id);
CREATE INDEX idx_ledger_trans_period ON ledger_transactions(period_name, fiscal_year);

-- Subledger Transactions Indexes
CREATE INDEX idx_subledger_trans_subledger_id ON subledger_transactions(subledger_id);
CREATE INDEX idx_subledger_trans_date ON subledger_transactions(transaction_date);
CREATE INDEX idx_subledger_trans_posting_date ON subledger_transactions(posting_date);
CREATE INDEX idx_subledger_trans_account ON subledger_transactions(account_code);
CREATE INDEX idx_subledger_trans_reference ON subledger_transactions(reference_number);
CREATE INDEX idx_subledger_trans_status ON subledger_transactions(reconciliation_status);
CREATE INDEX idx_subledger_trans_invoice ON subledger_transactions(invoice_number);
CREATE INDEX idx_subledger_trans_period ON subledger_transactions(period_name, fiscal_year);

-- Reconciliation Indexes
CREATE INDEX idx_recon_run_status ON reconciliation_run(status);
CREATE INDEX idx_recon_run_date ON reconciliation_run(run_date);
CREATE INDEX idx_match_run_id ON reconciliation_matches(run_id);
CREATE INDEX idx_match_status ON reconciliation_matches(match_status);
CREATE INDEX idx_exception_run_id ON reconciliation_exceptions(run_id);
CREATE INDEX idx_exception_status ON reconciliation_exceptions(status);
CREATE INDEX idx_exception_severity ON reconciliation_exceptions(severity);

COMMIT;
