# Reconciliation Engine

A comprehensive ledger and subledger reconciliation system designed for accounting and finance operations.

## Overview

The Reconciliation Engine provides an end-to-end solution for automating the reconciliation process between ledger and subledger transactions. It features a flexible rules engine, configurable matching criteria, exception management, and comprehensive reporting capabilities.

## Architecture

### Technology Stack

- **Frontend**: AngularJS 1.8.3
- **Backend**: Java 17 with Spring Boot 3.1.5
- **Database**: Oracle Database
- **Build Tool**: Maven

### System Components

1. **Database Layer** (Oracle)
   - Ledger and Subledger master and transaction tables
   - Rules engine metadata
   - Reconciliation run tracking
   - Exception and audit management

2. **Backend Layer** (Java/Spring Boot)
   - RESTful API services
   - Reconciliation engine core
   - Rules engine for flexible matching
   - Transaction and exception management

3. **Frontend Layer** (AngularJS)
   - Dashboard for monitoring
   - Rules builder UI
   - Reconciliation execution interface
   - Exception management console

## Features

### Core Features

- **Flexible Rules Engine**: Create and manage reconciliation rules with multiple matching types:
  - One-to-One matching
  - One-to-Many matching
  - Many-to-One matching
  - Many-to-Many matching

- **Matching Types**:
  - Exact match
  - Tolerance-based matching (amount and percentage)
  - Custom criteria-based matching

- **Exception Management**:
  - Automatic exception detection
  - Severity classification
  - Status tracking and resolution workflow

- **Comprehensive Reporting**:
  - Real-time reconciliation status
  - Match and exception details
  - Variance analysis

### Key Capabilities

- Multi-ledger and multi-subledger support
- Configurable matching criteria
- Period-based reconciliation
- Audit trail for all operations
- User-friendly interface for non-technical users

## Database Schema

The system uses a comprehensive metadata model with the following key tables:

### Master Tables
- `LEDGER_MASTER`: Ledger definitions
- `SUBLEDGER_MASTER`: Subledger definitions

### Transaction Tables
- `LEDGER_TRANSACTIONS`: Ledger transaction data
- `SUBLEDGER_TRANSACTIONS`: Subledger transaction data

### Rules Tables
- `RECONCILIATION_RULE_GROUPS`: Rule group configurations
- `RECONCILIATION_RULES`: Individual reconciliation rules
- `RULE_MATCHING_CRITERIA`: Detailed matching criteria

### Execution Tables
- `RECONCILIATION_RUN`: Reconciliation run header
- `RECONCILIATION_MATCHES`: Matched transactions
- `RECONCILIATION_MATCH_DETAILS`: Match detail linkage
- `RECONCILIATION_EXCEPTIONS`: Unmatched transactions

## Installation

### Prerequisites

- Java 17 or higher
- Oracle Database 12c or higher
- Node.js and npm (for frontend development)
- Maven 3.6 or higher

### Database Setup

1. Connect to your Oracle database
2. Execute the schema creation script:
   ```sql
   @database/schema/01_reconciliation_metadata.sql
   ```
3. Load sample data (optional):
   ```sql
   @database/schema/02_sample_data.sql
   ```

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Update `src/main/resources/application.properties` with your database credentials:
   ```properties
   spring.datasource.url=jdbc:oracle:thin:@<host>:<port>:<sid>
   spring.datasource.username=<username>
   spring.datasource.password=<password>
   ```

3. Build the application:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The backend API will be available at `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

The frontend application will be available at `http://localhost:8081`

## Usage

### 1. Create Rule Groups

1. Navigate to the "Rules Builder" page
2. Click "New Group"
3. Enter rule group details:
   - Group name
   - Ledger ID
   - Subledger ID
   - Description
4. Click "Create"

### 2. Create Reconciliation Rules

1. Select a rule group
2. Click "New Rule"
3. Configure the rule:
   - Rule name and code
   - Rule type (One-to-One, One-to-Many, etc.)
   - Matching type (Exact, Tolerance, Custom)
   - Tolerance amount/percentage if applicable
4. Click "Create"

### 3. Define Matching Criteria

1. Select a rule
2. Click "New Criteria"
3. Define criteria:
   - Field name (e.g., net_amount, reference_number)
   - Operator (Equals, Greater Than, Contains, etc.)
   - Value source (Ledger or Subledger)
   - Comparison field
   - Logical operator (AND/OR)
4. Click "Create"

### 4. Execute Reconciliation

1. Navigate to "Execute Reconciliation"
2. Enter run parameters:
   - Run name
   - Select rule group
   - Period start and end dates
   - Your name (created by)
3. Click "Execute Reconciliation"
4. View results or navigate to the run details page

### 5. Manage Exceptions

1. Navigate to "Exception Management"
2. Filter exceptions by status (Open, In Progress, Resolved)
3. Click "Resolve" on any exception
4. Update status and add resolution comments
5. Save changes

## API Documentation

The API documentation is available via Swagger UI when the backend is running:

**URL**: `http://localhost:8080/swagger-ui.html`

### Key Endpoints

#### Rules Management
- `GET /api/rules/groups` - Get all rule groups
- `POST /api/rules/groups` - Create rule group
- `GET /api/rules/groups/{id}/rules` - Get rules for a group
- `POST /api/rules` - Create rule
- `POST /api/rules/{id}/criteria` - Create matching criteria

#### Reconciliation
- `POST /api/reconciliation/execute` - Execute reconciliation
- `GET /api/reconciliation/runs` - Get all runs
- `GET /api/reconciliation/runs/{id}` - Get run details
- `GET /api/reconciliation/runs/{id}/matches` - Get matches
- `GET /api/reconciliation/runs/{id}/exceptions` - Get exceptions

#### Exception Management
- `GET /api/reconciliation/exceptions/open` - Get open exceptions
- `PUT /api/reconciliation/exceptions/{id}` - Update exception

## Configuration

### Backend Configuration

Edit `backend/src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:ORCL
spring.datasource.username=recon_user
spring.datasource.password=recon_pass

# Reconciliation Engine Configuration
reconciliation.batch.size=1000
reconciliation.max.parallel.threads=5
reconciliation.default.tolerance.percentage=0.01
reconciliation.auto.match.threshold=100
```

### Frontend Configuration

Edit `frontend/js/app.js`:

```javascript
app.constant('API_CONFIG', {
    baseUrl: 'http://localhost:8080/api'
});
```

## Project Structure

```
Accounting-Engine/
├── database/
│   └── schema/
│       ├── 01_reconciliation_metadata.sql
│       └── 02_sample_data.sql
├── backend/
│   ├── src/main/java/com/accounting/reconciliation/
│   │   ├── ReconciliationEngineApplication.java
│   │   ├── config/
│   │   ├── controller/
│   │   ├── engine/
│   │   │   ├── ReconciliationEngine.java
│   │   │   └── RulesEngine.java
│   │   ├── model/
│   │   ├── repository/
│   │   └── service/
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
└── frontend/
    ├── index.html
    ├── package.json
    ├── js/
    │   ├── app.js
    │   ├── controllers/
    │   └── services/
    ├── views/
    └── css/
```

## Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Verify Oracle database is running
   - Check connection parameters in application.properties
   - Ensure Oracle JDBC driver is properly loaded

2. **CORS Error**
   - Verify CORS configuration in CorsConfig.java
   - Check that frontend URL is in allowed origins

3. **Frontend Not Loading**
   - Ensure backend is running on port 8080
   - Check API_CONFIG.baseUrl in app.js
   - Verify all JavaScript files are loaded in index.html

4. **Rule Execution Fails**
   - Check that rule group has active rules
   - Verify matching criteria are properly configured
   - Check transaction date ranges match the reconciliation period

## Contributing

1. Create a feature branch
2. Make your changes
3. Test thoroughly
4. Submit a pull request

## License

Copyright (c) 2025 Reconciliation Engine Team. All rights reserved.

## Support

For questions or issues, please contact the development team or create an issue in the project repository.

## Version History

- **v1.0.0** (2025-01-09)
  - Initial release
  - Core reconciliation engine
  - Rules builder UI
  - Exception management
  - Dashboard and reporting
