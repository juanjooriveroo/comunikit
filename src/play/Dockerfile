# Build stage
FROM node:20-alpine AS builder

WORKDIR /app

# Copiar archivos de dependencias
COPY package*.json ./

# Instalar dependencias
RUN npm install

# Copiar código fuente
COPY . .

# Argumentos de build
ARG PUBLIC_API_URL
ENV PUBLIC_API_URL=${PUBLIC_API_URL}

# Build de la aplicación
RUN npm run build

# Production stage
FROM nginx:alpine

# Copiar configuración de nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copiar archivos estáticos desde el builder
COPY --from=builder /app/dist /usr/share/nginx/html

# Exponer puerto
EXPOSE 80

# Comando por defecto
CMD ["nginx", "-g", "daemon off;"]
