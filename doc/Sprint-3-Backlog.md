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
**Estado**: ⏳ To Do  
**Dependencias**: HU03, HU04

**Como** Usuario final  
**Quiero** editar mi tablero de manera que mi usuario dependiente pueda acceder a el  
**Para** poder navegar y comunicarme de forma intuitiva

#### Criterios de Aceptación
- [ ] **AC1**: Visualización del tablero con secciones en formato grid
- [ ] **AC2**: Navegación entre secciones (click para entrar, botón para volver)
- [ ] **AC3**: Visualización de pictogramas dentro de cada sección
- [ ] **AC4**: Reordenación de secciones mediante drag & drop o botones
- [ ] **AC5**: Persistencia del orden personalizado en base de datos
- [ ] **AC6**: Interfaz adaptada a tablets (touch-friendly)
- [ ] **AC7**: Componentes Angular para visualización y edición del tablero

---

### HU06 - Composición de frases
**Estado**: ⏳ To Do  
**Dependencias**: HU05

**Como** Usuario final  
**Quiero** seleccionar pictogramas y reproducir la frase por voz  
**Para** comunicar mis ideas

#### Criterios de Aceptación
- [ ] **AC1**: Click en pictogramas los añade a la barra de composición
- [ ] **AC2**: Barra de composición visible en la parte superior/inferior del tablero
- [ ] **AC3**: Botón de reproducir genera audio con TTS (Web Speech API)
- [ ] **AC4**: Botón de limpiar borra la composición actual
- [ ] **AC5**: Botón de borrar último pictograma añadido
- [ ] **AC6**: TTS generado en el cliente (sin servidor)
- [ ] **AC7**: Soporte de idiomas (ES, EN, FR, DE, PT)
- [ ] **AC8**: Funciona sin conexión una vez cargado el tablero

---

### HU07 - Tablero público para invitados
**Estado**: ⏳ To Do  
**Dependencias**: HU05, HU06

**Como** Invitado  
**Quiero** acceder a un tablero público según idioma elegido  
**Para** probar la aplicación sin registrarme

#### Criterios de Aceptación
- [ ] **AC1**: Acceso sin login desde la landing page
- [ ] **AC2**: Selector de idioma visible en página de inicio
- [ ] **AC3**: Tablero genérico con secciones predefinidas (comida, cuerpo, etc.)
- [ ] **AC4**: Solo lectura (no editable)
- [ ] **AC5**: TTS funcional igual que en tableros privados
- [ ] **AC6**: No se guardan datos del invitado
- [ ] **AC7**: Botón claro para registrarse si le gusta la experiencia

---

## Tareas generales

### Board-Service
- [ ] **G01**: Endpoint GET `/board/{userId}` para obtener tablero completo
- [ ] **G02**: Endpoint GET `/board/public/{language}` para tablero público
- [ ] **G03**: Crear tableros públicos predefinidos por idioma en BD
- [ ] **G04**: Tests de nuevos endpoints

### Frontend - Tablero
- [ ] **G05**: Componente `tablero-view` para visualización
- [ ] **G06**: Componente `seccion-view` para ver pictogramas de sección
- [ ] **G07**: Componente `barra-composicion` para frases
- [ ] **G08**: Servicio Angular de TTS (`tts.service.ts`)
- [ ] **G09**: Drag & drop para reordenar secciones

### Frontend - Público
- [ ] **G10**: Nuevo Front con Astro para mayor velocidad de la aplicación
- [ ] **G11**: Componente de tablero público (read-only)

---

## Diseño y Accesibilidad

### Guía de Estilo (continuación)
- [ ] **DS1**: Diseño del tablero optimizado para tablets (mínimo 768px)
- [ ] **DS2**: Pictogramas con tamaño mínimo de 80x80px para touch
- [ ] **DS3**: Barra de composición con altura fija (100px)
- [ ] **DS4**: Colores de la guía existente:
  - Primario: #3B82F6 (azul)
  - Secundario: #10B981 (verde)
  - Error: #EF4444 (rojo)
  - Fondo: #FFFFFF
  - Texto: #1F2937
  
- [ ] **DS5**: Tipografía consistente:
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