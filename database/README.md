# Database Schema Documentation

## Overview

This directory contains the Oracle database schema for the Reconciliation Engine.

## Files

### 01_reconciliation_metadata.sql

Main schema definition file containing:

- **Master Tables**: Ledger and Subledger definitions
- **Transaction Tables**: Ledger and Subledger transaction data
- **Rules Tables**: Reconciliation rules and matching criteria
- **Execution Tables**: Reconciliation run tracking and results
- **Exception Tables**: Exception and adjustment management
- **Audit Tables**: Audit trail and configuration
- **Sequences**: Auto-increment sequences for primary keys
- **Indexes**: Performance optimization indexes

### 02_sample_data.sql

Sample data for testing and demonstration:

- Sample ledger and subledger masters
- Sample transactions
- Example rule groups and rules
- Example matching criteria
- System configuration data

## Installation

1. Connect to Oracle database as a user with CREATE privileges

2. Execute the main schema script:
```sql
@01_reconciliation_metadata.sql
```

3. Optionally load sample data:
```sql
@02_sample_data.sql
```

## Schema Objects

### Tables (20)

1. `LEDGER_MASTER` - Ledger definitions
2. `SUBLEDGER_MASTER` - Subledger definitions
3. `LEDGER_TRANSACTIONS` - Ledger transaction data
4. `SUBLEDGER_TRANSACTIONS` - Subledger transaction data
5. `RECONCILIATION_RULE_GROUPS` - Rule group configurations
6. `RECONCILIATION_RULES` - Individual rules
7. `RULE_MATCHING_CRITERIA` - Matching criteria
8. `RECONCILIATION_RUN` - Reconciliation run header
9. `RECONCILIATION_MATCHES` - Match records
10. `RECONCILIATION_MATCH_DETAILS` - Match details
11. `RECONCILIATION_EXCEPTIONS` - Exception records
12. `RECONCILIATION_ADJUSTMENTS` - Manual adjustments
13. `RECONCILIATION_AUDIT_LOG` - Audit trail
14. `RECONCILIATION_CONFIG` - System configuration

### Sequences (14)

- One sequence per table for primary key generation

### Indexes (18)

- Performance indexes on frequently queried columns

## Entity Relationships

### Core Entities

```
LEDGER_MASTER
    └── LEDGER_TRANSACTIONS
            └── RECONCILIATION_MATCH_DETAILS

SUBLEDGER_MASTER
    └── SUBLEDGER_TRANSACTIONS
            └── RECONCILIATION_MATCH_DETAILS

RECONCILIATION_RULE_GROUPS
    └── RECONCILIATION_RULES
            └── RULE_MATCHING_CRITERIA
            └── RECONCILIATION_MATCHES
```

### Reconciliation Process Flow

```
RECONCILIATION_RUN
    ├── RECONCILIATION_MATCHES
    │   └── RECONCILIATION_MATCH_DETAILS
    └── RECONCILIATION_EXCEPTIONS
            └── RECONCILIATION_ADJUSTMENTS
```

## Data Dictionary

### Key Fields

#### Transaction Status Values

- `PENDING` - Not yet reconciled
- `MATCHED` - Successfully matched
- `UNMATCHED` - No match found
- `PARTIALLY_MATCHED` - Partial match
- `EXCEPTION` - Exception raised

#### Reconciliation Run Status

- `INITIATED` - Run created
- `IN_PROGRESS` - Currently executing
- `COMPLETED` - Successfully completed
- `FAILED` - Execution failed
- `CANCELLED` - Manually cancelled

#### Exception Types

- `UNMATCHED_LEDGER` - Ledger transaction not matched
- `UNMATCHED_SUBLEDGER` - Subledger transaction not matched
- `VARIANCE` - Amount variance detected
- `DATA_QUALITY` - Data quality issue
- `DUPLICATE` - Duplicate transaction
- `MISSING_DATA` - Required data missing

#### Severity Levels

- `LOW` - Minor issue
- `MEDIUM` - Moderate issue
- `HIGH` - Significant issue
- `CRITICAL` - Critical issue requiring immediate attention

## Maintenance

### Backup Recommendations

- Daily backup of transaction tables
- Weekly backup of configuration tables
- Full backup before major reconciliation runs

### Performance Tuning

- Regularly analyze and rebuild indexes
- Monitor table statistics
- Archive old reconciliation runs
- Partition large transaction tables by date

### Cleanup Scripts

Archive old reconciliation runs (older than 1 year):
```sql
-- Archive completed runs older than 1 year
DELETE FROM RECONCILIATION_MATCH_DETAILS
WHERE MATCH_ID IN (
    SELECT MATCH_ID FROM RECONCILIATION_MATCHES
    WHERE RUN_ID IN (
        SELECT RUN_ID FROM RECONCILIATION_RUN
        WHERE STATUS = 'COMPLETED'
        AND RUN_DATE < ADD_MONTHS(SYSDATE, -12)
    )
);

DELETE FROM RECONCILIATION_MATCHES
WHERE RUN_ID IN (
    SELECT RUN_ID FROM RECONCILIATION_RUN
    WHERE STATUS = 'COMPLETED'
    AND RUN_DATE < ADD_MONTHS(SYSDATE, -12)
);

DELETE FROM RECONCILIATION_EXCEPTIONS
WHERE RUN_ID IN (
    SELECT RUN_ID FROM RECONCILIATION_RUN
    WHERE STATUS = 'COMPLETED'
    AND RUN_DATE < ADD_MONTHS(SYSDATE, -12)
);

DELETE FROM RECONCILIATION_RUN
WHERE STATUS = 'COMPLETED'
AND RUN_DATE < ADD_MONTHS(SYSDATE, -12);

COMMIT;
```

## Security

### Recommended Privileges

Create a dedicated database user for the application:

```sql
CREATE USER recon_user IDENTIFIED BY <password>;

GRANT CONNECT, RESOURCE TO recon_user;
GRANT CREATE SEQUENCE TO recon_user;
GRANT CREATE TABLE TO recon_user;
GRANT CREATE VIEW TO recon_user;

-- Grant specific privileges on tables
GRANT SELECT, INSERT, UPDATE, DELETE ON LEDGER_MASTER TO recon_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON SUBLEDGER_MASTER TO recon_user;
-- ... grant on all tables

-- Limit DELETE on critical tables
REVOKE DELETE ON RECONCILIATION_AUDIT_LOG FROM recon_user;
```

## Monitoring

### Key Metrics to Monitor

- Transaction table growth rate
- Average reconciliation run time
- Exception rate trends
- Match confidence distribution
- Variance amounts over time

### Useful Queries

Get reconciliation statistics:
```sql
SELECT
    STATUS,
    COUNT(*) as RUN_COUNT,
    AVG(MATCHED_RECORDS) as AVG_MATCHED,
    AVG(EXCEPTION_RECORDS) as AVG_EXCEPTIONS,
    AVG(EXECUTION_DURATION_SECONDS) as AVG_DURATION
FROM RECONCILIATION_RUN
WHERE RUN_DATE >= TRUNC(SYSDATE) - 30
GROUP BY STATUS;
```

Get top exceptions:
```sql
SELECT
    EXCEPTION_TYPE,
    SEVERITY,
    COUNT(*) as COUNT,
    SUM(EXCEPTION_AMOUNT) as TOTAL_AMOUNT
FROM RECONCILIATION_EXCEPTIONS
WHERE STATUS = 'OPEN'
GROUP BY EXCEPTION_TYPE, SEVERITY
ORDER BY COUNT DESC;
```

## Version History

- **v1.0.0** (2025-01-09) - Initial schema release
