# Sprint 2 Backlog - ComuniKIT
## Infraestructura + Autenticación

**Fecha inicio**: 19 de noviembre de 2025  
**Fecha fin**: 17 de diciembre de 2025  
**Duración**: 5 semanas 

---

## Objetivo del Sprint

> **"Desarrollar más en profundidad la configuración y usos del usuario y comenzar con la subida y carga de pictogramas y tablero"**

Al finalizar este Sprint, debería tener:
- Usuarios con todos los casos finalizados (modificación de datos, eliminación de la cuenta, crear usuarios dependientes)
- Board-Service comenzado y ya integrariamos una base de datos donde habrian tableros y pictogramas default por idiomas y la posibilidad de subir pictogramas
- Agregar capacidad de lenguajes para Angular (los ya definidos previamente)
- HU03 y HU04 completamente implementadas y finalizar el HU02

---

## Historias de Usuario

### HU03 - Subida de pictogramas
**Estado**: ⏳ To Do  
**Dependencias**: Ninguna

**Como** Tutor  
**Quiero** subir pictogramas personalizados  
**Para** adaptarlos a las necesidades de mis usuarios

#### Criterios de Aceptación
- [ ] **AC1**: Upload de imágenes (PNG, JPG, JPEG)
- [ ] **AC2**: Máximo 3MB por archivo
- [ ] **AC3**: Validación de tipo MIME en backend
- [ ] **AC4**: Conversión automática a BYTEA en PostgreSQL
- [ ] **AC5**: Asociación de texto al pictograma
- [ ] **AC6**: Control de cuota de 50MB por cuenta
- [ ] **AC7**: Componente de Angular para la subida de pictograma

### HU04 - Creación de secciones privadas
**Estado**: ⏳ To Do  
**Dependencias**: HU03

**Como** Tutor  
**Quiero** crear secciones para el tablero de mi usuario  
**Para** organizar pictogramas por categorías

#### Criterios de Aceptación
- [ ] **AC1**: Formulario con: nombre, idioma, categoría
- [ ] **AC2**: Asociación de pictogramas a la sección
- [ ] **AC3**: Secciones privadas (no visibles por otros)
- [ ] **AC4**: Componentes Angular de creación y configuración de secciones

---

## Tareas generales

### Docker & DevOps
- [ ] **G01**: Crear `board-service.sql`

### Auth-Service
- [x] **G02**: Modificación de datos de usuario
- [x] **G03**: Eliminación de datos de usuario (baja)
- [x] **G04**: Creación de usuarios dependientes
- [x] **G05**: Gestión de usuarios dependientes

### Notification-Service
- [x] **G06**: Confirmación por email de eliminación de datos de usuario (baja)

### Board-Service
- [ ] **G07**: Crear proyecto `board-service`
- [ ] **G08**: Creación de pictogramas
- [ ] **G09**: Modificación de pictogramas
- [ ] **G10**: Creación de secciones
- [ ] **G11**: Modificación de secciones
- [ ] **G12**: Configurar Swagger/OpenAPI
- [ ] **G13**: Documentar endpoints en Swagger

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

## Definition of Done - Sprint 2

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

### Semana 1 (19/11 - 23/11)
**Día 1 (19/11)**: 
- Creación de documentación del Backlog del sprint2
- Baja de usuario (SOLAMENTE LA DE USUARIO PADRE, CUANDO SE PUEDAN TENER CUENTAS HIJA SE MODIFICARÁ)
- Correo de confirmación de baja
- Arreglar problemas del Jwt de como se devolvian los roles a los microservicios en el api-gateway
- Componentes Angular de la baja de usuarios (confirmacion, peticion por modal)
- Compra de dominio personal y aplicación de ella en el servidor

**Día 2 (20/11)**: 
- Fixeo fallos de resolución de navbar en resoluciones moviles y que los admins no puedan crear usuarios del mismo modo que los tutores (sprint 3 todo)

**Día 3 (21/11)**: 
- Creación tanto en el back como en el front de usuarios dependientes (via usuario tutor).
- Cambio de infraestructura del servidor (cambié de equipo que hostea todo el servicio)

**Día 4 (22/11)**:
- Modificación de perfiles tanto para uno mismo como para dependientes.
- Modificación de contraseñas para uno mismo.
- Implementación tanto en el backend como en el front ya funcional.

**Día 5 (23/11)**:
- Gestión de cuentas dependientes (visualizar los perfiles creados, editar sus datos o eliminarlos)
- Modificación del backend para adaptar los endpoints de borrado de cuenta y edición de cuenta a uso tanto propio como de cuentas derivadas
- Añadimos los endpoints de auth-services que necesitan estar autenticados al gateway como necesarios de autenticación (devuelve 401 si no pasas token los que requieren de usuario)
---

## Métricas de Éxito

Al final del Sprint 2, debería poder demostrar:
- Un tutor puede crear usuarios dependientes
- El tutor puede modificar sus datos como el de los usuarios dependientes a el
- El tutor puede eliminar su cuenta junto a la de usuarios dependientes o las dependientes individualmente
- El usuario final tiene un tablero clonado
- El tutor puede crear pictogramas
- El tutor puede modificar los pictogramas
- El tutor puede crear secciones
- El tutor puede modificar las secciones (nombre o añadir/quitar pictogramas)
- Todo el flujo cumple WCAG 2.1 AA
- Docker Compose levanta toda la infraestructura
- Tests pasan (coverage > 70%)