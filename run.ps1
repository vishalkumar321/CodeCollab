# =====================================================================
# CodeCollab Runner Script
# Downloads JDK 17 & Maven locally (no admin required) and runs the project
# =====================================================================

$ErrorActionPreference = "Stop"

# Define tools directory
$ToolsDir = Join-Path $PSScriptRoot "tools"
if (-not (Test-Path $ToolsDir)) {
    Write-Host "Creating tools directory..." -ForegroundColor Cyan
    New-Item -ItemType Directory -Path $ToolsDir | Out-Null
}

# 1. Download & Extract JDK 17
$JdkDir = Join-Path $ToolsDir "jdk17"
$JdkZip = Join-Path $ToolsDir "jdk17.zip"
if (-not (Test-Path $JdkDir)) {
    Write-Host "Downloading Microsoft OpenJDK 17 (this may take a minute)..." -ForegroundColor Cyan
    $JdkUrl = "https://aka.ms/download-jdk/microsoft-jdk-17-windows-x64.zip"
    Invoke-WebRequest -Uri $JdkUrl -OutFile $JdkZip
    
    Write-Host "Extracting JDK 17..." -ForegroundColor Cyan
    Expand-Archive -Path $JdkZip -DestinationPath $JdkDir
    
    Write-Host "Cleaning up JDK zip..." -ForegroundColor Cyan
    Remove-Item $JdkZip -Force
} else {
    Write-Host "JDK 17 already exists locally." -ForegroundColor Green
}

# 2. Download & Extract Maven 3.9.9
$MvnDir = Join-Path $ToolsDir "maven"
$MvnZip = Join-Path $ToolsDir "maven.zip"
if (-not (Test-Path $MvnDir)) {
    Write-Host "Downloading Apache Maven 3.9.9..." -ForegroundColor Cyan
    $MvnUrl = "https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip"
    Invoke-WebRequest -Uri $MvnUrl -OutFile $MvnZip
    
    Write-Host "Extracting Maven..." -ForegroundColor Cyan
    Expand-Archive -Path $MvnZip -DestinationPath $MvnDir
    
    Write-Host "Cleaning up Maven zip..." -ForegroundColor Cyan
    Remove-Item $MvnZip -Force
} else {
    Write-Host "Maven already exists locally." -ForegroundColor Green
}

# 3. Locate executable directories dynamically
Write-Host "Locating executable bin directories..." -ForegroundColor Cyan
$JavaExe = Get-ChildItem -Path $JdkDir -Filter "java.exe" -Recurse | Select-Object -First 1
if (-not $JavaExe) {
    throw "Could not find java.exe in extracted JDK!"
}
$JavaBinDir = $JavaExe.DirectoryName
$JavaHome = Split-Path -Parent $JavaBinDir

$MvnCmd = Get-ChildItem -Path $MvnDir -Filter "mvn.cmd" -Recurse | Select-Object -First 1
if (-not $MvnCmd) {
    throw "Could not find mvn.cmd in extracted Maven!"
}
$MvnBinDir = $MvnCmd.DirectoryName

Write-Host "JDK Bin Directory: $JavaBinDir" -ForegroundColor Gray
Write-Host "JAVA_HOME: $JavaHome" -ForegroundColor Gray
Write-Host "Maven Bin Directory: $MvnBinDir" -ForegroundColor Gray

# 4. Launch Backend in a new window
Write-Host "Launching Backend (Spring Boot)..." -ForegroundColor Cyan
$BackendDir = Join-Path $PSScriptRoot "backend"
$BackendCommand = @"
`$env:JAVA_HOME = '$JavaHome'
`$env:PATH = '$JavaBinDir;$MvnBinDir;' + `$env:PATH
cd '$BackendDir'
Write-Host "Starting Spring Boot..." -ForegroundColor Cyan
mvn spring-boot:run
"@

Start-Process powershell -ArgumentList "-NoExit", "-Command", $BackendCommand

# 5. Launch Frontend in a new window
Write-Host "Launching Frontend (Vite)..." -ForegroundColor Cyan
$FrontendDir = Join-Path $PSScriptRoot "frontend"
$FrontendCommand = @"
cd '$FrontendDir'
Write-Host "Starting Frontend Dev Server..." -ForegroundColor Cyan
npm run dev
"@

Start-Process powershell -ArgumentList "-NoExit", "-Command", $FrontendCommand

Write-Host "=====================================================================" -ForegroundColor Green
Write-Host "Both Frontend and Backend have been launched in separate windows!" -ForegroundColor Green
Write-Host "Frontend URL: http://localhost:5173" -ForegroundColor Green
Write-Host "Backend URL:  http://localhost:8085" -ForegroundColor Green
Write-Host "=====================================================================" -ForegroundColor Green
