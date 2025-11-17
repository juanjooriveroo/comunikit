# Manual de Despliegue - ComuniKIT

**Versión**: 1.0  
**Fecha**: 17 de noviembre de 2025  
**Autor**: Equipo ComuniKIT

---

## Tabla de Contenidos

1. [Requisitos Previos](#requisitos-previos)
2. [Pasos de Despliegue](#pasos-de-despliegue)
3. [Configuración de .env](#configuración-de-env)
4. [Configuración de FQDN](#configuración-de-fqdn)
5. [Validación del Despliegue](#validación-del-despliegue)
6. [Troubleshooting](#troubleshooting)

---

## Requisitos Previos

Asegúrate de tener instalado:

- **Docker** (v20.10+)
- **Docker Compose** (v2.0+)
- **Git** (para clonar el repositorio)
- **Un servidor** con acceso a internet (mínimo 2GB RAM libres, 5GB almacenamiento)
- **Dominio o FQDN** (puede ser duckdns.org o tu propio dominio)

### Verificar instalación:

```bash
docker --version
docker-compose --version
git --version
```

---

## Pasos de Despliegue

### **Paso 1: Clonar el Repositorio**

```bash
git clone https://github.com/juanjooriveroo/comunikit.git
cd comunikit/deploy
```

### **Paso 2: Configurar Variables de Entorno**

```bash
# Copiar el archivo de ejemplo
cp .env.example .env

# Editar con tu editor preferido
nano .env
```

### **Paso 3: Ajustar Configuración según tu Setup**

Ver sección [Configuración de .env](#configuración-de-env)

### **Paso 4: Configurar FQDN en Angular**

Editar `src/front/src/environments/environment.ts`:

```typescript
export const environment = {
  apiUrl: 'https://tu-dominio.com', // Cambiar a tu dominio
};
```

### **Paso 5: Lanzar Docker Compose**

```bash
cd deploy
docker compose up -d --build
```

### **Paso 6: Verificar Servicios**

```bash
docker compose ps
```

Deberías ver todos los servicios en estado `Up`.

---

## Configuración de .env

### **Escenario 1: Con duckdns.org (Sin SSL propio)**

**Env:**
```env
# === DUCKDNS ===
subdomain=mi-subdominio
token=mi-token

# === RED Y PUERTOS ===
NETWORK_NAME=internal
PORT_GATEWAY=8080
PORT_AUTH=8081
PORT_BOARD=8082
PORT_ADMIN=8083
PORT_NOTIFICATION=8084

# === DOMINIO ===
FQDN=mi-fqdn.duckdns.org
BACK_FQDN=back.mi-fqdn.duckdns.org

# === BASE DE DATOS ===
POSTGRES_USER=admin
POSTGRES_PASSWORD=mi-pass

# === JWT ===
JWT_SECRET=mi-secret-jwt
JWT_EXPIRATION=86400000

# === KAFKA ===
KAFKA_BOOTSTRAP_SERVERS=kafka:9092
KAFKA_CONSUMER_GROUP=notification-group

# === EMAIL ===
MAIL_HOST=smtp.mi-correo.com
MAIL_PORT=587
MAIL_USERNAME=usuario@mi-correo.com
MAIL_PASSWORD=mi-pass
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS=true
```

**Compose:**
```yml
services:
  # === DUCKDNS ===
  # Contenedor opcional si no tienes dominio propio. Mantiene actualizado tu IP pública
  duckdns:
    container_name: duckdns
    image: lscr.io/linuxserver/duckdns:latest
    environment:
      - PUID=1000
      - PGID=1000
      - TZ=Europe/Madrid
      - SUBDOMAINS=${subdomain}     # Se toma del .env
      - TOKEN=${token}              # Se toma del .env
      - UPDATE_IP=ipv4
      - LOG_FILE=false
    restart: unless-stopped
    networks:
      - ${NETWORK_NAME}

  # === CADDY ===
  # Proxy inverso que genera SSL automático y expone solo 80/443
  caddy:
    container_name: caddy
    image: lucaslorentz/caddy-docker-proxy:ci-alpine
    ports:
      - 80:80
      - 443:443/tcp
      - 443:443/udp
    environment:
      - CADDY_INGRESS_NETWORKS=${NETWORK_NAME}
    networks:
      - ${NETWORK_NAME}
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - caddy_data:/data
    restart: unless-stopped

  # === NGINX (Frontend Angular) ===
  # Sirve el frontend y lo expone a través de Caddy
  nginx:
    build: ../src/front
    container_name: frontend
    restart: unless-stopped
    labels:
      caddy: ${FQDN}                         # Dominio público
      caddy.reverse_proxy: "frontend:80"     # Redirige tráfico a NGINX
    networks:
      - ${NETWORK_NAME}
    # ports:                                  # Solo para testing local
    #   - 50000:80

  # === ZOOKEEPER ===
  # Coordinación para Kafka
  zookeeper:
    image: confluentinc/cp-zookeeper:7.4.0
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    networks:
      - ${NETWORK_NAME}
    restart: unless-stopped

  # === KAFKA ===
  # Broker de mensajes para microservicios
  kafka:
    image: confluentinc/cp-kafka:7.4.0
    container_name: kafka
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_INTERNAL://kafka:29092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_INTERNAL:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT_INTERNAL
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'true'
    networks:
      - ${NETWORK_NAME}
    restart: unless-stopped

  # === API GATEWAY ===
  gateway:
    build: ../src/back/api-gateway
    container_name: gateway
    expose:
      - "${PORT_GATEWAY}"                  # Expuesto solo a la red interna
    environment:
      - PORT_GATEWAY=${PORT_GATEWAY}
      - PORT_AUTH=${PORT_AUTH}
      - PORT_BOARD=${PORT_BOARD}
      - PORT_ADMIN=${PORT_ADMIN}
      - JWT_SECRET=${JWT_SECRET}
      - FQDN=${BACK_FQDN}
    labels:
      caddy: ${BACK_FQDN}
      caddy.reverse_proxy: "gateway:${PORT_GATEWAY}"
    depends_on:
      - auth-service
      - notification-service
    networks:
      - ${NETWORK_NAME}

  # === AUTH SERVICE ===
  auth-service:
    build: ../src/back/auth-service
    container_name: auth-service
    expose:
      - "${PORT_AUTH}"
    environment:
      - PORT_AUTH=${PORT_AUTH}
      - JWT_SECRET=${JWT_SECRET}
      - JWT_EXPIRATION=${JWT_EXPIRATION}
      - DB_HOST=postgres
      - DB_PORT=5432
      - DB_NAME=auth_service
      - DB_USER=${POSTGRES_USER}
      - DB_PASSWORD=${POSTGRES_PASSWORD}
      - KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS}
      - KAFKA_CONSUMER_GROUP=${KAFKA_CONSUMER_GROUP}
      - FQDN=${BACK_FQDN}
    depends_on:
      - kafka
      - postgres
    networks:
      - ${NETWORK_NAME}

  # === NOTIFICATION SERVICE ===
  notification-service:
    build: ../src/back/notification-service
    container_name: notification-service
    expose:
      - "${PORT_NOTIFICATION}"
    environment:
      - PORT_NOTIFICATION=${PORT_NOTIFICATION}
      - KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS}
      - KAFKA_CONSUMER_GROUP=${KAFKA_CONSUMER_GROUP}
      - MAIL_HOST=${MAIL_HOST}
      - MAIL_PORT=${MAIL_PORT}
      - MAIL_USERNAME=${MAIL_USERNAME}
      - MAIL_PASSWORD=${MAIL_PASSWORD}
      - MAIL_SMTP_AUTH=${MAIL_SMTP_AUTH}
      - MAIL_SMTP_STARTTLS=${MAIL_SMTP_STARTTLS}
      - FQDN=${BACK_FQDN}
    depends_on:
      - kafka
    networks:
      - ${NETWORK_NAME}

  # === POSTGRESQL ===
  postgres:
    image: postgres:16
    container_name: postgres
    environment:
      - POSTGRES_USER=${POSTGRES_USER}
      - POSTGRES_PASSWORD=${POSTGRES_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./db-init:/docker-entrypoint-initdb.d
    networks:
      - ${NETWORK_NAME}
    restart: unless-stopped

# === REDES ===
networks:
  internal:
    driver: bridge

# === VOLUMENES ===
volumes:
  caddy_data: {}
  postgres_data: {}
```

### **Escenario 2: Con Dominio Propio + SSL Existente (solo servicios / local)**

**Env:**
```env
# === RED Y PUERTOS ===
NETWORK_NAME=internal
PORT_GATEWAY=8080
PORT_AUTH=8081
PORT_BOARD=8082
PORT_ADMIN=8083
PORT_NOTIFICATION=8084

# === DOMINIO ===
FQDN=mi-fqdn.duckdns.org
BACK_FQDN=back.mi-fqdn.duckdns.org

# === BASE DE DATOS ===
POSTGRES_USER=admin
POSTGRES_PASSWORD=mi-pass

# === JWT ===
JWT_SECRET=mi-secret-jwt
JWT_EXPIRATION=86400000

# === KAFKA ===
KAFKA_BOOTSTRAP_SERVERS=kafka:9092
KAFKA_CONSUMER_GROUP=notification-group

# === EMAIL ===
MAIL_HOST=smtp.mi-correo.com
MAIL_PORT=587
MAIL_USERNAME=usuario@mi-correo.com
MAIL_PASSWORD=mi-pass
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS=true
```

**Compose:**
```yml
services:
  # === NGINX (Frontend Angular) ===
  # Sirve el frontend y lo expone a través de Caddy
  nginx:
    build: ../src/front
    container_name: frontend
    restart: unless-stopped
    networks:
      - ${NETWORK_NAME}
    ports:                                  
      - 80:80

  # === ZOOKEEPER ===
  # Coordinación para Kafka
  zookeeper:
    image: confluentinc/cp-zookeeper:7.4.0
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    networks:
      - ${NETWORK_NAME}
    restart: unless-stopped

  # === KAFKA ===
  # Broker de mensajes para microservicios
  kafka:
    image: confluentinc/cp-kafka:7.4.0
    container_name: kafka
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_INTERNAL://kafka:29092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_INTERNAL:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT_INTERNAL
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: 'true'
    networks:
      - ${NETWORK_NAME}
    restart: unless-stopped

  # === API GATEWAY ===
  gateway:
    build: ../src/back/api-gateway
    container_name: gateway
    expose:
      - "${PORT_GATEWAY}"                  
    environment:
      - PORT_GATEWAY=${PORT_GATEWAY}
      - PORT_AUTH=${PORT_AUTH}
      - PORT_BOARD=${PORT_BOARD}
      - PORT_ADMIN=${PORT_ADMIN}
      - JWT_SECRET=${JWT_SECRET}
      - FQDN=${BACK_FQDN}
    ports:          
      - 50000:80
    depends_on:
      - auth-service
      - notification-service
    networks:
      - ${NETWORK_NAME}

  # === AUTH SERVICE ===
  auth-service:
    build: ../src/back/auth-service
    container_name: auth-service
    expose:
      - "${PORT_AUTH}"
    environment:
      - PORT_AUTH=${PORT_AUTH}
      - JWT_SECRET=${JWT_SECRET}
      - JWT_EXPIRATION=${JWT_EXPIRATION}
      - DB_HOST=postgres
      - DB_PORT=5432
      - DB_NAME=auth_service
      - DB_USER=${POSTGRES_USER}
      - DB_PASSWORD=${POSTGRES_PASSWORD}
      - KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS}
      - KAFKA_CONSUMER_GROUP=${KAFKA_CONSUMER_GROUP}
      - FQDN=${BACK_FQDN}
    depends_on:
      - kafka
      - postgres
    networks:
      - ${NETWORK_NAME}

  # === NOTIFICATION SERVICE ===
  notification-service:
    build: ../src/back/notification-service
    container_name: notification-service
    expose:
      - "${PORT_NOTIFICATION}"
    environment:
      - PORT_NOTIFICATION=${PORT_NOTIFICATION}
      - KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS}
      - KAFKA_CONSUMER_GROUP=${KAFKA_CONSUMER_GROUP}
      - MAIL_HOST=${MAIL_HOST}
      - MAIL_PORT=${MAIL_PORT}
      - MAIL_USERNAME=${MAIL_USERNAME}
      - MAIL_PASSWORD=${MAIL_PASSWORD}
      - MAIL_SMTP_AUTH=${MAIL_SMTP_AUTH}
      - MAIL_SMTP_STARTTLS=${MAIL_SMTP_STARTTLS}
      - FQDN=${BACK_FQDN}
    depends_on:
      - kafka
    networks:
      - ${NETWORK_NAME}

  # === POSTGRESQL ===
  postgres:
    image: postgres:16
    container_name: postgres
    environment:
      - POSTGRES_USER=${POSTGRES_USER}
      - POSTGRES_PASSWORD=${POSTGRES_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./db-init:/docker-entrypoint-initdb.d
    networks:
      - ${NETWORK_NAME}
    restart: unless-stopped

# === REDES ===
networks:
  internal:
    driver: bridge

# === VOLUMENES ===
volumes:
  postgres_data: {}
```

---

## Configuración de FQDN

### **En Angular** (`src/front/src/environments/environment.ts`)

Después de configurar tu dominio, actualiza:

```typescript
export const environment = {
  apiUrl: 'https://api.tudominio.com',
};
```

### **En compose.yml**

El FQDN se obtiene automáticamente de la variable `FQDN` y `BACK_FQDN` en el `.env`.

---


### **2. Acceder a la Aplicación**

```
https://tu-dominio.com          # Frontend
https://back.tu-dominio.com     # Gateway
https://back.tu-dominio.com/documentacion  # Documentación API
```

### **3. Verificar Logs**

```bash
# Ver logs de un servicio específico
docker compose logs servicio
```

## Troubleshooting

### **Puerto 80/443 en uso**

```bash
# Encontrar qué proceso usa el puerto
sudo lsof -i :80
sudo lsof -i :443

# Matar el proceso
kill -9 <PID>

# O cambiar puertos en compose.yml
```

### **PostgreSQL no inicia**

```bash
# Ver logs
docker compose logs postgres

# Limpiar volumen (ADVERTENCIA: Borra datos)
docker volume rm deploy_postgres_data
docker compose up -d postgres
```

### **Certificados SSL vencidos**

```bash
# Renovar certificados (si usas Caddy)
docker compose exec caddy caddy reload

# O eliminar caché y regenerar
docker volume rm deploy_caddy_data
docker compose down
docker compose up -d --build

# CUIDADO, Caddy proporciona 5 certificados cada 160h, si los consumes tendrás que esperar
```

---

## Soporte

Si tienes problemas:

1. **Revisa los logs**: `docker compose logs -f`
2. **Verifica la documentación API**: `https://back.comunikit.duckdns.org/documentacion`
3. **Consulta el README**: `README.md` en la raíz del proyecto
4. **Abre un issue** en el repositorio

---

**Última actualización**: 17 de noviembre de 2025