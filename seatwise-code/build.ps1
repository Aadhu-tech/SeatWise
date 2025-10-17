Param(
    [string]$JavaHome = $env:JAVA_HOME,
    [string]$MysqlJar = "lib/mysql-connector-j-9.4.0/mysql-connector-j-9.4.0.jar",
    [string]$MainClass = "App"
)

$ErrorActionPreference = 'Stop'

# Use the directory where this script lives as project root
Push-Location $PSScriptRoot

Write-Host "Cleaning out directory..."
Remove-Item -Recurse -Force -ErrorAction SilentlyContinue out | Out-Null
New-Item -ItemType Directory -Force out | Out-Null

# Build classpath
$cp = "out;$MysqlJar"

# Find all .java files
$javaFiles = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }

if (-not $javaFiles) {
    throw "No Java files found under src"    
}

Write-Host "Compiling..."
$javac = if ($JavaHome) { Join-Path $JavaHome 'bin/javac.exe' } else { 'javac' }
& $javac -cp $cp -d out @javaFiles

# Copy resources (db.properties) to classpath root
Copy-Item -Force src/db.properties out/ | Out-Null

Write-Host "Running $MainClass ..."
$java = if ($JavaHome) { Join-Path $JavaHome 'bin/java.exe' } else { 'java' }
& $java -cp $cp $MainClass

Pop-Location
