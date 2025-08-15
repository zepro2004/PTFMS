# PTFMS Deployment Script for Windows
# This script automates the deployment of the Public Transit Fleet Management System

param(
    [switch]$Clean,
    [switch]$SkipBuild,
    [switch]$Help,
    [int]$AppPort = 0,
    [int]$DbPort = 0
)

function Show-Help {
    Write-Host "PTFMS Deployment Script" -ForegroundColor Green
    Write-Host "========================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Usage: .\deploy.ps1 [OPTIONS]"
    Write-Host ""
    Write-Host "Options:"
    Write-Host "  -Clean      Clean up existing containers and volumes before deployment"
    Write-Host "  -SkipBuild  Skip building the Docker image (use existing image)"
    Write-Host "  -AppPort    Specify custom application port (default: 8080)"
    Write-Host "  -DbPort     Specify custom database port (default: not exposed)"
    Write-Host "  -Help       Show this help message"
    Write-Host ""
    Write-Host "Examples:"
    Write-Host "  .\deploy.ps1                      # Normal deployment"
    Write-Host "  .\deploy.ps1 -Clean               # Clean deployment"
    Write-Host "  .\deploy.ps1 -AppPort 8081        # Use port 8081 for app"
    Write-Host "  .\deploy.ps1 -AppPort 9000 -DbPort 3307  # Custom ports"
    Write-Host ""
    Write-Host "Environment Variables:"
    Write-Host "  `$env:APP_PORT=8081; .\deploy.ps1 # Alternative way to set app port"
    Write-Host "  `$env:DB_PORT=3307; .\deploy.ps1  # Alternative way to set DB port"
}

function Write-Status {
    param([string]$Message)
    Write-Host "[$(Get-Date -Format 'HH:mm:ss')] $Message" -ForegroundColor Cyan
}

function Write-Success {
    param([string]$Message)
    Write-Host "[$(Get-Date -Format 'HH:mm:ss')] $Message" -ForegroundColor Green
}

function Write-Error {
    param([string]$Message)
    Write-Host "[$(Get-Date -Format 'HH:mm:ss')] ✗ $Message" -ForegroundColor Red
}

function Test-DockerRunning {
    try {
        docker version | Out-Null
        return $true
    } catch {
        return $false
    }
}

function Test-Prerequisites {
    # Check Docker installation
    if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
        Write-Host "❌ Docker is not installed. Please install Docker Desktop and try again." -ForegroundColor Red
        exit 1
    }
    
    # Check Docker running
    if (-not (Test-DockerRunning)) {
        Write-Host "❌ Docker is not running. Please start Docker Desktop and try again." -ForegroundColor Red
        exit 1
    }
    
    # Check Docker Compose
    if (-not (Get-Command docker-compose -ErrorAction SilentlyContinue)) {
        Write-Host "❌ Docker Compose is not installed. Please install Docker Compose and try again." -ForegroundColor Red
        exit 1
    }
    
    # Check disk space (simplified - just check if temp can be written to)
    try {
        $testFile = [System.IO.Path]::GetTempFileName()
        Remove-Item $testFile
    } catch {
        Write-Host "⚠️  Warning: Could not verify disk space" -ForegroundColor Yellow
    }
    
    # Check internet connectivity
    try {
        $response = Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/" -TimeoutSec 10 -UseBasicParsing
    } catch {
        Write-Host "❌ Cannot reach Maven Central repository. Check your internet connection." -ForegroundColor Red
        Write-Host "You may need to configure Maven for your corporate proxy if behind a firewall." -ForegroundColor Yellow
        exit 1
    }
}

function Test-PortAvailable {
    param([int]$Port)
    
    try {
        $connections = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue
        return -not $connections
    } catch {
        # If we can't check, assume it's available
        return $true
    }
}

function Find-AvailablePort {
    param([int]$StartPort, [int]$MaxAttempts = 50)
    
    for ($port = $StartPort; $port -lt ($StartPort + $MaxAttempts); $port++) {
        if (Test-PortAvailable -Port $port) {
            return $port
        }
    }
    return $null
}

function Request-PortChoice {
    param([string]$ServiceName, [int]$SuggestedPort)
    
    Write-Host ""
    Write-Host "Port $SuggestedPort is in use for $ServiceName" -ForegroundColor Yellow
    Write-Host "Options:"
    Write-Host "  1. Auto-find next available port"
    Write-Host "  2. Specify custom port"
    Write-Host "  3. Cancel deployment"
    Write-Host ""
    
    do {
        $choice = Read-Host "Choose option (1/2/3)"
        switch ($choice) {
            "1" {
                $availablePort = Find-AvailablePort -StartPort ($SuggestedPort + 1)
                if ($availablePort) {
                    Write-Host "Found available port: $availablePort" -ForegroundColor Green
                    return $availablePort
                } else {
                    Write-Host "Could not find available port automatically" -ForegroundColor Red
                    return $null
                }
            }
            "2" {
                do {
                    $customPort = Read-Host "Enter custom port number (1024-65535)"
                    if ($customPort -match '^\d+$' -and [int]$customPort -ge 1024 -and [int]$customPort -le 65535) {
                        if (Test-PortAvailable -Port $customPort) {
                            return [int]$customPort
                        } else {
                            Write-Host "Port $customPort is also in use" -ForegroundColor Red
                        }
                    } else {
                        Write-Host "Invalid port number" -ForegroundColor Red
                    }
                } while ($true)
            }
            "3" {
                Write-Host "Deployment cancelled by user" -ForegroundColor Red
                exit 1
            }
            default {
                Write-Host "Invalid choice" -ForegroundColor Red
            }
        }
    } while ($true)
}

function Wait-ForDatabase {
    Write-Status "Waiting for database to be ready..."
    $maxAttempts = 30
    $attempt = 0
    
    do {
        $attempt++
        Start-Sleep -Seconds 2
        
        try {
            $result = docker exec ptfms-mysql mysqladmin ping -h localhost -u ptfms_user -pchangeMe 2>$null
            if ($result -match "mysqld is alive") {
                Write-Success "Database is ready!"
                return $true
            }
        } catch {
            # Continue waiting
        }
        
        Write-Host "." -NoNewline
        
        if ($attempt -ge $maxAttempts) {
            Write-Error "Database failed to start within timeout period"
            return $false
        }
    } while ($true)
}

# Main execution
if ($Help) {
    Show-Help
    exit 0
}

Write-Host ""
Write-Host "██████╗ ████████╗███████╗███╗   ███╗███████╗" -ForegroundColor Blue
Write-Host "██╔══██╗╚══██╔══╝██╔════╝████╗ ████║██╔════╝" -ForegroundColor Blue
Write-Host "██████╔╝   ██║   █████╗  ██╔████╔██║███████╗" -ForegroundColor Blue
Write-Host "██╔═══╝    ██║   ██╔══╝  ██║╚██╔╝██║╚════██║" -ForegroundColor Blue
Write-Host "██║        ██║   ██║     ██║ ╚═╝ ██║███████║" -ForegroundColor Blue
Write-Host "╚═╝        ╚═╝   ╚═╝     ╚═╝     ╚═╝╚══════╝" -ForegroundColor Blue
Write-Host ""
Write-Host "Public Transit Fleet Management System" -ForegroundColor Blue
Write-Host "Automated Deployment Script" -ForegroundColor Gray
Write-Host ""

# Check prerequisites first
Test-Prerequisites

# Verify .env file exists
Write-Status "Checking environment configuration..."
if (Test-Path ".env") {
    Write-Success "✅ Environment configuration found (.env)"
} else {
    Write-Error "❌ .env file not found! Creating default configuration..."
    @"
# PTFMS Environment Configuration
# Production Environment
PROD_APP_PORT=8082
PROD_DB_PORT=3307
PROD_ADMINER_PORT=8083
PROD_MYSQL_ROOT_PASSWORD=rootChangeMe
PROD_MYSQL_DATABASE=ptfms
PROD_MYSQL_USER=ptfms_user
PROD_MYSQL_PASSWORD=changeMe

# Development Environment  
DEV_APP_PORT=8084
DEV_DB_PORT=3308
DEV_ADMINER_PORT=8085
DEV_DEBUG_PORT=5005
DEV_MYSQL_ROOT_PASSWORD=rootChangeMe_dev
DEV_MYSQL_DATABASE=ptfms_dev
DEV_MYSQL_USER=ptfms_dev_user
DEV_MYSQL_PASSWORD=changeMe_dev
DEV_JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8
DEV_CATALINA_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
"@ | Out-File -FilePath ".env" -Encoding UTF8
    Write-Success "✅ Created default environment configuration"
}

# Check port availability and resolve conflicts
Write-Status "Checking port availability..."

# Determine application port
if ($AppPort -gt 0) {
    $FinalAppPort = $AppPort
} elseif ($env:APP_PORT) {
    $FinalAppPort = [int]$env:APP_PORT
} else {
    $FinalAppPort = 8080
}

# Check if app port is available, prompt for alternative if not
if (-not (Test-PortAvailable -Port $FinalAppPort)) {
    $newPort = Request-PortChoice -ServiceName "application" -SuggestedPort $FinalAppPort
    if ($newPort) {
        $FinalAppPort = $newPort
        $env:APP_PORT = $FinalAppPort.ToString()
    } else {
        Write-Error "Could not resolve application port conflict"
        exit 1
    }
}

# Determine database port (if user wants external access)
if ($DbPort -gt 0) {
    $FinalDbPort = $DbPort
    $env:DB_PORT = $FinalDbPort.ToString()
    Write-Status "Database will be exposed on port $FinalDbPort"
    
    if (-not (Test-PortAvailable -Port $FinalDbPort)) {
        $newPort = Request-PortChoice -ServiceName "database" -SuggestedPort $FinalDbPort
        if ($newPort) {
            $FinalDbPort = $newPort
            $env:DB_PORT = $FinalDbPort.ToString()
        } else {
            Write-Error "Could not resolve database port conflict"
            exit 1
        }
    }
} elseif ($env:DB_PORT) {
    $FinalDbPort = [int]$env:DB_PORT
    Write-Status "Database will be exposed on port $FinalDbPort (from environment)"
} else {
    Write-Status "Database will not be exposed externally (recommended)"
}

# Clean up if requested
if ($Clean) {
    Write-Status "Cleaning up existing deployment..."
    docker-compose down -v 2>$null
    docker rmi ptfms-app 2>$null
}

# Stop existing containers if running
Write-Status "Stopping existing containers..."
docker-compose down 2>$null

# Build and start services
if (-not $SkipBuild) {
    Write-Status "Building application Docker image..."
    docker-compose build
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Failed to build Docker image"
        exit 1
    }
    Write-Success "Docker image built successfully"
}

Write-Status "Starting services..."
docker-compose up -d
if ($LASTEXITCODE -ne 0) {
    Write-Error "Failed to start services"
    exit 1
}

# Wait for database to be ready
if (-not (Wait-ForDatabase)) {
    Write-Error "Database startup failed"
    docker-compose logs database
    exit 1
}

# Execute database initialization
Write-Status "Checking database initialization..."

# Check if database is already initialized by looking for the users table
$tableCheck = docker exec ptfms-mysql mysql -u ptfms_user -pchangeMe -e "USE ptfms; SHOW TABLES;" 2>$null
if ($tableCheck -match "users") {
    Write-Status "Database already initialized, skipping schema setup"
} else {
    Write-Status "Initializing database schema..."
    docker cp src/main/java/V1__create_tables.sql ptfms-mysql:/tmp/
    docker exec ptfms-mysql mysql -u ptfms_user -pchangeMe ptfms -e "source /tmp/V1__create_tables.sql" 2>$null

    if ($LASTEXITCODE -eq 0) {
        Write-Success "Database schema initialized successfully"
    } else {
        Write-Error "Database initialization failed"
        docker-compose logs database
        exit 1
    }
}

# Verify deployment
Write-Status "Verifying deployment..."
Start-Sleep -Seconds 5

$appHealthy = $false
$maxChecks = 10
for ($i = 1; $i -le $maxChecks; $i++) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:$FinalAppPort" -TimeoutSec 5 -UseBasicParsing
        if ($response.StatusCode -eq 200) {
            $appHealthy = $true
            break
        }
    } catch {
        Start-Sleep -Seconds 2
    }
}

if ($appHealthy) {
    Write-Success "Application is healthy and accessible"
} else {
    Write-Error "Application health check failed"
    docker-compose logs app
    exit 1
}

# Display deployment information
Write-Host ""
Write-Host "Deployment completed successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "Service Information:" -ForegroundColor Yellow
Write-Host "  Application URL: http://localhost:$FinalAppPort" -ForegroundColor White
if ($env:DB_PORT) {
    Write-Host "  Database:        localhost:$($env:DB_PORT)" -ForegroundColor White
} else {
    Write-Host "  Database:        Internal Docker network (not exposed)" -ForegroundColor White
}
Write-Host ""
Write-Host "Test User Credentials:" -ForegroundColor Yellow
Write-Host "  Manager:   jmanager  / password123" -ForegroundColor White
Write-Host "  Manager:   mwilson   / password123" -ForegroundColor White
Write-Host "  Operator:  soperator / password123" -ForegroundColor White
Write-Host ""
Write-Host "Useful Commands:" -ForegroundColor Yellow
Write-Host "  View logs:        docker-compose logs" -ForegroundColor White
Write-Host "  Stop services:    docker-compose down" -ForegroundColor White
Write-Host "  Restart services: docker-compose restart" -ForegroundColor White
Write-Host ""
Write-Host "Ready to use! Open http://localhost:$($FinalAppPort) in your browser." -ForegroundColor Green