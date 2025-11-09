# Deployment Guide - Multiple Options

## 🎯 Quick Recommendation

**For Learning/Testing:** Local installation with Oracle XE (FREE)
**For Production:** Oracle Cloud Free Tier (FREE forever)
**For Quick Demo:** Heroku ($5/month)

---

## Option 1: Local Installation (FREE)

### Prerequisites
- 8GB RAM minimum
- 20GB free disk space
- Windows/Mac/Linux

### Step 1: Install Oracle Database XE

**Windows:**
```powershell
# Download from: https://www.oracle.com/database/technologies/xe-downloads.html
# Run the installer (OracleXE213_Win64.zip)
# During installation:
# - Set SYS password: YourPassword123
# - Port: 1521 (default)
# - Database name: XE
```

**Mac/Linux (using Docker - EASIER):**
```bash
# Pull Oracle XE image
docker pull container-registry.oracle.com/database/express:latest

# Run Oracle XE
docker run -d \
  --name oracle-xe \
  -p 1521:1521 \
  -p 5500:5500 \
  -e ORACLE_PWD=YourPassword123 \
  container-registry.oracle.com/database/express:latest

# Wait 2-3 minutes for database to start
docker logs -f oracle-xe
```

### Step 2: Set Up Database Schema

```bash
# Connect to database
sqlplus system/YourPassword123@localhost:1521/XE

# Create user
CREATE USER recon_user IDENTIFIED BY recon_pass;
GRANT CONNECT, RESOURCE TO recon_user;
GRANT CREATE SESSION TO recon_user;
GRANT UNLIMITED TABLESPACE TO recon_user;

# Connect as new user
CONNECT recon_user/recon_pass@localhost:1521/XE

# Run schema scripts
@database/schema/01_reconciliation_metadata.sql
@database/schema/02_sample_data.sql
```

### Step 3: Configure and Run Backend

```bash
cd backend

# Update application.properties
nano src/main/resources/application.properties

# Change these lines:
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.username=recon_user
spring.datasource.password=recon_pass

# Build and run
mvn clean install
mvn spring-boot:run
```

### Step 4: Run Frontend

```bash
cd frontend
npm install
npm start
```

### Step 5: Access Application

- Frontend: http://localhost:8081
- Backend API: http://localhost:8080
- Swagger Docs: http://localhost:8080/swagger-ui.html

**Total Cost: $0**

---

## Option 2: Oracle Cloud Free Tier (FREE Forever)

### Step 1: Create Oracle Cloud Account

1. Go to: https://www.oracle.com/cloud/free/
2. Click "Start for free"
3. Enter email and create account
4. Verify email
5. Add payment method (won't be charged on free tier)

### Step 2: Create Autonomous Database

```bash
# In Oracle Cloud Console:
1. Click "Create Database" → "Autonomous Database"
2. Choose:
   - Workload Type: Transaction Processing
   - Database Name: ReconDB
   - Admin Password: YourPassword123
   - Always Free: ✅ YES
3. Wait 2 minutes for provisioning

# Download connection wallet:
1. Click database name
2. Click "DB Connection"
3. Download Wallet (ZIP file)
```

### Step 3: Deploy Backend to Oracle Cloud

```bash
# Create Compute Instance (Free VM)
1. Go to Compute → Instances
2. Create Instance
   - Name: reconciliation-backend
   - Image: Oracle Linux 8
   - Shape: VM.Standard.E2.1.Micro (Always Free)
   - SSH Keys: Upload your public key
3. Wait for provisioning

# SSH into instance
ssh -i your-key.pem opc@<instance-ip>

# Install Java 17
sudo yum install -y java-17-openjdk-devel
sudo yum install -y maven git

# Clone your repo
git clone https://github.com/your-username/Accounting-Engine.git
cd Accounting-Engine/backend

# Update application.properties with Autonomous DB connection
# (use wallet and connection string from Step 2)

# Build and run
mvn clean package
nohup java -jar target/reconciliation-engine-1.0.0.jar &
```

### Step 4: Deploy Frontend to Object Storage

```bash
# In Oracle Cloud Console:
1. Go to Storage → Buckets
2. Create Bucket: "reconciliation-frontend"
3. Set visibility: Public

# Upload frontend files
cd frontend
npm install
# Update API URL in js/app.js to point to backend IP
# Upload all files to bucket

# Enable static website hosting
1. Click bucket
2. Edit → Enable "Static Website Hosting"
3. Index document: index.html
```

### Step 5: Access Application

- Frontend: https://<bucket-url>/index.html
- Backend: http://<instance-ip>:8080

**Total Cost: $0/month forever**

---

## Option 3: Switch to PostgreSQL (More Portable)

### Why PostgreSQL?
- ✅ FREE and open source
- ✅ Works everywhere (AWS, Azure, GCP, Heroku, local)
- ✅ Easier to set up
- ✅ Better community support

### Code Changes Required (Minor)

**1. Update pom.xml:**
```xml
<!-- Remove Oracle driver -->
<!-- Add PostgreSQL driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.0</version>
</dependency>
```

**2. Update application.properties:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/reconciliation
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

**3. Update SQL Scripts:**
```sql
-- Change SEQUENCES to SERIAL
-- Change NUMBER to NUMERIC
-- Change VARCHAR2 to VARCHAR
-- Change TO_DATE to to_timestamp
-- Remove CHAR(1) constraints (use VARCHAR(1))
```

Would you like me to create PostgreSQL versions of the SQL scripts?

### Local PostgreSQL Setup:

**Windows:**
```bash
# Download from: https://www.postgresql.org/download/windows/
# Install PostgreSQL 15
# Set password during installation
```

**Mac:**
```bash
brew install postgresql@15
brew services start postgresql@15
```

**Linux:**
```bash
sudo apt install postgresql-15
sudo systemctl start postgresql
```

**Docker (Any OS):**
```bash
docker run -d \
  --name postgres \
  -p 5432:5432 \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=reconciliation \
  postgres:15
```

**Total Cost: $0**

---

## Option 4: Heroku Quick Deploy (Easiest)

### Prerequisites
- Git installed
- Heroku CLI installed

### Steps:

```bash
# 1. Install Heroku CLI
# Download from: https://devcenter.heroku.com/articles/heroku-cli

# 2. Login
heroku login

# 3. Create apps
heroku create your-recon-backend
heroku create your-recon-frontend

# 4. Add PostgreSQL to backend
heroku addons:create heroku-postgresql:mini -a your-recon-backend
# Cost: $5/month

# 5. Deploy backend
cd backend
git init
heroku git:remote -a your-recon-backend
git add .
git commit -m "Deploy backend"
git push heroku main

# 6. Deploy frontend
cd ../frontend
# Update API URL in js/app.js
git init
heroku git:remote -a your-recon-frontend
git add .
git commit -m "Deploy frontend"
git push heroku main

# 7. Access
heroku open -a your-recon-frontend
```

**Total Cost: $5/month**

---

## Option 5: Docker Compose (Development)

### Prerequisites
- Docker Desktop installed

### Single Command Deployment:

```bash
# In project root
docker-compose up -d

# Access:
# - Frontend: http://localhost:80
# - Backend: http://localhost:8080
# - Database: localhost:1521 (Oracle) or localhost:5432 (PostgreSQL)
```

**Total Cost: $0 (local only)**

---

## 💰 Cost Comparison

| Option | Database | Hosting | Monthly Cost | Best For |
|--------|----------|---------|--------------|----------|
| Local Oracle XE | FREE | - | **$0** | Development |
| Oracle Cloud Free | FREE | FREE | **$0** | Production |
| AWS Free Tier | FREE (1yr) | FREE (1yr) | **$0** then ~$25 | Production |
| Heroku | $5 | $7 | **$12** | Quick demo |
| DigitalOcean | - | $6 | **$6** | Production |
| Local PostgreSQL | FREE | - | **$0** | Development |

---

## 🎯 My Recommendation

### For You (Learning/Testing):

**Best Option: Local PostgreSQL**
```bash
# Easiest setup, most portable
1. Install PostgreSQL (5 minutes)
2. Let me convert the SQL scripts to PostgreSQL
3. Run backend locally
4. Run frontend locally
Total time: 30 minutes
Total cost: $0
```

### For Production:

**Best Option: Oracle Cloud Free Tier**
- FREE forever
- Oracle database included
- Production-ready
- Auto-backups and monitoring

---

## 🚀 Quick Start (My Recommendation)

I suggest we:

1. **Convert to PostgreSQL** (I'll do this for you)
2. **Run locally first** (easiest to learn)
3. **Then deploy to cloud** when ready

Would you like me to:
1. ✅ Convert all SQL scripts to PostgreSQL?
2. ✅ Update Java code for PostgreSQL?
3. ✅ Create Docker setup for one-command deployment?
4. ✅ Create detailed step-by-step guide for your OS?

Just tell me:
- **What OS are you using?** (Windows/Mac/Linux)
- **Prefer Oracle or PostgreSQL?** (PostgreSQL is easier)
- **Want local or cloud first?** (Local is easier to start)
