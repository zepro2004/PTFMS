# PTFMS Deployment Guide

This directory contains automated deployment scripts for the Public Transit Fleet Management System (PTFMS).

## Quick Start

### Windows (PowerShell)
```powershell
.\deploy.ps1
```

### Linux/macOS (Bash)
```bash
./deploy.sh
```

## Prerequisites

- **Docker**: Install Docker Desktop (Windows/macOS) or Docker Engine (Linux)
- **Docker Compose**: Usually included with Docker Desktop
- **Git**: For cloning the repository

## Deployment Options

### Normal Deployment
```bash
# Windows
.\deploy.ps1

# Linux/macOS
./deploy.sh
```

### Clean Deployment (removes all data)
```bash
# Windows
.\deploy.ps1 -Clean

# Linux/macOS
./deploy.sh --clean
```

### Fast Deployment (skip image build)
```bash
# Windows
.\deploy.ps1 -SkipBuild

# Linux/macOS
./deploy.sh --skip-build
```

## What the Script Does

1. **Checks Docker availability**
2. **Builds the application Docker image** (unless skipped)
3. **Starts MySQL database and application containers**
4. **Waits for database to be ready**
5. **Initializes database with tables and sample data**
6. **Verifies deployment health**
7. **Displays connection information and test credentials**

## After Deployment

Once deployment is complete:

- **Application URL**: http://localhost:8080 (or custom port if set)
- **Database**: Internal Docker network (not exposed to avoid port conflicts)

### Test User Credentials
- **Manager**: `jmanager` / `password123`
- **Manager**: `mwilson` / `password123`
- **Operator**: `soperator` / `password123`

## Managing the Deployment

### View Logs
```bash
docker-compose logs
docker-compose logs app        # Application logs only
docker-compose logs database   # Database logs only
```

### Stop Services
```bash
docker-compose down
```

### Restart Services
```bash
docker-compose restart
```

### Complete Cleanup
```bash
docker-compose down -v  # Removes containers, networks, and volumes
```

## Troubleshooting

### Database Connection Issues
```bash
# Check database status
docker-compose logs database

# Restart database
docker-compose restart database
```

### Application Issues
```bash
# Check application logs
docker-compose logs app

# Restart application
docker-compose restart app
```

### Port Conflicts and Customization

The deployment scripts automatically detect port conflicts and offer solutions.

#### Automatic Port Detection
```bash
# If ports are busy, scripts will:
# 1. Detect the conflict
# 2. Offer to auto-find next available port
# 3. Allow manual port specification
# 4. Cancel if needed
```

#### Custom Port Examples

**Application Port (8080 alternative):**
```bash
# Linux/macOS
./deploy.sh --app-port 8081
./deploy.sh --app-port 9000
export APP_PORT=8081 && ./deploy.sh

# Windows
.\deploy.ps1 -AppPort 8081
.\deploy.ps1 -AppPort 9000
$env:APP_PORT=8081; .\deploy.ps1
```

**Database Port (external access):**
```bash
# Linux/macOS - Expose database externally
./deploy.sh --db-port 3307
./deploy.sh --app-port 8081 --db-port 3307

# Windows
.\deploy.ps1 -DbPort 3307
.\deploy.ps1 -AppPort 8081 -DbPort 3307
```

**Interactive Port Resolution:**
```bash
# When conflicts detected:
./deploy.sh
# Output:
# Port 8080 is in use for application
# Options:
#   1. Auto-find next available port    [Finds 8081, 8082, etc.]
#   2. Specify custom port             [You choose: 9000, 3000, etc.]
#   3. Cancel deployment
```

#### Environment Variable Method
```bash
# Set multiple ports via environment
export APP_PORT=8081
export DB_PORT=3307
./deploy.sh

# Windows PowerShell
$env:APP_PORT=8081
$env:DB_PORT=3307
.\deploy.ps1
```

#### Docker Compose Direct Override
```bash
# Manual docker-compose with custom ports
APP_PORT=8081 DB_PORT=3307 docker-compose up -d --build
```

**Note**: Database port exposure is optional. By default, the database runs internally and isn't accessible from outside Docker for security.

## Manual Deployment (Alternative)

If you prefer manual deployment:

```bash
# Build and start services
docker-compose up -d

# Initialize database (wait for database to start first)
docker cp src/main/java/V1__create_tables.sql ptfms-mysql:/tmp/
docker exec ptfms-mysql mysql -u ptfms_user -pchangeMe ptfms -e "source /tmp/V1__create_tables.sql"
```

## Development

For development with auto-reload:
```bash
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up
```

## Support

If you encounter issues:
1. Check Docker is running: `docker version`
2. Check Docker Compose: `docker-compose version`
3. View deployment logs from the script output
4. Check individual service logs: `docker-compose logs [service]`
