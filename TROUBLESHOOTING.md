# PTFMS Deployment Troubleshooting Guide

This guide addresses common edge cases and deployment issues that new users might encounter.

## 🚨 Common Deployment Issues

### 1. **Port Conflicts**
**Problem:** Ports 8080 or 3306 are already in use
**Solution:** ✅ **Automatically handled**
- Scripts detect port conflicts and offer alternatives
- Interactive prompts for resolution
- Command-line options: `--app-port PORT` and `--db-port PORT`

### 2. **Docker Not Available**
**Problem:** Docker is not installed or not running
**Symptoms:**
```
❌ Docker is not installed. Please install Docker and try again.
❌ Docker is not running. Please start Docker and try again.
```
**Solution:**
- **Windows:** Install [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- **Linux:** Install Docker Engine and Docker Compose
- **macOS:** Install [Docker Desktop](https://www.docker.com/products/docker-desktop/)

### 3. **PowerShell Execution Policy (Windows)**
**Problem:** Script execution blocked by PowerShell
**Symptoms:**
```
execution of scripts is disabled on this system
```
**Solution:**
```powershell
# Run as Administrator
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
# Then run the deployment
.\deploy.ps1
```

### 4. **File Permissions (Unix/Linux)**
**Problem:** Deploy script is not executable
**Symptoms:**
```
❌ Deploy script is not executable. Run: chmod +x deploy.sh
```
**Solution:**
```bash
chmod +x deploy.sh
./deploy.sh
```

### 5. **Internet Connectivity Issues**
**Problem:** Cannot download Maven dependencies
**Symptoms:**
```
❌ Cannot reach Maven Central repository. Check your internet connection.
```
**Solutions:**
- Check internet connection
- For corporate networks, configure Maven proxy:
  ```bash
  # Create ~/.m2/settings.xml with proxy settings
  mkdir -p ~/.m2
  cat > ~/.m2/settings.xml << EOF
  <settings>
    <proxies>
      <proxy>
        <host>your-proxy-host</host>
        <port>your-proxy-port</port>
      </proxy>
    </proxies>
  </settings>
  EOF
  ```

### 6. **Insufficient Disk Space**
**Problem:** Not enough space for Docker images
**Symptoms:**
```
❌ Insufficient disk space. Required: 2048MB, Available: 1024MB
```
**Solution:**
- Free up at least 2GB of disk space
- Clean up old Docker images: `docker system prune -a`

### 7. **Database Initialization Fails**
**Problem:** Database schema not applied correctly
**Symptoms:**
```
❌ Failed to initialize database schema
```
**Solutions:**
1. **Check SQL file exists:**
   ```bash
   ls -la src/main/java/V1__create_tables.sql
   ```

2. **Manual database reset:**
   ```bash
   docker-compose down -v  # Removes data volumes
   ./deploy.sh --clean     # Clean deployment
   ```

3. **Check database logs:**
   ```bash
   docker-compose logs database
   ```

### 8. **Maven Build Failures**
**Problem:** Java compilation or dependency issues
**Symptoms:**
```
❌ Failed to build Docker image
```
**Solutions:**
1. **Check Java version in Dockerfile matches pom.xml**
2. **Clear Maven cache:**
   ```bash
   docker-compose down
   docker rmi ptfms-app
   ./deploy.sh --clean
   ```

3. **Check for dependency conflicts in pom.xml**

## 🔧 Advanced Troubleshooting

### Container Health Checks
```bash
# Check container status
docker-compose ps

# View container logs
docker-compose logs app
docker-compose logs database

# Execute commands in containers
docker exec -it ptfms-app bash
docker exec -it ptfms-mysql mysql -u ptfms_user -p
```

### Port Management
```bash
# Check what's using a port (Linux/macOS)
lsof -i :8080
netstat -tulpn | grep :8080

# Check what's using a port (Windows)
netstat -ano | findstr :8080
Get-NetTCPConnection -LocalPort 8080
```

### Environment Variables
```bash
# Override default ports
export APP_PORT=9000
export DB_PORT=3307
./deploy.sh

# Or use command line
./deploy.sh --app-port 9000 --db-port 3307
```

### Clean Deployment
```bash
# Complete cleanup and fresh start
./deploy.sh --clean

# Manual cleanup
docker-compose down -v
docker rmi ptfms-app
docker system prune
```

## 🐛 Reporting Issues

If you encounter issues not covered here:

1. **Gather information:**
   ```bash
   # System info
   docker version
   docker-compose version
   
   # Container status
   docker-compose ps
   docker-compose logs
   
   # Port usage
   netstat -tulpn | grep -E "(8080|3306)"
   ```

2. **Check the deployment logs** for specific error messages
3. **Try a clean deployment:** `./deploy.sh --clean`
4. **Create an issue** with the gathered information

## 📚 Additional Resources

- [Docker Installation Guide](https://docs.docker.com/get-docker/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Maven Proxy Configuration](https://maven.apache.org/guides/mini/guide-proxies.html)
- [PTFMS Deployment Guide](./DEPLOYMENT.md)
