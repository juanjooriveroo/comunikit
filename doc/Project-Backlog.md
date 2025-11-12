# Project Backlog - ComuniKIT

**Fecha de inicio**: 12 de noviembre de 2025  
**Fecha de fin estimada**: 28 de enero de 2026  
**Duración total**: 3 Sprints (12 semanas)

---

## Objetivos Estratégicos

1. **Accesibilidad Universal**: Cumplir WCAG 2.1 AA desde el día 1
2. **Escalabilidad**: Arquitectura de microservicios con Kafka
3. **Privacidad**: Cumplimiento RGPD simulado
4. **Usabilidad**: Interfaz intuitiva adaptada a tablets y móviles
5. **Profesionalidad**: Código de calidad, documentado y testeado

---

## Stakeholders

- **Usuario final**: Persona con dificultades de comunicación
- **Tutores**: Padres o tutores legales
- **Administradores**: Gestores del sistema
- **Supervisor académico**: Miguel Jaque
- **Desarrollador**: Juan José Rivero Lorido

---

## Épicas del Proyecto

### Epic 1: Infraestructura y DevOps
**Descripción**: Configurar la base técnica del proyecto con Docker, CI/CD y arquitectura de microservicios.

**Valor de negocio**: Fundamental para el desarrollo ágil y despliegue escalable.

**Historias relacionadas**:
- Configuración de Docker Compose
- Setup de PostgreSQL y Kafka
- Configuración de API Gateway
- Estructura de repositorio Git

---

### Epic 2: Autenticación y Gestión de Usuarios
**Descripción**: Sistema completo de login, registro, roles y permisos.

**Valor de negocio**: Seguridad y control de acceso esencial para cumplir RGPD.

**Historias relacionadas**:
- HU01: Registro de Tutor
- HU02: Creación de Usuario final
- Sistema JWT
- Gestión de roles (USUARIO, TUTOR, EDUCADOR, ADMIN, INVITADO)

---

### Epic 3: Gestión de Tableros y Pictogramas
**Descripción**: CRUD completo de tableros, secciones y pictogramas con subida de imágenes.

**Valor de negocio**: Funcionalidad core de la aplicación.

**Historias relacionadas**:
- HU03: Subida de pictogramas
- HU04: Creación de secciones privadas
- HU05: Composición de frases
- HU06: Tablero público para invitados
- Integración TTS (Text-to-Speech)

---

### Epic 4: Administración y Soporte
**Descripción**: Sistema de tickets, moderación de contenido y auditoría.

**Valor de negocio**: Mantenimiento y soporte post-lanzamiento.

**Historias relacionadas**:
- HU07: Gestión de tickets
- Panel de administración
- Sistema de notificaciones por email

---

## Historias de Usuario

#### HU01 - Registro de Tutor
**Como** Tutor  
**Quiero** crear mi cuenta y acceder a mi panel  
**Para** poder gestionar usuarios y tableros asociados

**Criterios de aceptación**:
- Formulario de registro con validación frontend y backend
- Campos: nombre, email, idioma, contraseña (mín 8 caracteres)
- Email único en el sistema
- Confirmación por email
- Redirección automática al panel tras registro

**Sprint**: Sprint 1  
**Dependencias**: Ninguna

**Tareas técnicas**:
- [ ] Endpoint POST `/api/auth/register-tutor`
- [ ] Validación de email único
- [ ] Cifrado BCrypt de contraseña
- [ ] Creación de token JWT
- [ ] Componente Angular de registro
- [ ] Validaciones reactivas (FormGroup)

---

#### HU02 - Creación de Usuario final
**Como** Tutor  
**Quiero** crear un Usuario final con su tablero personal  
**Para** que pueda comunicarse mediante pictogramas

**Criterios de aceptación**:
- Formulario con campos: nombre, idioma
- Generación automática de credenciales seguras
- Clonación automática del tablero público según idioma
- Asignación automática del tutor que lo crea
- Límite de 50MB de almacenamiento asignado

**Sprint**: Sprint 1  
**Dependencias**: H01

**Tareas técnicas**:
- [ ] Endpoint POST `/api/users`
- [ ] Lógica de clonación de tablero público
- [ ] Generación de contraseña aleatoria segura
- [ ] Validación de límite de usuarios por tutor
- [ ] Componente Angular de creación de usuario
- [ ] Guard de autorización (TUTOR)

---

#### HU03 - Subida de pictogramas
**Como** Tutor  
**Quiero** subir pictogramas personalizados  
**Para** adaptarlos a las necesidades de mis usuarios

**Criterios de aceptación**:
- Upload de imágenes (PNG, JPG, JPEG)
- Máximo 3MB por archivo
- Validación de tipo MIME en backend
- Conversión automática a BYTEA en PostgreSQL
- Asociación de texto al pictograma
- Control de cuota de 50MB por cuenta

**Sprint**: Sprint 2  
**Dependencias**: HU02

**Tareas técnicas**:
- [ ] Endpoint POST `/api/pictogramas`
- [ ] Validación de MIME type (Java)
- [ ] Almacenamiento en PostgreSQL (BYTEA)
- [ ] Cálculo de espacio utilizado
- [ ] Componente Angular de upload

---

#### HU04 - Creación de secciones privadas
**Como** Tutor  
**Quiero** crear secciones para el tablero de mi usuario  
**Para** organizar pictogramas por categorías

**Criterios de aceptación**:
- Formulario con: nombre, idioma, categoría
- Asociación de pictogramas a la sección
- Secciones privadas (no visibles por otros)
 
**Sprint**: Sprint 2  
**Dependencias**: HU02

**Tareas técnicas**:
- [ ] Endpoint POST `/api/tablero/secciones`
- [ ] Lógica de asociación
- [ ] Componente Angular de creación de sección

---

#### HU05 - Composición de frases
**Como** Usuario final  
**Quiero** seleccionar pictogramas y reproducir la frase por voz  
**Para** comunicar mis ideas

**Criterios de aceptación**:
- Click en pictogramas los añade a la barra de composición
- Botón de reproducir genera audio con TTS
- Botón de limpiar borra la composición
- TTS generado en el cliente (Web Speech API)
- Funciona sin conexión una vez cargado
- Soporte de idiomas (ES, EN, FR, DE, PT)
 
**Sprint**: Sprint 3  
**Dependencias**: HU03, HU04

**Tareas técnicas**:
- [ ] Servicio Angular de TTS
- [ ] Componente de barra de composición
- [ ] Gestión de estado (RxJS)
- [ ] Integración de diferentes idiomas

---

#### HU06 - Tablero público para invitados
**Como** Invitado  
**Quiero** acceder a un tablero público según idioma elegido  
**Para** probar la aplicación

**Criterios de aceptación**:
- Acceso sin login
- Selector de idioma en landing page
- Tablero genérico con secciones predefinidas
- Solo lectura (no editable)
- TTS funcional
- No se guardan datos
 
**Sprint**: Sprint 3  
**Dependencias**: HU05

**Tareas técnicas**:
- [ ] Endpoint GET `/api/public/tablero/{idioma}`
- [ ] UUID de tableros públicos en BD
- [ ] Componente Angular de landing page
- [ ] Guard para permitir acceso público

---

#### HU07 - Gestión de tickets
**Como** Admin  
**Quiero** gestionar los tickets enviados por tutores y educadores  
**Para** resolver incidencias

**Criterios de aceptación**:
- Listado de tickets ordenados por fecha
- Filtros: pendiente, en progreso, cerrado
- Respuesta a tickets por email
- Cambio de estado
- Notificación por email al cerrar ticket
 
**Sprint**: Sprint 3  
**Dependencias**: HU01

**Tareas técnicas**:
- [ ] Endpoints CRUD en admin-service
- [ ] Modelo de datos Ticket
- [ ] Publicación de evento Kafka `ticket.closed`
- [ ] Panel de administración en Angular

## Diseño y UX

### Guía de Estilo
- **Colores primarios**: Azul (#3B82F6), Verde (#10B981)
- **Tipografía**: Inter (sans-serif)
- **Espaciado**: Sistema de 4px
- **Componentes**: Material Design adaptado a WCAG AA
- **Responsive**: Mobile-first, optimizado para tablets

---

## Modelo de Datos

### Entidades Principales

```
User (id, name, email, password, rol_id, language_id, storage_used)
  ├─ Board (id, name, public, user_id, language_id)
  │    ├─ Board_Section (relation_id, board_id, section_id)
  │    ├─ Board_Pictogram (relation_id, board_id, pictogram_id)
  │    └─ Language (id, code, name)
  │
  ├─ Section (id, name, public, category_id, language_id, owner_id)
  │    ├─ Section_Pictogram (relation_id, section_id, pictogram_id)
  │    ├─ Category (id, name)
  │    └─ Language (id, code, name)
  │
  ├─ Pictogram (id, name, text, public, image_id, owner_id)
  │    └─ Image (id, name, mime_type, data, size)
  │
  ├─ User_Relation (relation_id, origin_user_id, destination_user_id)
  ├─ Ticket (id, user_id, asunto, descripcion, estado, fecha)
  └─ Rol (id, name)
```

---

## Requerimientos No Funcionales

| Código | Categoría | Descripción | Prioridad |
|--------|-----------|-------------|-----------|
| RNF01 | Seguridad | Cifrado BCrypt de contraseñas | ALTA |
| RNF02 | Seguridad | Control de acceso JWT + Spring Security | ALTA |
| RNF03 | Seguridad | Validación estricta MIME (3MB max) | ALTA |
| RNF04 | Privacidad | Cumplimiento RGPD simulado | ALTA |
| RNF05 | Accesibilidad | Cumplimiento WCAG 2.1 AAA | ALTA |
| RNF06 | Usabilidad | Navegación optimizada para tablets | ALTA |
| RNF07 | DevOps | Docker Compose para despliegue | ALTA |
| RNF08 | Escalabilidad | Arquitectura de microservicios | MEDIA |
| RNF09 | Usabilidad | Soporte multilingüe (i18n) | MEDIA |
| RNF10 | Mantenibilidad | Documentación Swagger/OpenAPI | MEDIA |

---

## Planificación de Sprints

### Sprint 1: Infraestructura + Auth (5/11 - 18/11)
**Objetivo**: Sistema desplegable con login funcional

**Historias**:
- HU01: Registro de Tutor 
- HU02: Creación de Usuario final 

**Tareas adicionales**:
- Setup Docker Compose
- Configuración API Gateway
- Auth-service con JWT
- Notification-service para emails de confirmación
- Frontend base Angular
- Diseño WCAG desde día 1

---

### Sprint 2: Core Features (19/11 - 17/12)
**Objetivo**: Pictogramas funcionando

**Historias**:
- HU03: Subida de pictogramas 
- HU04: Creación de secciones privadas 

**Tareas adicionales**:
- Integración TTS
- Kafka events
- Subida de imágenes (BYTEA)

---

### Sprint 3: Admin + Polish (8/1 - 28/1)
**Objetivo**: Tablero y Sys.Admin funcionando

**Historias**:
- HU05: Composición de frases 
- HU06: Tablero público para invitados 
- HU07: Gestión de tickets 

**Tareas adicionales**:
- Board-service completo
- Admin-service
- Notification-service
- Bug fixes y optimizaciones

---

## Definition of Ready (DoR)

Una historia está lista para ser desarrollada cuando:
- Tiene criterios de aceptación claros
- Las dependencias técnicas están resueltas
- El equipo (desarrollador + profesor) la entiende
- Está estimada en Story Points

---

## Definition of Done (DoD)

Una historia se considera terminada cuando:
- Código implementado y funcionando
- Tests unitarios escritos y pasando
- Documentación API (Swagger) actualizada
- Revisión de código (self-review o profesor)
- Cumple criterios de aceptación
- Desplegada en entorno de desarrollo
- Sin bugs críticos conocidos

---

## Contacto y Soporte

- **Desarrollador**: Juan José Rivero Lorido 
- **Profesor supervisor**: Miguel Jaque 
- **Repositorio**: https://github.com/juanjooriveroo/comunikit

---

**Última actualización**: 12 de noviembre de 2025