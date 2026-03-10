#!/bin/bash

# Colores para la terminal
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 Iniciando infraestructura de Core Bank...${NC}"

# 1. Iniciar Docker
echo -e "${GREEN}🐳 Iniciando Docker (Postgres & Keycloak)...${NC}"
docker compose up -d

# 2. Iniciar Backend (Spring Boot)
echo -e "${GREEN}☕ Iniciando Backend (Spring Boot)...${NC}"
# Usamos & para correrlo en segundo plano y redirigimos logs mínimos
mvn spring-boot:run > backend.log 2>&1 &
BACKEND_PID=$!

# 3. Iniciar Frontend (React)
echo -e "${GREEN}⚛️ Iniciando Frontend (Vite)...${NC}"
cd banka-ui
npm run dev &
FRONT_PID=$!

echo -e "${BLUE}--------------------------------------------------${NC}"
echo -e "✅ ¡Todo está en marcha!"
echo -e "👉 Backend: http://localhost:8080 (Logs en backend.log)"
echo -e "👉 Frontend: http://localhost:5173"
echo -e "👉 Keycloak: http://localhost:7080"
echo -e "${BLUE}--------------------------------------------------${NC}"
echo -e "Presiona Ctrl+C para detener todo (próximamente)."

# Función para detener todo al salir
trap "kill $BACKEND_PID $FRONT_PID; exit" INT TERM
wait
