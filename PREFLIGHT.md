# 🚀 PTFMS Pre-Flight Checklist

Before deploying PTFMS, ensure these requirements are met:

## ✅ System Requirements

### Required Software
- [ ] **Docker** (20.10+ recommended)
- [ ] **Docker Compose** (2.0+ recommended)
- [ ] **Git** (for cloning repository)

### System Resources
- [ ] **Disk Space:** Minimum 2GB free space
- [ ] **Memory:** Minimum 4GB RAM recommended
- [ ] **Network:** Internet access for Maven dependencies

### Ports
- [ ] **Port 8080** available (or specify custom with `--app-port`)
- [ ] **Port 3306** not required (database runs internally)

## 🔧 Platform-Specific Setup

### Windows
```powershell
# Install Docker Desktop from https://docker.com
# Set PowerShell execution policy
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Run deployment
.\deploy.ps1
```

### Linux/macOS
```bash
# Make script executable
chmod +x deploy.sh

# Run deployment
./deploy.sh
```

## 🌐 Network Configuration

### Corporate Networks
If behind a corporate firewall, you may need to configure Maven proxy:

1. Create `~/.m2/settings.xml`:
```xml
<settings>
  <proxies>
    <proxy>
      <host>your-proxy-host</host>
      <port>your-proxy-port</port>
      <username>your-username</username>
      <password>your-password</password>
    </proxy>
  </proxies>
</settings>
```

### Port Conflicts
Common conflicting services:
- **Port 8080:** Other web servers, Jenkins, Tomcat
- **Port 3306:** Local MySQL installations

**Solution:** Use custom ports
```bash
./deploy.sh --app-port 9000 --db-port 3307
```

## 🚨 Quick Validation

Run this command to verify your system is ready:

### Windows (PowerShell)
```powershell
# Check Docker
docker --version
docker-compose --version

# Check ports
Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue

# Check internet
Test-NetConnection repo1.maven.org -Port 443
```

### Linux/macOS
```bash
# Check Docker
docker --version
docker-compose --version

# Check ports
lsof -i :8080

# Check internet
curl -I https://repo1.maven.org/maven2/
```

## 🎯 Quick Start

Once prerequisites are met:

```bash
# Clone repository
git clone <repository-url>
cd PTFMS

# Deploy (interactive mode handles conflicts)
./deploy.sh        # Linux/macOS
.\deploy.ps1       # Windows

# Or with custom ports
./deploy.sh --app-port 9000
```

## 🔍 Troubleshooting

If deployment fails, see [TROUBLESHOOTING.md](./TROUBLESHOOTING.md) for detailed solutions.

**Most common issues:**
1. Docker not running → Start Docker Desktop
2. Port conflicts → Use `--app-port` option
3. Internet issues → Check firewall/proxy settings
4. Permissions → Make script executable (`chmod +x`)

---
**Ready to deploy?** Run the deployment script and follow the prompts!
