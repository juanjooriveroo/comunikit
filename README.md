# ComuniKIT

## Plataforma Web de Comunicación Aumentativa y Alternativa (CAA)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Angular](https://img.shields.io/badge/Angular-15-red)](https://angular.io/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)](https://www.postgresql.org/)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.5-black)](https://kafka.apache.org/)
[![WCAG](https://img.shields.io/badge/WCAG-2.1%20AA-success)](https://www.w3.org/WAI/WCAG21/quickref/)

---

## Descripción

**ComuniKIT** es una solución web integral diseñada para facilitar la comunicación de personas con dificultades de expresión oral (parálisis cerebral, autismo, afasia, ELA, etc.) mediante tableros de pictogramas personalizables con síntesis de voz (TTS).

La plataforma implementa una **arquitectura de microservicios** con comunicación asíncrona vía **Apache Kafka**, garantizando escalabilidad, resiliencia y desacoplamiento entre componentes.
---

## Características Principales

-  **Tableros personalizados** con pictogramas organizados por secciones
-  **Síntesis de voz (TTS)** para reproducción de frases en tiempo real
-  **Gestión de usuarios** con roles jerárquicos (Usuario, Tutor, Educador, Admin)
-  **Accesibilidad WCAG 2.1 AA** con navegación inclusiva y adaptable
-  **Multilingüe (i18n)** con soporte para múltiples idiomas
-  **Sistema de tickets** para gestión de incidencias
-  **Almacenamiento de imágenes** en PostgreSQL (BYTEA)
-  **Cumplimiento RGPD** (simulado) con gestión de consentimientos
-  **Arquitectura de microservicios** con comunicación event-driven

---

## Arquitectura

```
┌──────────────────────────┐ ┌────────────────────────────┐
│   FRONTEND (Angular 15)  │ │        FRONT (Astro)       │
│  - WCAG 2.1 AA   - i18n  │ │     - TTS Client-side      │
└──────────────────────────┘ └────────────────────────────┘
            │                              │
            │ HTTPS/REST.                  │ HTTPS/REST
            ▼                              ▼
┌──────────────────────────────────────────────────────────┐
│              API GATEWAY (Spring Cloud Gateway)          │
│  - Routing  - Rate Limiting  - JWT Validation  - CORS    │
└──────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
        ▼                   ▼                   ▼
┌──────────────┐   ┌───────────────┐   ┌───────────────┐
│ AUTH-SERVICE │   │ BOARD-SERVICE │   │ NOTIFICATION  │
│              │   │               │   │    SERVICE    │
├──────────────┤   ├───────────────┤   ├───────────────┤
│ - Login/JWT  │   │ - Tableros    │   │ - Email Sender│
│ - Register   │   │ - Secciones   │   │               │
│ - Users CRUD │   │ - Pictogramas │   │               │
│ - Roles      │   │ - Imágenes    │   │               │
└──────────────┘   └───────────────┘   └───────────────┘
        │                   │                   │
        └───────────────────┼───────────────────┘
                            │
                ┌───────────▼───────────┐
                │   KAFKA CLUSTER       │
                ├───────────────────────┤
                │ Topics:               │
                │ • user.events         │
                │ • board.events        │
                │ • email.notifications │
                └───────────────────────┘
                            │
                            │
                ┌───────────▼───────────┐
                │   PostgreSQL 15       │
                │   (Single Database)   │
                └───────────────────────┘
```

---

## Stack Tecnológico

### **Frontend**
- Angular 15
- Astro
- TypeScript
- Bootstrap CSS
- Web Speech API (TTS)
- RxJS

### **Backend**
- Spring Boot 3.x
- Spring Security + JWT
- Spring Cloud Gateway
- Spring Data JPA
- Apache Kafka

### **Infraestructura**
- Docker y Docker Compose
- PostgreSQL 15
- Apache Kafka + Zookeeper
- VPS de alojamiento

### **DevOps**
- Git y GitHub
- Swagger/OpenAPI 3.0
- Logs estructurados (SLF4J)

---

## Estructura del Proyecto

```
comunikit/
├── README.md
├── .gitignore
├── doc/
│   ├── Project-Backlog.md
│   ├── Sprint-1-Backlog.md
│   ├── Sprint-2-Backlog.md
│   ├── Sprint-3-Backlog.md
│   ├── Manual-Usuario.md
│   ├── Manual-Programador.md
│   └── Manual-Instalacion.md
└── src/
    ├── front/                      # Angular 15 App
    │   ├── src/
    │   ├── angular.json
    │   └── package.json
    ├── play/                      # Astro
    │   ├── src/
    │   ├── public
    │   └── package.json
    ├── back/
    │   ├── api-gateway/            # Spring Cloud Gateway
    │   ├── auth-service/           # Microservicio de autenticación
    │   ├── board-service/          # Microservicio de tableros
    │   ├── admin-service/          # Microservicio de administración
    │   └── notification-service/   # Microservicio de notificaciones
    └── deploy/
        ├── docker-compose.yml
        ├── .env.example
        └── deb-init
            ├── auth-service.sql
            ├── board-service.sql
            └── admin-service.sql
```

---

## Ubicación de la app

La aplicación estará disponible en:
- **Frontend**: https://comunikit.es
- **API Gateway**: https://back.comunikit.es

---

## Documentación

- [ Project Backlog](doc/Project-Backlog.md)
- [ Sprint 1 - Infraestructura + Auth](doc/Sprint-1-Backlog.md)
- [ Sprint 2 - Pictogramas + Secciones](doc/Sprint-2-Backlog.md)
- [ Sprint 3 - Board + Admin](doc/Sprint-3-Backlog.md)
- [ Manual de Usuario](doc/Manual-Usuario.md)
- [ Manual de Programador](doc/Manual-Programador.md)
- [ Manual de Instalación](doc/Manual-Instalacion.md)

---

## Roles y Permisos

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| **USUARIO** | Persona con necesidades de comunicación | Solo lectura de su tablero |
| **TUTOR** | Padre/Tutor legal | CRUD tableros y pictogramas de sus usuarios |
| **INVITADO** | Acceso público anónimo | Solo lectura de tableros públicos |

---

## Seguridad

-  Autenticación JWT con refresh tokens
-  Cifrado de contraseñas con BCrypt
-  Control de acceso basado en roles (RBAC)
-  Validación estricta de archivos (MIME + tamaño)
-  Rate limiting en API Gateway
-  CORS configurado
-  HTTPS en producción

---

## Accesibilidad

-  **WCAG 2.1 AA** completo
-  Contraste de color AA
-  Focus indicators visibles
-  Textos alternativos en imágenes
-  Responsive para tablets y móviles

---

## Roadmap

### Sprint 1 (5/11 - 18/11)
- Infraestructura Docker
- Auth Service + API Gateway
- Frontend base con Angular
- Login/Register funcional

### Sprint 2 (19/11 - 17/12)
- Inicio Board Service
- CRUD tableros, secciones y pictogramas
- Subida de imágenes

### Sprint 3 (8/1 - 28/1)
- Board Service completo
- TTS integrado
- Admin Service
- Sistema de tickets
- Notification Service
- Testing y documentación

---

## Licencia

Este proyecto está bajo la licencia Creative Commons NonCommercial 4.0 International (CC NC 4.0). Ver [LICENSE](LICENSE) para más detalles.

---

## Autor

**Juan José Rivero Lorido**

- 🌐 LinkedIn: [juanjooriveroo](https://linkedin.com/in/juanjooriveroo)
- 📧 Email: jurrilo.25.22.github@gmail.com
- 🐙 GitHub: [@juanjooriveroo](https://github.com/juanjooriveroo)

---

**Desarrollado con ❤️ para mejorar la comunicación de personas con dificultades de expresión**