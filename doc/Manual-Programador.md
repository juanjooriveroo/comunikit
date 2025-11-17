# Manual del Programador - ComuniKIT

**Versión**: 1.0  
**Fecha**: 17 de noviembre de 2025  
**Audiencia**: Desarrolladores frontend y backend

---

## Tabla de Contenidos

1. [Arquitectura General](#arquitectura-general)
2. [Frontend - Angular](#frontend---angular)
3. [Backend - Microservicios](#backend---microservicios)
4. [Flujo de Autenticación](#flujo-de-autenticación)
5. [Cómo Contribuir](#cómo-contribuir)

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
        │ Auth Service │ │ Notification │ │ Otros Servicios  │
        │ (Java/Spring)│ │Service (Java)│ │  (Próximamente)  │
        └──────┬───────┘ └──────┬───────┘ └──────────────────┘
               │                │
               └────────┬───────┘
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
│       └── user.service.ts        # Gestiona usuarios
├── features/                       # Componentes con lógica específica
│   ├── auth/                      # Toda la autenticación
│   │   ├── login/
│   │   ├── register/
│   │   ├── recovery/
│   │   ├── reset-password/
│   │   ├── activate/
│   │   └── user-create/
│   └── home/                      # Página de inicio
├── shared/                         # Componentes y modelos reutilizables
│   ├── components/
│   │   └── navbar/               # Barra de navegación
│   └── models/
│       └── user.model.ts         # Interfaces de usuario
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
- `user.service.ts`: Gestiona operaciones de usuario (próximamente)

#### **`features/`** - Componentes "Inteligentes"

Contienen la lógica de cada funcionalidad.

**`auth/`** - Autenticación Completa:
- `login/`: Formulario de login con validación
- `register/`: Formulario de registro + modal de éxito
- `recovery/`: Solicitud de recuperación de contraseña
- `reset-password/`: Cambio de contraseña con token
- `activate/`: Activación de cuenta desde email
- `user-create/`: Creación de usuarios finales por tutores

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
    path: 'create-user',
    component: UserCreateComponent,
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

**Endpoints**:

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/auth/login` | Login con email/password |
| POST | `/auth/register` | Registro de tutor |
| POST | `/auth/recovery-account` | Solicitar recuperación |
| POST | `/auth/confirm-new-password` | Cambiar contraseña |
| POST | `/auth/activate/{id}` | Activar cuenta |
| GET | `/users/{id}` | Obtener usuario |
| PUT | `/users/{id}` | Actualizar usuario |

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

**Script inicial**: `deploy/db-init/auth-service.sql`

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
- **Logs**: Usar `Logger` de SLF4J

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

**Última actualización**: 17 de noviembre de 2025
