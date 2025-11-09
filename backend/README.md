# Reconciliation Engine - Backend

## Overview

Java Spring Boot backend service providing REST APIs for the Reconciliation Engine.

## Technology Stack

- **Java**: 17
- **Framework**: Spring Boot 3.1.5
- **ORM**: Spring Data JPA with Hibernate
- **Database**: Oracle JDBC
- **Build Tool**: Maven
- **API Documentation**: SpringDoc OpenAPI (Swagger)

## Project Structure

```
backend/
├── src/main/java/com/accounting/reconciliation/
│   ├── ReconciliationEngineApplication.java   # Main application class
│   ├── config/
│   │   └── CorsConfig.java                     # CORS configuration
│   ├── controller/
│   │   ├── ReconciliationController.java       # Reconciliation APIs
│   │   └── RuleController.java                 # Rules management APIs
│   ├── engine/
│   │   ├── ReconciliationEngine.java           # Core reconciliation logic
│   │   └── RulesEngine.java                    # Rules evaluation engine
│   ├── model/
│   │   ├── LedgerMaster.java
│   │   ├── SubledgerMaster.java
│   │   ├── LedgerTransaction.java
│   │   ├── SubledgerTransaction.java
│   │   ├── ReconciliationRuleGroup.java
│   │   ├── ReconciliationRule.java
│   │   ├── RuleMatchingCriteria.java
│   │   ├── ReconciliationRun.java
│   │   ├── ReconciliationMatch.java
│   │   ├── ReconciliationMatchDetail.java
│   │   └── ReconciliationException.java
│   ├── repository/
│   │   └── [JPA Repositories]
│   └── service/
│       ├── ReconciliationService.java
│       └── RuleService.java
├── src/main/resources/
│   └── application.properties                  # Configuration
└── pom.xml                                     # Maven dependencies
```

## Setup and Installation

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Oracle Database 12c+

### Configuration

Edit `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:oracle:thin:@<host>:<port>:<sid>
spring.datasource.username=<username>
spring.datasource.password=<password>

# Server Port
server.port=8080

# Reconciliation Engine Settings
reconciliation.batch.size=1000
reconciliation.max.parallel.threads=5
reconciliation.default.tolerance.percentage=0.01
```

### Build

```bash
mvn clean install
```

### Run

```bash
mvn spring-boot:run
```

Or run the JAR:

```bash
java -jar target/reconciliation-engine-1.0.0.jar
```

## API Endpoints

### Base URL

```
http://localhost:8080/api
```

### Rule Management

#### Rule Groups

- `GET /rules/groups` - Get all rule groups
- `GET /rules/groups/active` - Get active rule groups
- `GET /rules/groups/{id}` - Get rule group by ID
- `POST /rules/groups` - Create rule group
- `PUT /rules/groups/{id}` - Update rule group

#### Rules

- `GET /rules` - Get all rules
- `GET /rules/groups/{groupId}/rules` - Get rules for a group
- `GET /rules/{id}` - Get rule by ID
- `POST /rules` - Create rule
- `PUT /rules/{id}` - Update rule
- `DELETE /rules/{id}` - Delete rule

#### Matching Criteria

- `GET /rules/{ruleId}/criteria` - Get criteria for a rule
- `POST /rules/{ruleId}/criteria` - Create matching criteria
- `DELETE /rules/criteria/{id}` - Delete criteria

### Reconciliation

#### Execution

- `POST /reconciliation/execute` - Execute reconciliation
  ```json
  {
    "runName": "Monthly AP Reconciliation - Jan 2025",
    "ruleGroupId": 1,
    "periodStartDate": "2025-01-01",
    "periodEndDate": "2025-01-31",
    "createdBy": "John Doe"
  }
  ```

#### Runs

- `GET /reconciliation/runs` - Get all reconciliation runs
- `GET /reconciliation/runs/{id}` - Get run details
- `GET /reconciliation/runs/{id}/matches` - Get matches for a run
- `GET /reconciliation/runs/{id}/exceptions` - Get exceptions for a run

#### Exceptions

- `GET /reconciliation/exceptions/open` - Get open exceptions
- `PUT /reconciliation/exceptions/{id}` - Update exception status
  ```json
  {
    "status": "RESOLVED",
    "resolutionComments": "Matched manually",
    "updatedBy": "John Doe"
  }
  ```

#### Statistics

- `GET /reconciliation/statistics` - Get dashboard statistics

## Core Components

### ReconciliationEngine

The core reconciliation logic that:
- Fetches unreconciled transactions
- Applies matching rules in priority order
- Performs matching based on rule type (1:1, 1:N, N:1, N:N)
- Creates match records
- Identifies and logs exceptions
- Updates transaction statuses

### RulesEngine

Evaluates matching criteria:
- Supports multiple operators (EQUALS, GREATER_THAN, CONTAINS, etc.)
- Handles tolerance-based matching
- Supports AND/OR logical operators
- Uses reflection for flexible field comparison

### Services

#### ReconciliationService

- Creates and executes reconciliation runs
- Manages matches and exceptions
- Provides reporting data

#### RuleService

- CRUD operations for rule groups
- CRUD operations for rules
- CRUD operations for matching criteria

## Testing

### Unit Tests

```bash
mvn test
```

### Integration Tests

```bash
mvn verify
```

### Manual Testing with Swagger

Access Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

## Performance Tuning

### Database Connection Pool

Configure in `application.properties`:
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

### Batch Processing

Adjust batch size for large reconciliations:
```properties
reconciliation.batch.size=1000
```

### Parallel Processing

Configure thread pool:
```properties
reconciliation.max.parallel.threads=5
```

## Logging

Configure logging levels in `application.properties`:
```properties
logging.level.root=INFO
logging.level.com.accounting.reconciliation=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

## Security Considerations

### Production Deployment

1. **Enable HTTPS**: Configure SSL certificates
2. **Authentication**: Implement Spring Security
3. **Authorization**: Add role-based access control
4. **API Keys**: Protect endpoints with API keys
5. **Input Validation**: Enable validation annotations
6. **SQL Injection**: Use parameterized queries (already implemented)

### Example Security Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/reconciliation/**").hasRole("RECON_USER")
                .requestMatchers("/api/rules/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
```

## Error Handling

The application includes comprehensive error handling:

- Custom exception classes
- Global exception handler
- Meaningful error messages
- HTTP status code mapping

## Monitoring

### Actuator Endpoints

```
http://localhost:8080/actuator/health
http://localhost:8080/actuator/metrics
```

Enable additional endpoints in `application.properties`:
```properties
management.endpoints.web.exposure.include=health,info,metrics
```

## Deployment

### JAR Deployment

```bash
mvn clean package
java -jar target/reconciliation-engine-1.0.0.jar
```

### Docker Deployment

Create `Dockerfile`:
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/reconciliation-engine-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

Build and run:
```bash
docker build -t reconciliation-engine .
docker run -p 8080:8080 reconciliation-engine
```

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check Oracle database is running
   - Verify connection string in application.properties
   - Ensure Oracle JDBC driver is in classpath

2. **Out of Memory Errors**
   - Increase JVM heap size: `-Xmx2g`
   - Reduce batch size
   - Optimize queries

3. **Slow Performance**
   - Check database indexes
   - Review query execution plans
   - Enable query caching
   - Increase connection pool size

## Contributing

1. Follow Java code conventions
2. Add unit tests for new features
3. Update API documentation
4. Test with Oracle database before committing

## License

Copyright (c) 2025 Reconciliation Engine Team
