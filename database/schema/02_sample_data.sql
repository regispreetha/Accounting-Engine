-- =====================================================
-- SAMPLE DATA FOR RECONCILIATION ENGINE
-- =====================================================

-- Insert Ledger Masters
INSERT INTO ledger_master (ledger_id, ledger_code, ledger_name, ledger_type, description, is_active, created_by)
VALUES (seq_ledger_master.NEXTVAL, 'GL001', 'General Ledger', 'GL', 'Main General Ledger', 'Y', 'SYSTEM');

INSERT INTO ledger_master (ledger_id, ledger_code, ledger_name, ledger_type, description, is_active, created_by)
VALUES (seq_ledger_master.NEXTVAL, 'AP001', 'Accounts Payable Ledger', 'AP', 'Accounts Payable Main Ledger', 'Y', 'SYSTEM');

INSERT INTO ledger_master (ledger_id, ledger_code, ledger_name, ledger_type, description, is_active, created_by)
VALUES (seq_ledger_master.NEXTVAL, 'AR001', 'Accounts Receivable Ledger', 'AR', 'Accounts Receivable Main Ledger', 'Y', 'SYSTEM');

-- Insert Subledger Masters
INSERT INTO subledger_master (subledger_id, subledger_code, subledger_name, subledger_type, parent_ledger_id, description, is_active, created_by)
VALUES (seq_subledger_master.NEXTVAL, 'AP_SUB001', 'AP Vendor Subledger', 'AP', 2, 'Vendor Invoice Subledger', 'Y', 'SYSTEM');

INSERT INTO subledger_master (subledger_id, subledger_code, subledger_name, subledger_type, parent_ledger_id, description, is_active, created_by)
VALUES (seq_subledger_master.NEXTVAL, 'AR_SUB001', 'AR Customer Subledger', 'AR', 3, 'Customer Invoice Subledger', 'Y', 'SYSTEM');

-- Insert Sample Ledger Transactions
INSERT INTO ledger_transactions (transaction_id, ledger_id, transaction_number, transaction_date, posting_date, account_code, debit_amount, credit_amount, net_amount, description, reference_number, period_name, fiscal_year, created_by)
VALUES (seq_ledger_transactions.NEXTVAL, 2, 'LT-2025-001', TO_DATE('2025-01-15', 'YYYY-MM-DD'), TO_DATE('2025-01-15', 'YYYY-MM-DD'), '2000', 0, 5000, -5000, 'Vendor Payment', 'REF-001', 'JAN-2025', 2025, 'SYSTEM');

INSERT INTO ledger_transactions (transaction_id, ledger_id, transaction_number, transaction_date, posting_date, account_code, debit_amount, credit_amount, net_amount, description, reference_number, period_name, fiscal_year, created_by)
VALUES (seq_ledger_transactions.NEXTVAL, 2, 'LT-2025-002', TO_DATE('2025-01-16', 'YYYY-MM-DD'), TO_DATE('2025-01-16', 'YYYY-MM-DD'), '2000', 0, 3000, -3000, 'Vendor Payment', 'REF-002', 'JAN-2025', 2025, 'SYSTEM');

-- Insert Sample Subledger Transactions
INSERT INTO subledger_transactions (transaction_id, subledger_id, transaction_number, transaction_date, posting_date, account_code, debit_amount, credit_amount, net_amount, description, reference_number, invoice_number, period_name, fiscal_year, created_by)
VALUES (seq_subledger_transactions.NEXTVAL, 1, 'ST-2025-001', TO_DATE('2025-01-15', 'YYYY-MM-DD'), TO_DATE('2025-01-15', 'YYYY-MM-DD'), '2000', 0, 5000, -5000, 'Vendor Invoice', 'REF-001', 'INV-001', 'JAN-2025', 2025, 'SYSTEM');

INSERT INTO subledger_transactions (transaction_id, subledger_id, transaction_number, transaction_date, posting_date, account_code, debit_amount, credit_amount, net_amount, description, reference_number, invoice_number, period_name, fiscal_year, created_by)
VALUES (seq_subledger_transactions.NEXTVAL, 1, 'ST-2025-002', TO_DATE('2025-01-16', 'YYYY-MM-DD'), TO_DATE('2025-01-16', 'YYYY-MM-DD'), '2000', 0, 3000, -3000, 'Vendor Invoice', 'REF-002', 'INV-002', 'JAN-2025', 2025, 'SYSTEM');

-- Insert Reconciliation Rule Group
INSERT INTO reconciliation_rule_groups (rule_group_id, rule_group_name, description, ledger_id, subledger_id, is_active, priority, created_by)
VALUES (seq_rule_groups.NEXTVAL, 'AP Ledger to Subledger Reconciliation', 'Reconcile AP Ledger with AP Subledger', 2, 1, 'Y', 1, 'SYSTEM');

-- Insert Reconciliation Rules
INSERT INTO reconciliation_rules (rule_id, rule_group_id, rule_name, rule_code, rule_type, matching_type, description, is_active, priority, created_by)
VALUES (seq_rules.NEXTVAL, 1, 'Exact Amount and Reference Match', 'RULE_001', 'ONE_TO_ONE', 'EXACT', 'Match based on exact amount and reference number', 'Y', 1, 'SYSTEM');

INSERT INTO reconciliation_rules (rule_id, rule_group_id, rule_name, rule_code, rule_type, matching_type, tolerance_amount, description, is_active, priority, created_by)
VALUES (seq_rules.NEXTVAL, 1, 'Amount with Tolerance Match', 'RULE_002', 'ONE_TO_ONE', 'TOLERANCE', 10, 'Match with tolerance of 10 units', 'Y', 2, 'SYSTEM');

-- Insert Rule Matching Criteria
INSERT INTO rule_matching_criteria (criteria_id, rule_id, field_name, operator, value_source, comparison_field, is_mandatory, sequence_number, logical_operator, created_by)
VALUES (seq_matching_criteria.NEXTVAL, 1, 'net_amount', 'EQUALS', 'LEDGER', 'net_amount', 'Y', 1, 'AND', 'SYSTEM');

INSERT INTO rule_matching_criteria (criteria_id, rule_id, field_name, operator, value_source, comparison_field, is_mandatory, sequence_number, logical_operator, created_by)
VALUES (seq_matching_criteria.NEXTVAL, 1, 'reference_number', 'EQUALS', 'LEDGER', 'reference_number', 'Y', 2, 'AND', 'SYSTEM');

-- Insert System Configuration
INSERT INTO reconciliation_config (config_id, config_key, config_value, config_type, description, created_by)
VALUES (seq_config.NEXTVAL, 'MAX_MATCH_RECORDS_PER_RUN', '100000', 'SYSTEM', 'Maximum records to process per reconciliation run', 'SYSTEM');

INSERT INTO reconciliation_config (config_id, config_key, config_value, config_type, description, created_by)
VALUES (seq_config.NEXTVAL, 'AUTO_MATCH_THRESHOLD', '100', 'SYSTEM', 'Auto-match confidence threshold percentage', 'SYSTEM');

INSERT INTO reconciliation_config (config_id, config_key, config_value, config_type, description, created_by)
VALUES (seq_config.NEXTVAL, 'DEFAULT_TOLERANCE_PERCENTAGE', '0.01', 'SYSTEM', 'Default tolerance percentage for matching', 'SYSTEM');

COMMIT;
