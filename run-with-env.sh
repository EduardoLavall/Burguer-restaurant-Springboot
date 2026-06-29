#!/usr/bin/env bash
# run-with-env.sh
# Usage: ./run-with-env.sh [--port PORT] [--no-build]
# Loads .env, starts the Vite frontend and runs the Spring Boot app using mvnw com SQLite como banco local oficial.

set -euo pipefail
script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
cd "$script_dir"

NO_BUILD=false
PORT=""
FRONTEND_PID=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    --no-build) NO_BUILD=true; shift ;;
    --port) PORT="$2"; shift 2 ;;
    -p) PORT="$2"; shift 2 ;;
    --help|-h) echo "Usage: $0 [--port PORT] [--no-build]"; exit 0 ;;
    *) echo "Unknown option: $1"; exit 1 ;;
  esac
done

if [ -f .env ]; then
  echo "Loading .env"
  while IFS='=' read -r key value; do
    if [[ -z "$key" || "$key" =~ ^[[:space:]]*# ]]; then
      continue
    fi
    key=$(echo "$key" | xargs)
    value=$(echo "${value:-}" | xargs)
    export "$key=$value"
  done < <(grep -v '^\s*#' .env | sed -n 's/\r$//p') || true
fi

if [ -n "$PORT" ]; then
  export SERVER_PORT="$PORT"
  echo "SERVER_PORT set to $SERVER_PORT"
fi

cleanup() {
  if [ -n "${FRONTEND_PID:-}" ] && kill -0 "$FRONTEND_PID" >/dev/null 2>&1; then
    kill "$FRONTEND_PID" >/dev/null 2>&1 || true
  fi
}

trap cleanup EXIT INT TERM

if [ -f frontend/package.json ]; then
  mkdir -p tmp
  echo "Starting frontend with Vite in background"
  (
    cd frontend
    npm run dev
  ) >"$script_dir/tmp/frontend-vite.out.log" 2>"$script_dir/tmp/frontend-vite.err.log" &
  FRONTEND_PID=$!
  echo "Frontend iniciado em segundo plano com PID $FRONTEND_PID"
  echo "Logs do frontend: $script_dir/tmp/frontend-vite.out.log"
else
  echo "Frontend nao encontrado em $script_dir/frontend. Pulando subida do Vite." >&2
fi

if [ ! -f mvnw ] && [ ! -f mvnw.cmd ]; then
  echo "mvnw wrapper not found" >&2
  exit 1
fi

if [ "$NO_BUILD" = false ]; then
  echo "Running ./mvnw clean package -DskipTests"
  if [ -f mvnw ]; then
    ./mvnw clean package -DskipTests
  else
    ./mvnw.cmd clean package -DskipTests
  fi
fi

echo "Starting application"
if [ -f mvnw ]; then
  ./mvnw spring-boot:run
else
  ./mvnw.cmd spring-boot:run
fi
