#!/usr/bin/env bash
# run-with-env.sh
# Usage: ./run-with-env.sh [--start-db] [--port PORT] [--no-build]
# Loads .env, optionally starts docker compose, builds and runs the Spring Boot app using mvnw.

set -euo pipefail
script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
cd "$script_dir"

START_DB=false
NO_BUILD=false
PORT=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    --start-db) START_DB=true; shift ;;
    --no-build) NO_BUILD=true; shift ;;
    --port) PORT="$2"; shift 2 ;;
    -p) PORT="$2"; shift 2 ;;
    --help|-h) echo "Usage: $0 [--start-db] [--port PORT] [--no-build]"; exit 0 ;;
    *) echo "Unknown option: $1"; exit 1 ;;
  esac
done

# Load .env
if [ -f .env ]; then
  echo "Loading .env"
  # export vars from .env safely
  while IFS='=' read -r key value; do
    # skip empty or comment lines
    if [[ -z "$key" || "$key" =~ ^[[:space:]]*# ]]; then
      continue
    fi
    key=$(echo "$key" | xargs)
    value=$(echo "${value:-}" | xargs)
    export "$key=$value"
  done < <(grep -v '^\s*#' .env | sed -n 's/\r$//p') || true
fi

# Override port if passed
if [ -n "$PORT" ]; then
  export SERVER_PORT="$PORT"
  echo "SERVER_PORT set to $SERVER_PORT"
fi

# Optionally start DB
if [ "$START_DB" = true ]; then
  if command -v docker >/dev/null 2>&1; then
    echo "Starting docker compose services..."
    docker compose up -d
  else
    echo "Docker not found in PATH. Skipping DB startup." >&2
  fi
fi

# Ensure mvnw exists
if [ ! -f mvnw ] && [ ! -f mvnw.cmd ]; then
  echo "mvnw wrapper not found" >&2
  exit 1
fi

# Build unless NO_BUILD
if [ "$NO_BUILD" = false ]; then
  echo "Running ./mvnw clean package -DskipTests"
  if [ -f mvnw ]; then
    ./mvnw clean package -DskipTests
  else
    ./mvnw.cmd clean package -DskipTests
  fi
fi

# Run application
echo "Starting application"
if [ -f mvnw ]; then
  ./mvnw spring-boot:run
else
  ./mvnw.cmd spring-boot:run
fi
