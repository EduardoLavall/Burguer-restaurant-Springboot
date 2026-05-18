<#
run-with-env.ps1
Usage:
  .\run-with-env.ps1 [-StartDb] [-Port <port>] [-NoBuild]

Description:
  - Carrega variaveis do arquivo .env (se existir) para o ambiente da sessão.
  - Opcionalmente sobe o banco via `docker compose up -d` se passado `-StartDb`.
  - Executa `mvnw.cmd` para baixar dependencias/build (a menos que -NoBuild seja passado).
  - Executa `mvnw.cmd spring-boot:run` para iniciar a aplicacao.

Examples:
  # Start DB, build (skip tests) and run on default port (from .env or 8080)
  .\run-with-env.ps1 -StartDb

  # Run on porta 9090 without starting DB and without building
  .\run-with-env.ps1 -Port 9090 -NoBuild
#>

param(
    [switch]$StartDb,
    [int]$Port = $null,
    [switch]$NoBuild
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Push-Location $scriptDir

function Load-EnvFile {
    param([string]$path)
    if (-not (Test-Path $path)) { return }
    Write-Host "Loading environment from $path"
    Get-Content $path | ForEach-Object {
        $line = $_.Trim()
        if (-not $line) { return }
        if ($line -match '^[\s#]') { return }
        $parts = $line -split '=', 2
        if ($parts.Length -eq 2) {
            $name = $parts[0].Trim()
            $value = $parts[1].Trim()
            if ($name) {
                # expand simple ~ to HOME
                if ($value -like '~*') { $value = $value -replace '^~', $env:USERPROFILE }
                Set-Item -Path "Env:$name" -Value $value
            }
        }
    }
}

# Load .env if present
$envFile = Join-Path $scriptDir '.env'
Load-EnvFile -path $envFile

# Override port if passed
if ($Port) {
    Set-Item -Path "Env:SERVER_PORT" -Value $Port
    Write-Host "SERVER_PORT set to $Port"
}

# Optionally start DB via docker compose
if ($StartDb) {
    if (Get-Command docker -ErrorAction SilentlyContinue) {
        Write-Host "Starting docker compose services..."
        & docker compose up -d
    } else {
        Write-Warning "Docker not found in PATH. Skipping DB startup."
    }
}

# Ensure mvnw exists
$mvnw = Join-Path $scriptDir 'mvnw.cmd'
if (-not (Test-Path $mvnw)) { throw "mvnw.cmd not found in $scriptDir" }

# Build (download dependencies) unless NoBuild
if (-not $NoBuild) {
    Write-Host "Running mvnw clean package (dependencies will be downloaded). This may take a while..."
    & $mvnw clean package -DskipTests
}

# Run application
Write-Host "Starting application with mvnw spring-boot:run"
& $mvnw spring-boot:run

Pop-Location
