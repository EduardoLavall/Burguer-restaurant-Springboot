<#
run-with-env.ps1
Usage:
  .\run-with-env.ps1 [-Port <port>] [-NoBuild]

Description:
  - Carrega variaveis do arquivo .env (se existir) para o ambiente da sessao.
  - Usa `SQLite` como banco local oficial do projeto.
  - Sobe o frontend Vite automaticamente em paralelo.
  - Executa `mvnw.cmd` para baixar dependencias/build (a menos que -NoBuild seja passado).
  - Executa `mvnw.cmd spring-boot:run` para iniciar a aplicacao.

Examples:
  .\run-with-env.ps1
  .\run-with-env.ps1 -Port 9090 -NoBuild
#>

param(
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
                if ($value -like '~*') { $value = $value -replace '^~', $env:USERPROFILE }
                Set-Item -Path "Env:$name" -Value $value
            }
        }
    }
}

function Start-Frontend {
    param([string]$frontendDir)

    if (-not (Test-Path (Join-Path $frontendDir 'package.json'))) {
        Write-Warning "Frontend nao encontrado em $frontendDir. Pulando subida do Vite."
        return
    }

    $npmCmd = Get-Command npm.cmd -ErrorAction SilentlyContinue
    if (-not $npmCmd) {
        Write-Warning "npm.cmd nao encontrado no PATH. Pulando subida do frontend."
        return
    }

    $viteLogDir = Join-Path $scriptDir 'tmp'
    if (-not (Test-Path $viteLogDir)) {
        New-Item -ItemType Directory -Path $viteLogDir | Out-Null
    }

    $stdoutLog = Join-Path $viteLogDir 'frontend-vite.out.log'
    $stderrLog = Join-Path $viteLogDir 'frontend-vite.err.log'

    $processoFrontend = Start-Process `
        -FilePath $npmCmd.Source `
        -ArgumentList 'run', 'dev' `
        -WorkingDirectory $frontendDir `
        -WindowStyle Hidden `
        -RedirectStandardOutput $stdoutLog `
        -RedirectStandardError $stderrLog `
        -PassThru

    Write-Host "Frontend iniciado em segundo plano com PID $($processoFrontend.Id)."
    Write-Host "Logs do frontend: $stdoutLog"
}

$envFile = Join-Path $scriptDir '.env'
Load-EnvFile -path $envFile

if ($Port) {
    Set-Item -Path "Env:SERVER_PORT" -Value $Port
    Write-Host "SERVER_PORT set to $Port"
}

$frontendDir = Join-Path $scriptDir 'frontend'
Start-Frontend -frontendDir $frontendDir

$mvnw = Join-Path $scriptDir 'mvnw.cmd'
if (-not (Test-Path $mvnw)) { throw "mvnw.cmd not found in $scriptDir" }

if (-not $NoBuild) {
    Write-Host "Running mvnw clean package (dependencies will be downloaded). This may take a while..."
    & $mvnw clean package -DskipTests
}

Write-Host "Starting application with mvnw spring-boot:run"
& $mvnw spring-boot:run

Pop-Location
