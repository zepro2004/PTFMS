#!/bin/bash

# PTFMS Deployment Script for Unix/Linux/macOS
# This script automates the deployment of the Public Transit Fleet Management System

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Default options
CLEAN=false
SKIP_BUILD=false
SHOW_HELP=false
CUSTOM_APP_PORT="if [[ "$CLEAN" == true ]]; then
    log_status "Cleaning up existing deployment..."
    docker-compose down -v 2>/dev/null || true
    docker rmi ptfms-app 2>/dev/null || true
fiOM_DB_PORT=""

# Check prerequisites first
check_prerequisites

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --clean|-c)
            CLEAN=true
            shift
            ;;
        --skip-build|-s)
            SKIP_BUILD=true
            shift
            ;;
        --app-port)
            CUSTOM_APP_PORT="$2"
            shift 2
            ;;
        --db-port)
            CUSTOM_DB_PORT="$2"
            shift 2
            ;;
        --help|-h)
            SHOW_HELP=true
            shift
            ;;
        *)
            echo "Unknown option $1"
            exit 1
            ;;
    esac
done

show_help() {
    echo -e "${GREEN}PTFMS Deployment Script${NC}"
    echo -e "${GREEN}========================${NC}"
    echo ""
    echo "Usage: ./deploy.sh [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -c, --clean       Clean up existing containers and volumes before deployment"
    echo "  -s, --skip-build  Skip building the Docker image (use existing image)"
    echo "  --app-port PORT   Specify custom application port (default: 8080)"
    echo "  --db-port PORT    Specify custom database port (default: not exposed)"
    echo "  -h, --help        Show this help message"
    echo ""
    echo "Examples:"
    echo "  ./deploy.sh                           # Normal deployment"
    echo "  ./deploy.sh --clean                   # Clean deployment"
    echo "  ./deploy.sh --app-port 8081           # Use port 8081 for app"
    echo "  ./deploy.sh --app-port 9000 --db-port 3307  # Custom ports"
    echo ""
    echo "Environment Variables:"
    echo "  APP_PORT=8081 ./deploy.sh             # Alternative way to set app port"
    echo "  DB_PORT=3307 ./deploy.sh              # Alternative way to set DB port"
}

log_status() {
    echo -e "${CYAN}[$(date +'%H:%M:%S')] $1${NC}"
}

log_success() {
    echo -e "${GREEN}[$(date +'%H:%M:%S')] ✓ $1${NC}"
}

log_error() {
    echo -e "${RED}[$(date +'%H:%M:%S')] ✗ $1${NC}"
}

check_docker() {
    if ! command -v docker &> /dev/null; then
        log_error "Docker is not installed. Please install Docker and try again."
        exit 1
    fi
    
    if ! docker version &> /dev/null; then
        log_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose is not installed. Please install Docker Compose and try again."
        exit 1
    fi
}

check_disk_space() {
    local required_space_mb=2048  # 2GB minimum
    local available_space_mb
    
    if command -v df &> /dev/null; then
        available_space_mb=$(df . | awk 'NR==2 {print int($4/1024)}')
        if [ "$available_space_mb" -lt "$required_space_mb" ]; then
            log_error "Insufficient disk space. Required: ${required_space_mb}MB, Available: ${available_space_mb}MB"
            exit 1
        fi
    fi
}

check_internet_connectivity() {
    # Try curl first, then wget, then skip with warning
    if command -v curl &> /dev/null; then
        if ! timeout 10 curl -s https://repo1.maven.org/maven2/ > /dev/null 2>&1; then
            log_error "Cannot reach Maven Central repository. Check your internet connection."
            echo "You may need to configure Maven for your corporate proxy if behind a firewall."
            exit 1
        fi
    elif command -v wget &> /dev/null; then
        if ! timeout 10 wget -q --spider https://repo1.maven.org/maven2/ 2>/dev/null; then
            log_error "Cannot reach Maven Central repository. Check your internet connection."
            echo "You may need to configure Maven for your corporate proxy if behind a firewall."
            exit 1
        fi
    fi
    # Silent success - no output if connectivity is fine
}

check_prerequisites() {
    # Check if script is executable
    if [ ! -x "$0" ]; then
        log_error "Deploy script is not executable. Run: chmod +x deploy.sh"
        exit 1
    fi
    
    check_docker
    check_disk_space
    check_internet_connectivity
}

check_port_availability() {
    local port=$1
    local service_name=$2
    
    if command -v lsof &> /dev/null; then
        if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null ; then
            return 1
        fi
    elif command -v netstat &> /dev/null; then
        if netstat -ln | grep ":$port " >/dev/null ; then
            return 1
        fi
    elif command -v ss &> /dev/null; then
        if ss -ln | grep ":$port " >/dev/null ; then
            return 1
        fi
    fi
    return 0
}

find_available_port() {
    local start_port=$1
    local max_attempts=50
    
    for ((port=start_port; port<start_port+max_attempts; port++)); do
        if check_port_availability $port ""; then
            echo $port
            return 0
        fi
    done
    
    echo ""
    return 1
}

prompt_for_port() {
    local service_name=$1
    local suggested_port=$2
    
    echo ""
    echo -e "${YELLOW}Port $suggested_port is in use for $service_name${NC}"
    echo "Options:"
    echo "  1. Auto-find next available port"
    echo "  2. Specify custom port"
    echo "  3. Cancel deployment"
    echo ""
    read -p "Choose option (1/2/3): " -n 1 -r choice
    echo ""
    
    case $choice in
        1)
            local available_port
            available_port=$(find_available_port $((suggested_port + 1)))
            if [[ -n "$available_port" ]]; then
                echo -e "${GREEN}Found available port: $available_port${NC}"
                echo $available_port
                return 0
            else
                echo -e "${RED}Could not find available port automatically${NC}"
                return 1
            fi
            ;;
        2)
            echo ""
            read -p "Enter custom port number: " custom_port
            if [[ $custom_port =~ ^[0-9]+$ ]] && [[ $custom_port -ge 1024 ]] && [[ $custom_port -le 65535 ]]; then
                if check_port_availability $custom_port ""; then
                    echo $custom_port
                    return 0
                else
                    echo -e "${RED}Port $custom_port is also in use${NC}"
                    return 1
                fi
            else
                echo -e "${RED}Invalid port number${NC}"
                return 1
            fi
            ;;
        3)
            echo -e "${RED}Deployment cancelled${NC}"
            exit 1
            ;;
        *)
            echo -e "${RED}Invalid choice${NC}"
            return 1
            ;;
    esac
}

initialize_database() {
    # Check if schema is already initialized
    if docker exec ptfms-mysql mysql -u ptfms_user -pchangeMe -e "USE ptfms; SHOW TABLES;" 2>/dev/null | grep -q users; then
        return 0  # Silent success - database already initialized
    fi
    
    # Find SQL schema file
    local sql_file=""
    if [ -f "src/main/java/V1__create_tables.sql" ]; then
        sql_file="src/main/java/V1__create_tables.sql"
    elif [ -f "schema.sql" ]; then
        sql_file="schema.sql"
    else
        log_error "No database schema file found. Expected 'src/main/java/V1__create_tables.sql' or 'schema.sql'"
        exit 1
    fi
    
    log_info "Applying database schema from $sql_file..."
    docker cp "$sql_file" ptfms-mysql:/tmp/
    if docker exec ptfms-mysql mysql -u ptfms_user -pchangeMe ptfms -e "source /tmp/V1__create_tables.sql" 2>/dev/null; then
        log_success "Database schema initialized successfully"
    else
        log_error "Failed to initialize database schema"
        exit 1
    fi
}

wait_for_database() {
    log_status "Waiting for database to be ready..."
    local max_attempts=30
    local attempt=0
    
    while [ $attempt -lt $max_attempts ]; do
        attempt=$((attempt + 1))
        sleep 2
        
        if docker exec ptfms-mysql mysqladmin ping -h localhost -u ptfms_user -pchangeMe &> /dev/null; then
            log_success "Database is ready!"
            return 0
        fi
        
        printf "."
        
        if [ $attempt -ge $max_attempts ]; then
            echo ""
            log_error "Database failed to start within timeout period"
            return 1
        fi
    done
}

verify_deployment() {
    log_status "Verifying deployment..."
    sleep 5
    
    local app_port=${APP_PORT:-8080}
    local max_checks=10
    for i in $(seq 1 $max_checks); do
        if curl -s -o /dev/null -w "%{http_code}" http://localhost:$app_port | grep -q "200"; then
            log_success "Application is healthy and accessible"
            return 0
        fi
        sleep 2
    done
    
    log_error "Application health check failed"
    docker-compose logs app
    return 1
}

# Main execution
if [ "$SHOW_HELP" = true ]; then
    show_help
    exit 0
fi

echo ""
echo -e "${BLUE}██████╗ ████████╗███████╗███╗   ███╗███████╗${NC}"
echo -e "${BLUE}██╔══██╗╚══██╔══╝██╔════╝████╗ ████║██╔════╝${NC}"
echo -e "${BLUE}██████╔╝   ██║   █████╗  ██╔████╔██║███████╗${NC}"
echo -e "${BLUE}██╔═══╝    ██║   ██╔══╝  ██║╚██╔╝██║╚════██║${NC}"
echo -e "${BLUE}██║        ██║   ██║     ██║ ╚═╝ ██║███████║${NC}"
echo -e "${BLUE}╚═╝        ╚═╝   ╚═╝     ╚═╝     ╚═╝╚══════╝${NC}"
echo ""
echo -e "${BLUE}Public Transit Fleet Management System${NC}"
echo -e "Automated Deployment Script"
echo ""

# Check prerequisites first
check_prerequisites

# Check port availability and resolve conflicts
log_status "Checking port availability..."

# Determine application port
if [[ -n "$CUSTOM_APP_PORT" ]]; then
    APP_PORT=$CUSTOM_APP_PORT
elif [[ -z "$APP_PORT" ]]; then
    APP_PORT=8080
fi
export APP_PORT

# Check if app port is available, prompt for alternative if not
if ! check_port_availability $APP_PORT "application"; then
    new_port=$(prompt_for_port "application" $APP_PORT)
    if [[ $? -eq 0 && -n "$new_port" ]]; then
        APP_PORT=$new_port
        export APP_PORT
    else
        log_error "Could not resolve application port conflict"
        exit 1
    fi
fi

# Determine database port (if user wants external access)
if [[ -n "$CUSTOM_DB_PORT" ]]; then
    DB_PORT=$CUSTOM_DB_PORT
    export DB_PORT
    log_status "Database will be exposed on port $DB_PORT"
    
    if ! check_port_availability $DB_PORT "database"; then
        new_port=$(prompt_for_port "database" $DB_PORT)
        if [[ $? -eq 0 && -n "$new_port" ]]; then
            DB_PORT=$new_port
            export DB_PORT
        else
            log_error "Could not resolve database port conflict"
            exit 1
        fi
    fi
elif [[ -n "$DB_PORT" ]]; then
    log_status "Database will be exposed on port $DB_PORT (from environment)"
else
    log_status "Database will not be exposed externally (recommended)"
fi

# Clean up if requested
if [ "$CLEAN" = true ]; then
    log_status "Cleaning up existing deployment..."
    docker-compose down -v 2>/dev/null || true
    docker rmi ptfms-app 2>/dev/null || true
    log_success "Cleanup completed"
fi

# Stop existing containers if running
log_status "Stopping existing containers..."
docker-compose down 2>/dev/null || true

# Build and start services
if [ "$SKIP_BUILD" != true ]; then
    log_status "Building application Docker image..."
    if ! docker-compose build; then
        log_error "Failed to build Docker image"
        exit 1
    fi
    log_success "Docker image built successfully"
fi

log_status "Starting services..."
if ! docker-compose up -d; then
    log_error "Failed to start services"
    exit 1
fi

# Wait for database to be ready
if ! wait_for_database; then
    log_error "Database startup failed"
    docker-compose logs database
    exit 1
fi

# Execute database initialization
initialize_database

# Verify deployment
if ! verify_deployment; then
    exit 1
fi

# Display deployment information
APP_PORT=${APP_PORT:-8080}
echo ""
echo -e "${GREEN}🎉 Deployment completed successfully!${NC}"
echo ""
echo -e "${YELLOW}📋 Service Information:${NC}"
echo -e "  Application URL: ${NC}http://localhost:$APP_PORT"
echo -e "  Database:        ${NC}Internal Docker network (not exposed)"
echo ""
echo -e "${YELLOW}👤 Test User Credentials:${NC}"
echo -e "  Manager:   ${NC}jmanager  / password123"
echo -e "  Manager:   ${NC}mwilson   / password123"
echo -e "  Operator:  ${NC}soperator / password123"
echo ""
echo -e "${YELLOW}📊 Useful Commands:${NC}"
echo -e "  View logs:        ${NC}docker-compose logs"
echo -e "  Stop services:    ${NC}docker-compose down"
echo -e "  Restart services: ${NC}docker-compose restart"
echo ""
echo -e "${GREEN}✨ Ready to use! Open http://localhost:$APP_PORT in your browser.${NC}"
