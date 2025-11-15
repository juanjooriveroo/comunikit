# 📧 TriviaRush Notification Service - Microservicio de Notificaciones

<p align="center">
  <img src="https://img.shields.io/badge/Spring_Boot-3.5.3-brightgreen" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Apache_Kafka-3.8-orange" alt="Kafka">
  <img src="https://img.shields.io/badge/Thymeleaf-3.1-green" alt="Thymeleaf">
  <img src="https://img.shields.io/badge/SMTP-Mail_Server-blue" alt="SMTP">
  <img src="https://img.shields.io/badge/Docker-Ready-2496ed" alt="Docker">
</p>

El **Servicio de Notificaciones de TriviaRush** es un microservicio independiente desarrollado con **Spring Boot 3.5.3** que gestiona el envío de emails automáticos del sistema. Procesa eventos de Kafka y envía notificaciones por correo electrónico utilizando templates HTML profesionales.

## 🏗️ **Arquitectura del Microservicio**

### **Patrón Event-Driven**
```
Kafka Consumer → Event Processing → Template Rendering → SMTP Delivery
     ↓               ↓                    ↓                 ↓
Eventos Kafka   Procesamiento      Templates HTML     Envío Email
```

### **Componentes Principales**

| **Capa** | **Componente** | **Responsabilidad** |
|----------|---------------|-------------------|
| **Consumer** | KafkaConsumer | Procesa eventos de registro y recuperación |
| **Service** | EmailService | Lógica de negocio para envío de emails |
| **Template** | ThymeleafEngine | Renderizado de templates HTML |
| **SMTP** | JavaMailSender | Configuración y envío de correos |
| **Config** | KafkaConfig, MailConfig | Configuración de servicios |

## 📁 **Estructura del Proyecto**

```
triviaRush_notification/
├── 📄 README.md                              # Este archivo
├── 🔧 pom.xml                               # Configuración Maven
├── 🐳 Dockerfile                            # Contenedor Docker
├── 📜 build-and-deploy.sh                   # Script de build
│
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/com/notificationservice/
│   │   │   ├── 🚀 NotificationServiceApplication.java
│   │   │   │
│   │   │   ├── 🔧 config/                   # Configuraciones
│   │   │   │   ├── KafkaConfig.java         # Config Kafka
│   │   │   │   └── MailConfig.java          # Config SMTP
│   │   │   │
│   │   │   ├── 📨 consumer/                 # Kafka Consumers
│   │   │   │   └── UserEventConsumer.java  # Procesador eventos
│   │   │   │
│   │   │   ├── 📧 service/                  # Servicios
│   │   │   │   └── EmailService.java       # Lógica email
│   │   │   │
│   │   │   └── 📋 dto/                      # DTOs
│   │   │       └── UserRegistrationEvent.java
│   │   │
│   │   ├── 📁 resources/
│   │   │   ├── ⚙️ application.properties     # Config principal
│   │   │   │
│   │   │   └── 📁 templates/                # Templates Thymeleaf
│   │   │       ├── welcome-email.html       # Email bienvenida
│   │   │       ├── activation-email.html    # Email activación
│   │   │       ├── password-reset.html      # Reset contraseña
│   │   │       └── layout/
│   │   │           └── email-layout.html    # Layout base
│   │   │
│   │   └── 📁 test/java/                    # Tests unitarios
│   │
│   └── 📁 target/                           # Artefactos compilados
│       └── notification-service-0.0.1-SNAPSHOT.jar
```

## 📚 **Recursos Adicionales**

- [Spring Boot Email](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.email)
- [Apache Kafka with Spring](https://spring.io/projects/spring-kafka)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Gmail SMTP Configuration](https://support.google.com/a/answer/176600?hl=en)

---

**Última actualización**: Julio 2025  
**Versión**: 1.0.0  
**Autor**: Juan José Rivero
