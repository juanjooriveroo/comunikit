#!/bin/bash

# ===============================================
# TriviaRush Notification Service - Build Script
# ===============================================

set -e  # Salir en caso de error

echo "🚀 Iniciando build del microservicio de notificaciones..."

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Directorio del proyecto
PROJECT_DIR="/Users/jurrilo/Documents/GITHUB/triviaRush/triviaRush_notification"
DEPLOY_DIR="/Users/jurrilo/Documents/GITHUB/triviaRush/triviaRush_deploy/apis"

echo -e "${BLUE}📁 Directorio del proyecto: ${PROJECT_DIR}${NC}"
echo -e "${BLUE}📦 Directorio de deploy: ${DEPLOY_DIR}${NC}"

# Navegar al directorio del proyecto
cd "${PROJECT_DIR}"

echo -e "${YELLOW}🧹 Limpiando builds anteriores...${NC}"
./mvnw clean

echo -e "${YELLOW}🔧 Compilando y empaquetando...${NC}"
./mvnw package -DskipTests

# Verificar que el JAR se creó correctamente
JAR_FILE=$(find target -name "*.jar" -not -name "*-sources.jar" | head -1)

if [ -z "$JAR_FILE" ]; then
    echo -e "${RED}❌ Error: No se encontró el archivo JAR${NC}"
    exit 1
fi

echo -e "${GREEN}✅ JAR creado: ${JAR_FILE}${NC}"

# Copiar JAR al directorio de deploy con nombre descriptivo
JAR_NAME="triviaRush-notification-service.jar"
cp "${JAR_FILE}" "${DEPLOY_DIR}/${JAR_NAME}"

echo "✅ JAR copiado a: $DEPLOY_DIR/triviaRush-notification-service.jar"

# La configuración está centralizada - no se necesitan archivos adicionales
DEPLOY_ROOT=$(dirname "$DEPLOY_DIR")
echo "📋 Configuración centralizada en: $DEPLOY_ROOT/.env"

# Mostrar información del JAR
echo -e "${BLUE}📋 Información del JAR:${NC}"
ls -lh "${DEPLOY_DIR}/${JAR_NAME}"

echo -e "${GREEN}🎉 Build completado exitosamente!${NC}"
echo -e "${BLUE}📍 Archivos listos en: ${DEPLOY_DIR}${NC}"
echo -e "${YELLOW}⚠️  Recuerda configurar las variables de entorno en $DEPLOY_ROOT/.env antes de deploy${NC}"
