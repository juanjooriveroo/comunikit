# Funcionalidad de Cuentas Dependientes

## Resumen de Implementación

Se ha implementado la funcionalidad completa para gestionar cuentas dependientes en el frontend de ComuniKIT.

## Componentes Creados

### 1. Servicio de Cuentas Dependientes
**Archivo:** `src/app/core/services/dependent.service.ts`

Métodos implementados:
- `getAllDependents()` - Obtiene todas las cuentas dependientes del tutor
- `getDependentAccount(id)` - Obtiene detalles de una cuenta dependiente específica
- `editDependentProfile(data)` - Edita el perfil de una cuenta dependiente
- `changeDependentPassword(data)` - (Preparado pero no funcional aún) Cambia contraseña

### 2. Modelos de Datos
**Archivo:** `src/app/shared/models/dependent.model.ts`

Interfaces creadas:
- `Language` - Estructura del idioma
- `DependentAccount` - Cuenta dependiente completa
- `DependentAccountListItem` - Item de lista simplificado
- `GetAllDependentsResponse` - Respuesta del endpoint getAll
- `EditProfileRequest` - Request para editar perfil
- `ChangePasswordRequest` - Request para cambiar contraseña

### 3. Componente Home (Actualizado)
**Archivos:** 
- `src/app/features/home/home.component.ts`
- `src/app/features/home/home.component.html`
- `src/app/features/home/home.component.css`

**Funcionalidad:**
- Detecta si el usuario logueado es TUTOR
- Carga automáticamente las cuentas dependientes al iniciar
- Muestra cards con avatar, nombre completo y username
- Botón "Acceder" en cada card que navega al detalle

**Diseño:**
- Grid responsive de cards
- Avatar circular con inicial del nombre
- Gradiente verde corporativo
- Animaciones smooth en hover

### 4. Componente Detalle de Cuenta Dependiente
**Archivos:**
- `src/app/features/dependent-detail/dependent-detail.component.ts`
- `src/app/features/dependent-detail/dependent-detail.component.html`
- `src/app/features/dependent-detail/dependent-detail.component.css`

**Funcionalidad implementada:**

#### a) Vista de Información
- Header con avatar grande y datos del usuario
- Card con información completa:
  - ID de la cuenta
  - Nombre completo
  - Username
  - Idioma (nombre descriptivo)
  - Almacenamiento usado

#### b) Botones de Acción
- ✏️ **Editar Perfil** (FUNCIONAL)
  - Formulario con nombre, email e idioma
  - Validación de campos requeridos
  - Mensajes de éxito/error
  - Actualiza vista tras guardar

- 🔒 **Cambiar Contraseña** (PREPARADO - NO ENVÍA)
  - Formulario con contraseña anterior y nueva
  - Validación de longitud mínima (8 caracteres)
  - Muestra mensaje de "funcionalidad en desarrollo"
  - NO envía datos al backend

- 📊 **Tablero** (Placeholder)
  - Muestra alert de funcionalidad en desarrollo

- 🖼️ **Pictogramas y Secciones** (Placeholder)
  - Muestra alert de funcionalidad en desarrollo

#### c) Diseño
- Header con gradiente verde
- Cards con sombras y bordes redondeados
- Formularios con animación slideDown
- Botones con efectos hover
- Responsive para móviles

### 5. Rutas Configuradas
**Archivo:** `src/app/app-routing.module.ts`

Nueva ruta:
```typescript
{
  path: 'dependent-detail/:id',
  component: DependentDetailComponent,
  canActivate: [AuthGuard, RoleGuard],
  data: { roles: [UserRole.TUTOR] }
}
```

## Endpoints del Backend Utilizados

### GET `/auth/get-dependents-accounts`
**Headers:** `X-User-ID`
**Response:**
```json
{
  "accounts": [
    {
      "id": "uuid",
      "name": "string",
      "username": "string",
      "language": null,
      "storage_used": null
    }
  ]
}
```

### GET `/auth/get/{id}`
**Headers:** `X-User-ID`
**Params:** `id` (UUID de la cuenta dependiente)
**Response:**
```json
{
  "id": "uuid",
  "name": "string",
  "username": "string",
  "language": {
    "code": "es",
    "name": "Español"
  },
  "storage_used": 0.0
}
```

### PUT `/auth/edit-profile`
**Headers:** `X-User-ID`
**Body:**
```json
{
  "name": "string",
  "email": "string",
  "language": "es",
  "userId": "uuid" // ID de la cuenta a editar
}
```

### PUT `/auth/change-password` (NO IMPLEMENTADO AÚN)
**Headers:** `X-User-ID`
**Body:**
```json
{
  "oldPassword": "string",
  "newPassword": "string"
}
```

## Flujo de Usuario

1. Usuario con rol TUTOR inicia sesión
2. En la página principal, ve automáticamente las cards de sus cuentas dependientes
3. Click en "Acceder" de cualquier card
4. Navega a `/dependent-detail/{id}`
5. Ve toda la información de la cuenta
6. Puede:
   - Editar perfil (nombre, email, idioma)
   - Preparar cambio de contraseña (no funcional aún)
   - Ver botones de Tablero y Pictogramas (placeholders)
7. Click en "Volver" regresa a la página principal

## Pendiente de Implementación

1. **Cambio de Contraseña**: 
   - Actualmente el formulario está preparado pero NO envía datos
   - El usuario necesita modificar el backend antes de activar esta funcionalidad

2. **Tablero**: Funcionalidad futura

3. **Pictogramas y Secciones**: Funcionalidad futura

## Notas Técnicas

- El campo `email` no se devuelve en el endpoint GET `/auth/get/{id}` pero es requerido en PUT `/auth/edit-profile`
- Se agregó un mensaje informativo en el formulario de editar perfil sobre esto
- El idioma se maneja como objeto completo en el backend pero como código string en las requests
- Todas las validaciones de permisos se hacen en el backend (el tutor debe tener permiso sobre la cuenta)

## Estilos y UX

- Paleta de colores corporativa (verde #10B981)
- Diseño responsive mobile-first
- Animaciones suaves (fadeIn, slideDown)
- Feedback visual en todos los botones
- Estados de loading y error
- Mensajes de éxito/error en formularios
