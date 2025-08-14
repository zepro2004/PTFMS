@echo off
echo Starting PTFMS Deployment...
echo.

REM Check if PowerShell is available
powershell -Command "Get-Host" >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: PowerShell is not available on this system.
    echo Please run deploy.ps1 manually or install PowerShell.
    pause
    exit /b 1
)

REM Run the PowerShell deployment script
powershell -ExecutionPolicy Bypass -File "deploy.ps1"
