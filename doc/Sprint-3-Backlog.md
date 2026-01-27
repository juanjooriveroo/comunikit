# Sprint 3 Backlog - ComuniKIT
## Tablero + TTS + Acceso Público

**Fecha inicio**: 8 de enero de 2026  
**Fecha fin**: 28 de enero de 2026  
**Duración**: 3 semanas 

---

## Objetivo del Sprint

> **"Completar la funcionalidad del tablero de comunicación con gestión visual, composición de frases con TTS y acceso público para invitados"**

Al finalizar este Sprint, debería tener:
- Tablero completamente funcional con visualización y edición de orden de secciones
- Composición de frases mediante pictogramas con reproducción por voz (TTS)
- Tablero público accesible sin registro para probar la aplicación
- HU05, HU06 y HU07 completamente implementadas

---

## Historias de Usuario

### HU05 - Gestión de tablero
**Estado**: FINISHED
**Dependencias**: HU03, HU04

**Como** Usuario final  
**Quiero** editar mi tablero de manera que mi usuario dependiente pueda acceder a el  
**Para** poder navegar y comunicarme de forma intuitiva

#### Criterios de Aceptación
- [X] **AC1**: Visualización del tablero con secciones en formato grid
- [X] **AC2**: Navegación entre secciones (click para entrar, botón para volver)
- [X] **AC3**: Visualización de pictogramas dentro de cada sección
- [x] **AC4**: Reordenación de secciones mediante drag & drop o botones
- [x] **AC5**: Persistencia del orden personalizado en base de datos
- [x] **AC6**: Interfaz adaptada a tablets (touch-friendly)
- [x] **AC7**: Componentes Angular para visualización y edición del tablero

---

### HU06 - Composición de frases
**Estado**: FINISHED  
**Dependencias**: HU05

**Como** Usuario final  
**Quiero** seleccionar pictogramas y reproducir la frase por voz  
**Para** comunicar mis ideas

#### Criterios de Aceptación
- [X] **AC1**: Click en pictogramas los añade a la barra de composición
- [X] **AC2**: Barra de composición visible en la parte superior/inferior del tablero
- [X] **AC3**: Botón de reproducir genera audio con TTS (Web Speech API)
- [X] **AC4**: Botón de limpiar borra la composición actual
- [X] **AC5**: Botón de borrar último pictograma añadido
- [X] **AC6**: TTS generado en el cliente (sin servidor)
- [X] **AC7**: Soporte de idiomas (ES, EN, FR, DE, PT)
- [X] **AC8**: Funciona sin conexión una vez cargado el tablero

---

### HU07 - Tablero público para invitados
**Estado**: FINISHED 
**Dependencias**: HU05

**Como** Invitado  
**Quiero** acceder a un tablero público según idioma elegido  
**Para** probar la aplicación sin registrarme

#### Criterios de Aceptación
- [x] **AC1**: Acceso sin login desde la landing page
- [x] **AC2**: Selector de idioma visible en página de inicio
- [x] **AC3**: Tablero genérico con secciones predefinidas (comida, cuerpo, etc.)
- [x] **AC4**: Solo lectura (no editable)
- [x] **AC5**: TTS funcional igual que en tableros privados
- [x] **AC6**: No se guardan datos del invitado

---

## Tareas generales

### Board-Service
- [x] **G01**: Endpoint GET `/board/{userId}` para obtener tablero completo
- [x] **G02**: Endpoint GET `/board/public/{language}` para tablero público
- [x] **G03**: Crear tableros públicos predefinidos por idioma en BD
- [x] **G04**: Tests de nuevos endpoints

### Frontend - Tablero
- [x] **G05**: Componente `tablero-view` para visualización
- [x] **G06**: Componente `seccion-view` para ver pictogramas de sección
- [x] **G07**: Componente `barra-composicion` para frases
- [x] **G08**: Servicio Angular de TTS (`tts.service.ts`)
- [x] **G09**: Drag & drop para reordenar secciones

### Frontend - Público
- [x] **G10**: Nuevo Front con Astro para mayor velocidad de la aplicación
- [x] **G11**: Componente de tablero público (read-only)

---

## Diseño y Accesibilidad

### Guía de Estilo (continuación)
- [x] **DS1**: Diseño del tablero optimizado para tablets (mínimo 768px)
- [x] **DS2**: Pictogramas con tamaño mínimo de 80x80px para touch
- [x] **DS3**: Barra de composición con altura fija (100px)
- [x] **DS4**: Colores de la guía existente:
  - Primario: #3B82F6 (azul)
  - Secundario: #10B981 (verde)
  - Error: #EF4444 (rojo)
  - Fondo: #FFFFFF
  - Texto: #1F2937
  
- [x] **DS5**: Tipografía consistente:
  - Fuente: Inter (Google Fonts)
  - Tamaños: 14px (body), 16px (inputs), 24px (h1)
  
---

## Definition of Done - Sprint 3

Una historia se considera DONE cuando:
- Código implementado y funcionando en local
- Tests unitarios escritos y pasando (coverage > 70%)
- Documentación Swagger actualizada (si aplica)
- Cumple criterios de aceptación
- Cumple WCAG 2.1 AA
- Revisión de código (self-review o profesor)
- Docker Compose levanta sin errores
- Commit con mensaje descriptivo en Git
- Funciona en dispositivos móviles/tablets

---

## Registro del día

### Semana 1 (08/01 - 12/01)
**Día 1 (10/01)**: 
- Desarrollo del backend completo (board entity, controller, mapper, service, repository, dtos...)
- Edicion de tablero y obtencion por usuario (o público). Creación de tablero por Kafka al crearse el usuario dependiente en auth-service
- Creación de tablas de board-service.sql
- Creación de tablero al crear usuario dependiente importando la copia del tablero publico del idioma seleccionado

**Día 2 (14/01)**: 
- Eliminamos la restricción UNIQUE de la tabla board porque daba problemas a la hora de devolver la tabla
- Ajusto la col de food y body de los sql por errores
- Ajusto la configuración de kafka en board-service para recibir correctamente la serialización del mensaje de creación de tabla para el usuario pedido

**Día 3 (17/01)**: 
- Creación de servicio para peticiones al board
- Creación de componente de tablero (recibe tablero, muestra, permite reordenar con drag and drop y subir la nueva estructura)
- Modelo de board y rutas y modulos añadidos

**Día 4 (24/01)**: 
- Creación de front nuevo (ASTRO) para la visualización de tableros y funcionalidades.
- Implementado modo invitado en diferentes idiomas
- Creación de pictogramas default estáticos
- Creación de endpoint para pedir el tablero default del idioma seleccionado y se le gestiona permisos en el gateway

**Día 5 (27/01)**: 
- Creación de endpoint de login de usuarios dependientes publico
- Desarrollo completo de login en Astro, permitiendo acceder a tu cuenta y tableros por tu user y contraseña
- Creación de endpoint de obtención de tablero personal

---

## Métricas de Éxito

Al final del Sprint 3, debería poder demostrar:
- Un usuario puede ver su tablero con todas las secciones organizadas
- El usuario puede reordenar las secciones a su gusto
- El usuario puede navegar dentro de las secciones y ver los pictogramas
- El usuario puede seleccionar pictogramas y formar una frase
- El usuario puede reproducir la frase por voz (TTS)
- El TTS funciona en los 5 idiomas soportados (ES, EN, FR, DE, PT)
- Un invitado puede acceder al tablero público sin registrarse
- El invitado puede usar TTS en el tablero público
- Todo el flujo cumple WCAG 2.1 AA
- La interfaz es usable en tablets (touch-friendly)
- Docker Compose levanta toda la infraestructura
- Tests pasan (coverage > 70%)