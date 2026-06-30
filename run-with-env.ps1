<#
run-with-env.ps1
Usage:
  .\run-with-env.ps1 [-Port <port>] [-NoBuild]

Description:
  - Carrega variaveis do arquivo .env (se existir) para o ambiente da sessao.
  - Usa `SQLite` como banco local oficial do projeto.
  - Sobe frontend e backend em segundo plano.
  - Ignora processos que ja estiverem rodando nas portas do projeto.
  - Executa `mvnw.cmd` para baixar dependencias/build (a menos que -NoBuild seja passado).

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

function Ensure-LogDir {
    $logDir = Join-Path $scriptDir 'tmp'
    if (-not (Test-Path $logDir)) {
        New-Item -ItemType Directory -Path $logDir | Out-Null
    }
    return $logDir
}

function Test-PortListening {
    param([int]$Port)

    return [bool](Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue)
}

function Start-Frontend {
    param(
        [string]$frontendDir,
        [int]$frontendPort = 5173
    )

    if (-not (Test-Path (Join-Path $frontendDir 'package.json'))) {
        Write-Warning "Frontend nao encontrado em $frontendDir. Pulando subida do Vite."
        return
    }

    $npmCmd = Get-Command npm.cmd -ErrorAction SilentlyContinue
    if (-not $npmCmd) {
        Write-Warning "npm.cmd nao encontrado no PATH. Pulando subida do frontend."
        return
    }

    if (Test-PortListening -Port $frontendPort) {
        Write-Host "Frontend ja esta rodando na porta $frontendPort. Ignorando nova subida."
        return
    }

    $viteLogDir = Ensure-LogDir
    $stdoutLog = Join-Path $viteLogDir 'frontend-vite.out.log'
    $stderrLog = Join-Path $viteLogDir 'frontend-vite.err.log'

    $processoFrontend = Start-Process `
        -FilePath $npmCmd.Source `
        -ArgumentList 'run', 'dev', '--', '--host', '0.0.0.0', '--port', "$frontendPort" `
        -WorkingDirectory $frontendDir `
        -WindowStyle Hidden `
        -RedirectStandardOutput $stdoutLog `
        -RedirectStandardError $stderrLog `
        -PassThru

    Write-Host "Frontend iniciado em segundo plano com PID $($processoFrontend.Id)."
    Write-Host "Logs do frontend: $stdoutLog"
}

function Start-Backend {
    param(
        [string]$mvnwPath,
        [int]$backendPort = 8080,
        [switch]$SkipBuild
    )

    if (Test-PortListening -Port $backendPort) {
        Write-Host "Backend ja esta rodando na porta $backendPort. Ignorando nova subida."
        return
    }

    $logDir = Ensure-LogDir
    $stdoutLog = Join-Path $logDir 'backend-spring.out.log'
    $stderrLog = Join-Path $logDir 'backend-spring.err.log'

    $argumentos = if ($SkipBuild) {
        '/c', 'mvnw.cmd -Dmaven.test.skip=true spring-boot:run'
    } else {
        '/c', 'mvnw.cmd -Dmaven.test.skip=true clean spring-boot:run'
    }

    $processoBackend = Start-Process `
        -FilePath 'cmd.exe' `
        -ArgumentList $argumentos `
        -WorkingDirectory $scriptDir `
        -WindowStyle Hidden `
        -RedirectStandardOutput $stdoutLog `
        -RedirectStandardError $stderrLog `
        -PassThru

    Write-Host "Backend iniciado em segundo plano com PID $($processoBackend.Id)."
    Write-Host "Logs do backend: $stdoutLog"
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

$backendPort = 8080
if ($env:SERVER_PORT) {
    $backendPort = [int]$env:SERVER_PORT
}

Start-Backend -mvnwPath $mvnw -backendPort $backendPort -SkipBuild:$NoBuild

Write-Host "Script finalizado. Frontend e backend foram iniciados em paralelo quando necessario."

Pop-Location
