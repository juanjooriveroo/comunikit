# Sprint 1 Backlog - ComuniKIT
## Infraestructura + Autenticación

**Fecha inicio**: 5 de noviembre de 2025  
**Fecha fin**: 18 de noviembre de 2025  
**Duración**: 2 semanas 

---

## Objetivo del Sprint

> **"Crear un sistema desplegable con Docker que permita a tutores registrarse y crear usuarios finales con autenticación JWT funcional"**

Al finalizar este Sprint, debería tener:
- Infraestructura Docker Compose operativa (PostgreSQL + Kafka)
- API Gateway configurado y enrutando
- Auth-service con login/register funcional
- Frontend Angular base con WCAG AA
- HU01 y HU02 completamente implementadas

---

## Historias de Usuario

### HU01 - Registro de Tutor 
**Estado**: ⏳ To Do

**Como** Tutor  
**Quiero** crear mi cuenta y acceder a mi panel  
**Para** poder gestionar usuarios y tableros asociados

#### Criterios de Aceptación
- [ ] **AC1**: Existe un formulario de registro con campos: nombre, email, idioma, contraseña
- [ ] **AC2**: El email debe ser único en el sistema (validación backend)
- [ ] **AC3**: La contraseña debe tener mínimo 8 caracteres (validación frontend y backend)
- [ ] **AC4**: La contraseña se cifra con BCrypt antes de guardar
- [ ] **AC5**: Al registrarse, se crea automáticamente un token JWT
- [ ] **AC6**: El usuario es redirigido automáticamente a su panel tras registro exitoso
- [ ] **AC7**: Se muestra mensaje de error si el email ya existe
- [ ] **AC8**: El formulario cumple WCAG 2.1 AA
- [ ] **AC9**: Se debe de confirmar cuenta por email

---

### HU02 - Creación de Usuario final
**Estado**: ⏳ To Do  
**Dependencias**: HU01

**Como** Tutor o Educador  
**Quiero** crear un Usuario final con su tablero personal  
**Para** que pueda comunicarse mediante pictogramas

#### Criterios de Aceptación
- [ ] **AC1**: Existe un formulario de creación con campos: nombre, idioma
- [ ] **AC2**: El sistema genera automáticamente una contraseña segura
- [ ] **AC3**: Se crea un tablero personal asociado al usuario
- [ ] **AC4**: El tablero se clona del tablero público del idioma seleccionado
- [ ] **AC5**: El tutor/educador que crea el usuario queda asignado automáticamente
- [ ] **AC6**: Se asigna un límite de 50MB de almacenamiento (campo `storage_used` inicializado a 0)
- [ ] **AC7**: Solo usuarios con rol TUTOR pueden acceder a este endpoint
- [ ] **AC8**: El formulario cumple WCAG 2.1 AA

---

## Tareas de Infraestructura

### Docker & DevOps
- [x] **I1**: Crear `docker-compose.yml`  
- [x] **I2**: Crear archivo `.env.example` 
- [x] **I3**: Configurar estructura de carpetas `/src`

### API Gateway
- [x] **I4**: Crear proyecto Spring Cloud Gateway `api-gateway`  
- [x] **I5**: Configurar routing en `application.yml`
- [x] **I6**: Configurar CORS en Gateway

### Documentación
- [ ] **D1**: Configurar Swagger/OpenAPI en auth-service
- [x] **D2**: Crear README.md del proyecto
- [ ] **D3**: Documentar endpoints en Swagger

---

## Diseño y Accesibilidad

### Guía de Estilo
- [x] **DS1**: Definir paleta de colores con contraste AA
  - Primario: #3B82F6 (azul)
  - Secundario: #10B981 (verde)
  - Error: #EF4444 (rojo)
  - Fondo: #FFFFFF
  - Texto: #1F2937
  
- [x] **DS2**: Definir tipografía
  - Fuente: Inter (Google Fonts)
  - Tamaños: 14px (body), 16px (inputs), 24px (h1)
  
---

## Definition of Done - Sprint 1

Una historia se considera DONE cuando:
- Código implementado y funcionando en local
- Tests unitarios escritos y pasando (coverage > 70%)
- Documentación Swagger actualizada
- Cumple criterios de aceptación
- Cumple WCAG 2.1 AA
- Revisión de código (self-review o profesor)
- Docker Compose levanta sin errores
- Commit con mensaje descriptivo en Git

---

## Registro del dia

### Semana 1 (12/11 - 16/11)
**Día 1 (12/11)**: 
- Creación de la documentación inicial del proyecto (backlogs, README)
- Creación de docker-compose con todas las dependencias necesarias para el despliegue completo
- Creación de la primera base de datos para auth-service

**Día 2 (13/11)**:
- Creación de subdominio duckdns para la aplicación
- Montaje de servidor Ubuntu Server para hostear la aplicación mediante Docker
- Test de funcionamiento del despliegue

**Día 3 (14/11)**: 
- Creación del Gateway de microservicios
- Modificación del compost.yml y .env para desplegarlo junto a sus parámetros de entorno
- Lectura y validación de tokens y redirección a microservicios específicos
- Implementación completa de Swagger para documentación de endpoints
- Inyección de datos de token por cabecera para enviar a los microservicios información
- Tests para validar el funcionamiento de validación de tokens y redirección
- Configuración de CORS habilitados únicamente para la página a desarrollar

**Día 4 (15/11)**: 

**Día 5 (16/11)**:

### Semana 2 (12/11 - 18/11)
**Día 6 (17/11)**:

**Día 7 (18/11)**:

---

## Métricas de Éxito

Al final del Sprint 1, debería poder demostrar:
- Un tutor puede registrarse desde el formulario
- El tutor recibe un token JWT válido
- El tutor puede crear un usuario final
- El usuario final tiene un tablero clonado
- Todo el flujo cumple WCAG 2.1 AA
- Docker Compose levanta toda la infraestructura
- Tests pasan (coverage > 70%)