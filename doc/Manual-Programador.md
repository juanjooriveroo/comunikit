# Manual del Programador - ComuniKIT

**Versión**: 1.0  
**Fecha**: 15 de diciembre de 2025  
**Audiencia**: Desarrolladores frontend y backend

---

## Tabla de Contenidos

1. [Arquitectura General](#arquitectura-general)
2. [Frontend - Angular](#frontend---angular)
3. [Backend - Microservicios](#backend---microservicios)
4. [Flujo de Autenticación](#flujo-de-autenticación)
5. [Gestión de Cuentas Dependientes](#gestión-de-cuentas-dependientes)
6. [Gestión de Pictogramas e Imágenes](#gestión-de-pictogramas-e-imágenes)
7. [Gestión de Secciones](#gestión-de-secciones)
8. [Cómo Contribuir](#cómo-contribuir)

---

## Arquitectura General

ComuniKIT sigue una arquitectura de **microservicios con API Gateway** y **frontend Angular SPA**.

```
┌─────────────────────────────────────────────────────────────┐
│                      Frontend (Angular)                     │
│                (SPA - Single Page Application)              │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────┐
│                     API Gateway (Spring)                     │
│            (Enrutador, Validación de JWT, CORS)              │
└───────────────┬──────────────┬──────────────────┬────────────┘
                │              │                  │
                ▼              ▼                  ▼
        ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐
        │ Auth Service │ │ Notification │ │   Board Service  │
        │ (Java/Spring)│ │Service (Java)│ │      (Java)      │
        └──────┬───────┘ └──────┬───────┘ └────────┬─────────┘
               │                │                  │
               └────────┬───────┘──────────────────┘
                        ▼
                 ┌─────────────┐
                 │   Kafka     │
                 │ (Event Bus) │
                 └─────┬───────┘
                       ▼
                 ┌─────────────┐
                 │ PostgreSQL  │
                 │ (Database)  │
                 └─────────────┘
```

---

## Frontend - Angular

### **Estructura de Directorios**

```
src/app/
├── app-routing.module.ts          # Configuración de rutas
├── app.module.ts                  # Módulo raíz
├── app.component.ts               # Componente raíz
├── core/                          # Lógica compartida singleton
│   ├── guards/
│   │   ├── auth.guard.ts         # Protege rutas autenticadas
│   │   └── role.guard.ts         # Verifica roles de usuario
│   ├── interceptors/
│   │   └── jwt.interceptor.ts    # Inyecta token JWT en headers
│   └── services/
│       ├── auth.service.ts        # Gestiona autenticación
│       ├── user.service.ts        # Gestiona usuarios
│       ├── dependent.service.ts   # Gestiona cuentas dependientes
│       ├── pictogram.service.ts   # Gestiona pictogramas e imágenes
│       └── section.service.ts     # Gestiona secciones
├── features/                       # Componentes con lógica específica
│   ├── auth/                      # Toda la autenticación
│   │   ├── login/
│   │   ├── register/
│   │   ├── recovery/
│   │   ├── reset-password/
│   │   ├── activate/
│   │   ├── user-create/           # Creación de usuarios dependientes
│   │   ├── edit-profile/          # Edición de perfil propio
│   │   ├── change-password/       # Cambio de contraseña
│   │   ├── delete-account/        # Baja de cuenta
│   │   └── user/dependent/        # Gestión de perfil dependiente
│   ├── dependent-detail/          # Detalle de cuenta dependiente
│   ├── gestor/                    # Gestor de tableros
│   │   ├── pictogramas/           # CRUD de pictogramas
│   │   │   ├── crear-editar-pictograma/
│   │   │   ├── lista-pictogramas/
│   │   │   ├── modal-upload-imagen/
│   │   │   └── selector-imagenes/
│   │   └── secciones/             # CRUD de secciones
│   │       ├── crear-editar-seccion/
│   │       └── lista-secciones/
│   └── home/                      # Página de inicio
├── shared/                         # Componentes y modelos reutilizables
│   ├── components/
│   │   └── navbar/               # Barra de navegación
│   └── models/
│       ├── user.model.ts         # Interfaces de usuario
│       └── dependent.model.ts    # Interfaces de cuentas dependientes
└── environments/                   # Configuración por ambiente
    ├── environment.ts             # Producción
    └── environment.prod.ts        # Producción (alias)
```

### **Descripción de Directorios**

#### **`core/`** - Singleton (una instancia por aplicación)

**Guards:**
- `auth.guard.ts`: Protege rutas que requieren autenticación. Redirige a login si no hay token válido.
- `role.guard.ts`: Verifica que el usuario tenga los roles necesarios para acceder a una ruta.

**Interceptors:**
- `jwt.interceptor.ts`: Intercepta todas las peticiones HTTP y:
  - Inyecta el token JWT en el header `Authorization: Bearer {token}`
  - Maneja errores 401 (token expirado) redirigiendo a login
  - Propaga otros errores

**Services:**
- `auth.service.ts`: Gestiona:
  - Login / Logout
  - Registro
  - Recuperación de cuenta
  - Cambio de contraseña
  - Activación de cuenta
  - Estado del usuario actual (BehaviorSubject)
  - Validación de tokens JWT
- `user.service.ts`: Gestiona operaciones de usuario
- `dependent.service.ts`: Gestiona cuentas dependientes:
  - Obtener todas las cuentas dependientes
  - Obtener información de cuenta específica
  - Editar perfil de cuenta dependiente
  - Cambiar contraseña de cuenta dependiente
  - Eliminar cuenta dependiente
- `pictogram.service.ts`: Gestiona pictogramas e imágenes:
  - CRUD de pictogramas
  - Subida y eliminación de imágenes
  - Obtención de recursos por propietario
- `section.service.ts`: Gestiona secciones de tablero:
  - CRUD de secciones
  - Asociación de pictogramas a secciones

#### **`features/`** - Componentes "Inteligentes"

Contienen la lógica de cada funcionalidad.

**`auth/`** - Autenticación Completa:
- `login/`: Formulario de login con validación
- `register/`: Formulario de registro + modal de éxito
- `recovery/`: Solicitud de recuperación de contraseña
- `reset-password/`: Cambio de contraseña con token
- `activate/`: Activación de cuenta desde email
- `user-create/`: Creación de usuarios dependientes por tutores
- `edit-profile/`: Edición del perfil propio o de dependientes
- `change-password/`: Cambio de contraseña propia
- `delete-account/`: Baja de cuenta con confirmación por email
- `user/dependent/`: Gestión y visualización de perfil dependiente

**`dependent-detail/`** - Detalle de Cuenta Dependiente:
- Vista completa del perfil dependiente
- Acceso a edición y gestión del tablero

**`gestor/`** - Gestión de Tableros:
- `gestor.component.ts`: Componente principal con tabs (tablero, secciones, pictogramas)
- `pictogramas/`: Componentes para CRUD de pictogramas
  - `crear-editar-pictograma/`: Formulario de creación/edición
  - `lista-pictogramas/`: Listado con acciones
  - `modal-upload-imagen/`: Modal para subir imágenes
  - `selector-imagenes/`: Selector de imágenes existentes
- `secciones/`: Componentes para CRUD de secciones
  - `crear-editar-seccion/`: Formulario de creación/edición
  - `lista-secciones/`: Listado con acciones

**`home/`** - Página de Inicio:
- Landing page accesible sin autenticación
- Botones para registrarse o iniciar sesión
- Saludo personalizado si está autenticado

#### **`shared/`** - Reutilizable

**Components:**
- `navbar/`: Barra de navegación con:
  - Logo
  - Usuario actual (si está logueado)
  - Menú desplegable
  - Botón logout

**Models:**
- `user.model.ts`: Interfaces TypeScript:
  ```typescript
  interface User {
    id: string;
    email: string;
    name: string;
    role: UserRole;
  }
  
  interface LoginRequest { ... }
  interface RegisterRequest { ... }
  // etc.
  ```
- `dependent.model.ts`: Interfaces para cuentas dependientes:
  ```typescript
  interface DependentAccount {
    id: string;
    name: string;
    username: string;
    language: Language | null;
    storage_used: number | null;
  }
  
  interface EditProfileRequest {
    name: string;
    email?: string;
    language: string;
    userId?: string;
  }
  
  interface ChangePasswordRequest {
    oldPassword: string;
    newPassword: string;
    userId?: string;
  }
  
  interface DeleteAccountRequest {
    password: string;
    userId?: string;
  }
  ```

#### **`environments/`** - Configuración

Define el API URL según el ambiente:

```typescript
export const environment = {
  apiUrl: 'https://api.tudominio.com',
};
```

### **Flujo Típico de un Componente**

```typescript
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {}

  ngOnInit() {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/']);
    }
    
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) return;
    
    this.loading = true;
    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error.message;
      }
    });
  }
}
```

### **Frontend - Astro (Play)**

Nota: además del frontend Angular, el proyecto incluye una interfaz "Play" construida con Astro + TypeScript destinada a la vista de tablero/kiosk.

- Ubicación: `src/play/` (contiene `package.json`, `Dockerfile`, `astro.config.mjs`, `src/` y `public/`).
- Archivos clave:
    - `src/play/src/components/MainMenu.astro` — menú principal y modal de login/invitado.
    - `src/play/src/scripts/board/boardController.ts` — lógica de carga y mapeo del tablero, colocación de pictogramas y handlers de UI.
    - `src/play/src/scripts/board/speechService.ts` — utilidades TTS, selección de voz y desbloqueo de `AudioContext` para móviles.
    - `src/play/src/components/board/BoardGrid.astro` — plantilla y estilos del grid de Play.
- Build / desarrollo:
    - Instalar dependencias: `cd src/play && npm install`
    - Desarrollo (hot reload): `npm run dev` (dentro de `src/play`)
    - Compilar para producción: `npm run build`
    - El contenedor Docker se genera usando `src/play/Dockerfile`.
- Integración con backend:
    - Play consume los endpoints públicos: `/board-public-full/{languageCode}` (invitado) y `/board-full/{dependentId}` (privado).
    - El login por `username` usa `/auth/login/user`; el token se guarda en `localStorage.authToken` y el `sub` (UUID) en `localStorage.userUuid` si procede.
- Notas técnicas:
    - Grid mapping: el editor interno usa formato 5x6; Play usa 12x7 — el frontend mapea las coordenadas y evita sobrescribir `sections` existentes, posicionando pictogramas entrantes en la esquina inferior derecha cuando procede.
    - TTS: la implementación en `speechService.ts` incluye reintentos de selección de voz y una heurística para desbloquear `AudioContext` en dispositivos móviles antes de reproducir audio.


### **Rutas Disponibles**

```typescript
const routes: Routes = [
  { path: '', component: HomeComponent },                    
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'auth/recovery', component: RecoveryComponent },
  { path: 'auth/reset-password/:id', component: ResetPasswordComponent },
  { path: 'auth/activate/:id', component: ActivateComponent },
  { 
    path: 'auth/delete-account', 
    component: DeleteAccountComponent,
    canActivate: [AuthGuard] 
  },
  { 
    path: 'auth/edit-profile', 
    component: EditProfileComponent,
    canActivate: [AuthGuard] 
  },
  { 
    path: 'auth/change-password', 
    component: ChangePasswordComponent,
    canActivate: [AuthGuard] 
  },
  {
    path: 'create-user',
    component: UserCreateComponent,
    canActivate: [AuthGuard, RoleGuard],  
    data: { roles: [UserRole.TUTOR] }
  },
  {
    path: 'user/profile/:id',
    component: UserDependentProfileComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'dependent-detail/:id',
    component: DependentDetailComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [UserRole.TUTOR] }
  },
  {
    path: 'gestor/:id',
    component: GestorComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [UserRole.TUTOR] }
  },
  { path: '**', redirectTo: '' }  
];
```

---

## Backend - Microservicios

### **Documentación API (Swagger)**

Accede a: `https://back.comunikit.duckdns.org/documentacion`

### **Microservicio 1: API Gateway**

**Ubicación**: `src/back/api-gateway/`

**Responsabilidades**:
- Enrutamiento de peticiones a los microservicios
- Validación de tokens JWT
- Manejo de CORS
- Balanceo de carga (próximamente)

**Endpoints principales**:
```
POST   /api/auth/login                  → auth-service
POST   /api/auth/register               → auth-service
POST   /api/auth/recovery-account       → auth-service
POST   /api/auth/confirm-new-password   → auth-service
POST   /api/auth/activate/:id           → auth-service
```

**Tecnología**:
- Spring Cloud Gateway
- Spring Security
- JWT (JSON Web Tokens)

### **Microservicio 2: Auth Service**

**Ubicación**: `src/back/auth-service/`

**Responsabilidades**:
- Gestión de usuarios
- Autenticación (login/logout)
- Registro de tutores
- Generación de tokens JWT
- Validación de credenciales
- Recuperación de contraseña
- Activación de cuenta
- Cambio de contraseña
- Creación y gestión de cuentas dependientes
- Edición de perfiles (propio y dependientes)
- Baja de cuentas
- Validación de almacenamiento

**Endpoints**:

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/auth/login` | Login con email/password |
| POST | `/auth/register` | Registro de tutor |
| POST | `/auth/recovery-account` | Solicitar recuperación |
| POST | `/auth/confirm-new-password` | Cambiar contraseña con token |
| POST | `/auth/activate/{id}` | Activar cuenta |
| POST | `/auth/create-user` | Crear cuenta dependiente |
| PUT | `/auth/edit-profile` | Editar perfil (propio o dependiente) |
| PUT | `/auth/change-password` | Cambiar contraseña |
| DELETE | `/auth/delete-account` | Eliminar cuenta |
| GET | `/auth/get-dependents-accounts` | Obtener cuentas dependientes |
| GET | `/auth/get/{id}` | Obtener cuenta dependiente específica |
| GET | `/auth/validate-relation` | Validar relación tutor-dependiente |
| POST | `/auth/storage/validate` | Validar límite de almacenamiento |

**Tecnología**:
- Spring Boot
- Spring Data JPA
- Spring Security
- JWT (io.jsonwebtoken)
- BCrypt (contraseñas)
- Kafka
- PostgreSQL

**Estructura**:
```
src/main/java/authservice/
├── controller/         # Endpoints REST
├── service/            # Lógica de negocio
├── repository/         # Acceso a datos
├── entity/             # Modelos de BD
├── dto/                # Data Transfer Objects
├── config/             # Configuración
├── exception/          # Excepciones personalizadas
├── mapper/             # Mappers de entidades y Dtos
├── event/              # Eventos para Kafka
├── kafka/              # Publicador de tópicos de kafka
└── security/           # Configuración JWT
```

### **Microservicio 3: Notification Service**

**Ubicación**: `src/back/notification-service/`

**Responsabilidades**:
- Envío de emails de activación
- Envío de emails de recuperación
- Envío de notificaciones generales

**Tecnología**:
- Spring Boot
- Kafka (consumidor de eventos)
- JavaMailSender (SMTP)

**Flujo**:
1. Auth Service publica evento en Kafka (ej: `user.registered`)
2. Notification Service consume el evento
3. Notification Service envía email

**Variables de entorno necesarias**:
```
MAIL_SMTP_HOST=smtp.gmail.com
MAIL_SMTP_PORT=587
MAIL_USERNAME=tu-email@gmail.com
MAIL_PASSWORD=tu-app-password
MAIL_FROM=noreply@comunikit.com
```

### **Base de Datos**

**PostgreSQL** - Contiene:
- `users`     - Usuarios registrados
- `role`      - Roles posibles para usuarios
- `language`  - Idiomas del sistema
- `user_relation` - Relaciones tutor-dependiente

**Script inicial**: `deploy/db-init/auth-service.sql`

### **Microservicio 4: Board Service**

**Ubicación**: `src/back/board-service/`

**Responsabilidades**:
- Gestión de pictogramas
- Gestión de imágenes
- Gestión de secciones
- Control de almacenamiento por usuario
- Validación de relaciones tutor-dependiente

**Endpoints de Pictogramas**:

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/board/pictogram` | Crear pictograma |
| PUT | `/board/pictogram/{id}` | Actualizar pictograma |
| DELETE | `/board/pictogram/{id}` | Eliminar pictograma |
| GET | `/board/getAll-pictograms` | Obtener todos los pictogramas de un usuario |

**Endpoints de Imágenes**:

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/board/image` | Subir imagen (multipart/form-data) |
| DELETE | `/board/image/{id}` | Eliminar imagen |
| GET | `/board/getAll-images` | Obtener todas las imágenes de un usuario |

**Endpoints de Secciones**:

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/board/section` | Crear sección |
| PUT | `/board/section/{id}` | Actualizar sección |
| DELETE | `/board/section/{id}` | Eliminar sección |
| GET | `/board/getAll-section` | Obtener todas las secciones de un usuario |

**Tecnología**:
- Spring Boot
- Spring Data JPA
- Kafka (eventos de almacenamiento)
- PostgreSQL (BYTEA para imágenes)
- RestTemplate (comunicación con auth-service)

**Estructura**:
```
src/main/java/boardservice/
├── controller/         # Endpoints REST
│   ├── PictogramController.java
│   ├── ImageController.java
│   ├── SectionController.java
│   └── BoardController.java
├── service/            # Lógica de negocio
├── repository/         # Acceso a datos
├── entity/             # Modelos de BD
├── dto/                # Data Transfer Objects
├── config/             # Configuración (Swagger, etc.)
├── exception/          # Excepciones personalizadas
├── mapper/             # Mappers de entidades y DTOs
├── events/             # Eventos para Kafka
├── kafka/              # Consumidor/publicador de Kafka
├── client/             # Cliente REST para auth-service
└── utils/              # Utilidades (validación MIME, etc.)
```

**Base de Datos (board-service)**:

**PostgreSQL** - Contiene:
- `image`      - Imágenes almacenadas en BYTEA
- `pictogram`  - Pictogramas con referencia a imagen
- `section`    - Secciones de tablero
- `section_pictogram` - Relación sección-pictograma

**Scripts iniciales**: 
- `deploy/db-init/board-service.sql`
- `deploy/db-init/food.sql` (pictogramas públicos)
- `deploy/db-init/body.sql` (pictogramas públicos)

---

## Flujo de Autenticación

### **1. Registro (Sign Up)**

```
Usuario → Frontend (Register Component)
    ↓
        input: { name, email, password, language, rol: 'TUTOR' }
    ↓
POST /api/auth/register (API Gateway)
    ↓
Auth Service
    ├─ Validar email único
    ├─ Hashear contraseña (BCrypt)
    ├─ Guardar usuario en BD
    └─ Publicar evento "user.registered" en Kafka
    ↓
Notification Service
    ├─ Consume evento
    └─ Envía email de activación
    ↓
Frontend muestra modal: "Revisa tu email para activar tu cuenta"
```

### **2. Activación**

```
Usuario recibe email → Click en enlace: /auth/activate/{token}
    ↓
Frontend carga ActivateComponent
    ↓
POST /api/auth/activate/{token} (API Gateway)
    ↓
Auth Service
    ├─ Validar token
    ├─ Activar usuario
    └─ Responder 200 OK
    ↓
Frontend redirige a Login
    └─ Muestra mensaje: "Cuenta activada. Inicia sesión"
```

### **3. Login (Sign In)**

```
Usuario → Frontend (Login Component)
    ↓
        input: { email, password }
    ↓
POST /api/auth/login (API Gateway)
    ↓
Auth Service
    ├─ Validar credenciales
    ├─ Comparar contraseña (BCrypt)
    ├─ Generar JWT token
    └─ Responder { token: "eyJhbGc..." }
    ↓
Frontend
    ├─ Guarda token en localStorage
    ├─ Decodifica JWT (sin validar firma, solo lectura)
    ├─ Publica user$ BehaviorSubject
    └─ Redirige a Home
```

### **4. Peticiones Autenticadas**

```
Frontend component → authService.getUser() → HTTP GET /api/users/me
    ↓
JwtInterceptor intercepta
    ├─ Lee token de localStorage
    ├─ Añade header: Authorization: Bearer {token}
    └─ Pasa petición al Gateway
    ↓
API Gateway
    ├─ Valida JWT signature
    ├─ Extrae userId del token
    ├─ Añade header X-User-Id: {userId}
    └─ Redirige a auth-service
    ↓
Auth Service recibe petición autenticada
    └─ Responde con datos del usuario
    ↓
Frontend actualiza vista
```

### **5. Token Expirado**

```
Frontend intenta petición HTTP
    ↓
JwtInterceptor valida token
    ├─ Token expirado (exp < Date.now())
    └─ No añade Authorization header
    ↓
API Gateway rechaza con 401 Unauthorized
    ↓
JwtInterceptor captura error 401
    ├─ Limpia localStorage
    ├─ Redirige a /login?expired=true
    └─ Muestra mensaje: "Sesión expirada"
```

---

## Gestión de Cuentas Dependientes

### **Arquitectura**

Los tutores pueden crear y gestionar cuentas dependientes (usuarios finales). Esta relación se almacena en la tabla `user_relation`.

### **1. Creación de Cuenta Dependiente**

```
Tutor → Frontend (UserCreateComponent)
    ↓
        input: { name, language, password }
    ↓
POST /api/auth/create-user (API Gateway)
    ↓
Auth Service
    ├─ Validar que el usuario es TUTOR
    ├─ Crear usuario con rol USUARIO
    ├─ Hashear contraseña (BCrypt)
    ├─ Crear relación tutor-dependiente
    ├─ Asignar límite de 50MB de almacenamiento
    └─ Responder con credenciales { idUser, username }
    ↓
Frontend redirige al perfil del nuevo usuario
```

**Request DTO (Backend)**:
```java
public record CreateUserRequestDto(
    @NotBlank String name,
    @NotNull String language,
    @NotBlank @Size(min = 8) String password,
    UUID userId  // null para crear desde tutor actual
) {}
```

**Response DTO (Backend)**:
```java
public record CreateUserResponseDto(
    UUID idUser,
    String username
) {}
```

### **2. Obtener Cuentas Dependientes**

```
Tutor → Frontend (DependentService)
    ↓
GET /api/auth/get-dependents-accounts
    ↓
Auth Service
    ├─ Obtener userId del header X-User-ID
    ├─ Buscar todas las relaciones donde origin = userId
    └─ Responder con lista de cuentas
    ↓
Frontend muestra lista de dependientes
```

**Response DTO**:
```java
public record GetAllDependentsAccountsResponseDto(
    List<DependentAccountDto> accounts
) {}

public record DependentAccountDto(
    UUID id,
    String name,
    String username,
    Language language,
    Long storageUsed
) {}
```

### **3. Edición de Perfil (Propio o Dependiente)**

```
Usuario → Frontend (EditProfileComponent)
    ↓
        input: { name, email?, language, userId? }
    ↓
PUT /api/auth/edit-profile
    ↓
Auth Service
    ├─ Si userId está presente:
    │   ├─ Validar relación tutor-dependiente
    │   └─ Editar perfil del dependiente
    ├─ Si userId es null:
    │   └─ Editar perfil propio
    └─ Responder 200 OK
    ↓
Frontend muestra confirmación
```

**Request DTO**:
```java
public record EditProfileRequestDto(
    String name,
    @Email String email,
    String language,
    UUID userId  // null para editar propio, UUID para dependiente
) {}
```

### **4. Eliminación de Cuenta**

```
Usuario → Frontend (DeleteAccountComponent)
    ↓
        input: { password, userId? }
    ↓
DELETE /api/auth/delete-account
    ↓
Auth Service
    ├─ Validar contraseña del tutor
    ├─ Si userId presente:
    │   ├─ Validar relación tutor-dependiente
    │   └─ Eliminar cuenta dependiente
    ├─ Si userId es null:
    │   ├─ Eliminar todas las cuentas dependientes
    │   └─ Eliminar cuenta del tutor
    └─ Publicar evento "user.deleted" en Kafka
    ↓
Notification Service
    └─ Envía email de confirmación de baja
    ↓
Frontend redirige a landing page
```

---

## Gestión de Pictogramas e Imágenes

### **Arquitectura de Almacenamiento**

- Las imágenes se almacenan como **BYTEA** en PostgreSQL
- Máximo **5MB** por archivo
- Cuota de **50MB** por cuenta
- Formatos permitidos: PNG, JPG, JPEG
- Validación de tipo MIME en backend

### **1. Subida de Imagen**

```
Tutor → Frontend (PictogramService.uploadImage)
    ↓
        FormData: { data: { name, language, ownerId }, file: File }
    ↓
POST /api/board/image (multipart/form-data)
    ↓
Board Service
    ├─ Validar relación tutor-ownerId (RestTemplate a auth-service)
    ├─ Validar tipo MIME (PNG, JPG, JPEG)
    ├─ Validar tamaño (máx 5MB)
    ├─ Validar cuota de almacenamiento (RestTemplate a auth-service)
    ├─ Comprimir imagen si es necesario
    ├─ Guardar en BD como BYTEA
    ├─ Publicar evento de actualización de almacenamiento (Kafka)
    └─ Responder con ImageDto { id, name, mimeType, size }
    ↓
Auth Service (consume evento Kafka)
    └─ Actualizar storage_used del usuario
```

**Request DTO (multipart)**:
```java
public record ImagePushRequestDto(
    String name,
    String language,
    UUID ownerId
) {}
```

**Response DTO**:
```java
public record ImageDto(
    UUID id,
    String name,
    String mimeType,
    Long size,
    byte[] data  // Base64 encoded para frontend
) {}
```

**Frontend Service**:
```typescript
uploadImage(file: File, ownerId: string, language: string, name?: string): Observable<any> {
  const formData = new FormData();
  const data = { ownerId, language, name: name ?? file.name };
  
  formData.append('data', new Blob([JSON.stringify(data)], { type: 'application/json' }));
  formData.append('file', file);
  
  return this.http.post<any>(`${this.baseUrl}/image`, formData);
}
```

### **2. Creación de Pictograma**

```
Tutor → Frontend (PictogramService.createPictogram)
    ↓
        input: { imageId, ownerId, name, language }
    ↓
POST /api/board/pictogram
    ↓
Board Service
    ├─ Validar relación tutor-ownerId
    ├─ Validar que la imagen pertenece al ownerId
    ├─ Crear pictograma con referencia a imagen
    └─ Responder con PictogramDto
    ↓
Frontend actualiza lista de pictogramas
```

**Request DTO**:
```java
public record PictogramPushRequestDto(
    UUID imageId,
    UUID ownerId,
    String name,
    String language
) {}
```

**Response DTO**:
```java
public record PictogramDto(
    UUID id,
    String name,
    String language,
    UUID ownerId,
    ImageDto image
) {}
```

### **3. Actualización de Pictograma**

```
Tutor → Frontend (PictogramService.updatePictogram)
    ↓
        input: { name?, language?, imageId?, ownerId }
    ↓
PUT /api/board/pictogram/{id}
    ↓
Board Service
    ├─ Validar relación tutor-ownerId
    ├─ Validar propiedad del pictograma
    ├─ Actualizar campos proporcionados
    └─ Responder con PictogramDto actualizado
```

**Request DTO**:
```java
public record PictogramUpdateRequestDto(
    String name,
    String language,
    UUID imageId,
    UUID ownerId
) {}
```

### **4. Eliminación de Pictograma**

```
Tutor → Frontend (PictogramService.deletePictogram)
    ↓
DELETE /api/board/pictogram/{id}
    Body: { ownerId }
    ↓
Board Service
    ├─ Validar relación tutor-ownerId
    ├─ Validar propiedad del pictograma
    ├─ Eliminar pictograma (la imagen NO se elimina)
    └─ Responder 204 No Content
```

### **5. Eliminación de Imagen**

```
Tutor → Frontend (PictogramService.deleteImage)
    ↓
DELETE /api/board/image/{id}
    Body: { ownerId }
    ↓
Board Service
    ├─ Validar relación tutor-ownerId
    ├─ Validar propiedad de la imagen
    ├─ Eliminar imagen y pictogramas asociados
    ├─ Publicar evento de liberación de almacenamiento (Kafka)
    └─ Responder 204 No Content
    ↓
Auth Service (consume evento Kafka)
    └─ Actualizar storage_used del usuario
```

---

## Gestión de Secciones

### **Arquitectura**

Las secciones organizan pictogramas en categorías dentro del tablero de un usuario. Cada sección tiene:
- Nombre
- Imagen de portada (referencia a una imagen existente)
- Idioma
- Propietario (usuario dependiente)
- Lista de pictogramas asociados con posiciones

### **1. Creación de Sección**

```
Tutor → Frontend (SectionService.createSection)
    ↓
        input: { imageId, ownerId, name, language }
    ↓
POST /api/board/section
    ↓
Board Service
    ├─ Validar relación tutor-ownerId
    ├─ Validar propiedad de la imagen
    ├─ Crear sección
    └─ Responder con SectionDto
    ↓
Frontend abre sección en modo edición
```

**Request DTO**:
```java
public record SectionPushRequestDto(
    UUID imageId,
    UUID ownerId,
    String name,
    String language
) {}
```

**Response DTO**:
```java
public record SectionDto(
    UUID id,
    String name,
    String language,
    UUID ownerId,
    ImageDto image,
    List<PictogramPositionDto> pictograms  // Con posiciones
) {}

public record PictogramPositionDto(
    UUID pictogramId,
    Integer position,  // 0-29 (máximo 30 pictogramas por sección)
    PictogramDto pictogram
) {}
```

### **2. Actualización de Sección**

```
Tutor → Frontend (SectionService.updateSection)
    ↓
        input: { name?, language?, imageId?, pictograms?, ownerId }
    ↓
PUT /api/board/section/{id}
    ↓
Board Service
    ├─ Validar relación tutor-ownerId
    ├─ Validar propiedad de la sección
    ├─ Actualizar nombre/idioma/imagen si se proporcionan
    ├─ Si pictograms presente:
    │   ├─ Validar que todos los pictogramas pertenecen al ownerId
    │   ├─ Limpiar pictogramas anteriores
    │   └─ Asignar nuevos pictogramas con posiciones
    └─ Responder con SectionDto actualizado
```

**Request DTO**:
```java
public record SectionUpdateRequestDto(
    String name,
    String language,
    UUID imageId,
    List<PictogramPositionRequestDto> pictograms,
    UUID ownerId
) {}

public record PictogramPositionRequestDto(
    UUID pictogramId,
    Integer position  // Posición en el grid (0-29)
) {}
```

### **3. Eliminación de Sección**

```
Tutor → Frontend (SectionService.deleteSection)
    ↓
DELETE /api/board/section/{id}
    Body: { ownerId }
    ↓
Board Service
    ├─ Validar relación tutor-ownerId
    ├─ Validar propiedad de la sección
    ├─ Eliminar relaciones sección-pictograma
    ├─ Eliminar sección
    └─ Responder 204 No Content
```

### **Componentes Frontend del Gestor**

**GestorComponent** (`gestor.component.ts`):
```typescript
@Component({
  selector: 'app-gestor',
  templateUrl: './gestor.component.html'
})
export class GestorComponent implements OnInit {
  activeTab: 'tablero' | 'secciones' | 'pictogramas' = 'tablero';
  dependienteId: string = '';
  mostrarFormularioSeccion: boolean = false;
  mostrarFormularioPictograma: boolean = false;
  seccionEditando: any = null;
  pictogramaEditando: any = null;

  // Métodos para gestión de secciones
  crearSeccion(): void { ... }
  editarSeccion(seccion: any): void { ... }
  cerrarFormularioSeccion(): void { ... }

  // Métodos para gestión de pictogramas
  crearPictograma(): void { ... }
  editarPictograma(pictograma: any): void { ... }
  cerrarFormularioPictograma(): void { ... }
}
```

**Flujo típico de edición de sección**:
1. Tutor abre gestor del dependiente (`/gestor/:id`)
2. Navega a pestaña "Secciones"
3. Crea nueva sección o edita existente
4. Selecciona imagen de portada
5. Añade pictogramas arrastrando o seleccionando
6. Guarda cambios

---

### **Convenciones de Código**

#### **Angular**
- **Componentes**: `*-component.ts` con selector `app-*`
- **Servicios**: `*-service.ts` con `providedIn: 'root'`
- **Interfaces**: `*.model.ts` o `*.interface.ts`
- **Reactive Forms**: Usar `FormBuilder` en `ngOnInit()`
- **Observables**: Usar RxJS operators (`tap`, `catchError`, `map`)

#### **Spring Boot**
- **Controllers**: `@RestController` con `@RequestMapping`
- **Services**: `@Service` con lógica de negocio
- **Repositories**: Extender `JpaRepository<Entity, ID>`
- **DTOs**: Separar entities de DTOs
- **Exceptions**: Crear excepciones personalizadas

### **Flujo de Desarrollo**

1. **Crear rama**: `git checkout -b feature/nombre-feature`
2. **Desarrollar**: Hacer commits descriptivos
3. **Testear**: Ejecutar tests antes de push
4. **Push**: `git push origin feature/nombre-feature`
5. **Pull Request**: Crear PR con descripción clara

---

## Recursos Útiles

- **Angular Docs**: https://angular.io/docs
- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **JWT.io**: https://jwt.io (decodificar tokens)
- **Swagger API**: `https://back.comunikit-test.duckdns.org/documentacion`

---

**Última actualización**: 15 de diciembre de 2025
