# Manual del Usuario - ComuniKIT

**Versión**: 2.0  
**Fecha**: 26 de enero de 2026  
**Audiencia**: Tutores y usuarios finales

---

## Tabla de Contenidos

1. [Bienvenida](#bienvenida)
2. [Crear tu Cuenta (Registro)](#crear-tu-cuenta-registro)
3. [Activar tu Cuenta](#activar-tu-cuenta)
4. [Iniciar Sesión](#iniciar-sesión)
5. [Recuperar Contraseña](#recuperar-contraseña)
6. [Gestionar tu Perfil](#gestionar-tu-perfil)
7. [Crear Usuarios Dependientes](#crear-usuarios-dependientes)
8. [Gestionar Usuarios Dependientes](#gestionar-usuarios-dependientes)
9. [Gestionar Pictogramas](#gestionar-pictogramas)
10. [Gestionar Secciones](#gestionar-secciones)
11. [Usar la Aplicación](#usar-la-aplicación)
12. [Preguntas Frecuentes](#preguntas-frecuentes)
13. [Soporte](#soporte)

---

## Bienvenida

**ComuniKIT** es una plataforma de comunicación diseñada para facilitar la gestión y colaboración entre educadores y alumnos.

### **¿Quiénes pueden usar ComuniKIT?**

- **Tutores**: Educadores que crean y gestionan grupos de comunicación
- **Alumnos**: Estudiantes que se unen a los grupos de sus tutores

### **Requisitos**

- Navegador web moderno (Chrome, Firefox, Safari, Edge)
- Conexión a internet
- Email válido y accesible

---

## Crear tu Cuenta (Registro)

### **Paso 1: Acceder a la página de registro**

1. Abre `https://comunikit.es/` en tu navegador
2. Haz clic en el botón **"Registrarse"** (esquina superior derecha)

### **Paso 2: Completar el formulario**

Verás un formulario con los siguientes campos:

| Campo | Descripción | Ejemplo |
|-------|-------------|---------|
| **Nombre Completo** | Tu nombre y apellido | Juan Pérez López |
| **Email** | Email activo donde recibirás mensajes | juan.perez@email.com |
| **Contraseña** | Mínimo 8 caracteres, incluir mayúsculas, números | Seg#Pas123 |
| **Confirmar Contraseña** | Repite tu contraseña | Seg#Pas123 |
| **Idioma** | Selecciona tu idioma preferido | Español / English |

### **Paso 3: Validaciones de Contraseña**

Tu contraseña debe cumplir:
- Mínimo 8 caracteres

**Ejemplo válido**: `Comunikit#2025`  

### **Paso 4: Confirmar registro**

1. Haz clic en **"Registrarse"**
2. Si todo está correcto, verás un modal con el mensaje:

> **¡Registro exitoso!**
> 
> Hemos enviado un email de activación a tu correo.
> Debes hacer clic en el enlace del email para activar tu cuenta.
> 
> **[Ir a Login]**

---

## Activar tu Cuenta

### **¿Por qué debo activar mi cuenta?**

Para garantizar que tu email es válido y que eres realmente tú quien se registró.

### **Paso 1: Revisar tu email**

1. Abre tu cliente de email (Gmail, Outlook, etc.)
2. Busca un email de **"noreply@comunikit.com"** con asunto:

> **Activa tu cuenta en ComuniKIT**

3. Si no ves el email en Bandeja de entrada, revisa la carpeta **"Correo no deseado"** o **"Spam"**

### **Paso 2: Hacer clic en el enlace de activación**

El email contiene un botón azul:

> **[Activar Cuenta]**

O también un enlace directo:
```
https://comunikit.es/auth/activate/abc123def456...
```

1. Haz clic en el botón o enlace
2. La página se abrirá automáticamente y tu cuenta será **activada**

### **Paso 3: Ir a Login**

Después de activar, verás el mensaje:

> **Cuenta activada exitosamente**
> 
> Tu cuenta está lista para usar.  
> **[Iniciar Sesión]**

---

## Iniciar Sesión

### **Paso 1: Acceder a la página de login**

1. Abre `https://comunikit.es/`
2. Haz clic en **"Iniciar Sesión"** (esquina superior derecha)
3. O accede directamente a `https://comunikit.es/login`

### **Paso 2: Completar credenciales**

Ingresa:
- **Email**: El email con el que te registraste
- **Contraseña**: Tu contraseña (diferencia mayúsculas/minúsculas)

### **Paso 3: Entrar**

1. Haz clic en **"Iniciar Sesión"**
2. Si las credenciales son correctas, entrarás a tu **panel de control**
3. Si no son correctas, verás un mensaje de error rojo

### **Después de iniciar sesión**

- Verás tu **nombre** en la barra de navegación (arriba a la derecha)
- Podrás acceder a todas las funciones de la aplicación
- Tu sesión permanecerá activa durante 24 horas

### **Login con nombre de usuario (username)**

- Además del login por email, la aplicación permite iniciar sesión con el *nombre de usuario* (username). Este flujo está pensado para cuentas de tipo `USUARIO` (dependientes).
- El endpoint usado por la aplicación para este caso es `/auth/login/user`. Tras un login exitoso el frontend guarda el token JWT en `localStorage` como `authToken`. Si la respuesta del servidor incluye sólo el token, la aplicación extrae el claim `sub` (UUID) y lo guarda en `localStorage` como `userUuid` para identificar el tablero privado.
- Cuando estés logueado, el menú cambiará sus textos/iconos y aparecerá la opción **Entrar a tu tablero** para cargar tu tablero privado.

---

## Recuperar Contraseña

**¿Olvidaste tu contraseña?** No hay problema, puedes recuperarla fácilmente.

### **Paso 1: Ir a Recuperar Contraseña**

1. En la página de **Login**, haz clic en el enlace:

> **¿Olvidaste tu contraseña?**

2. O accede directamente a: `https://comunikit.es/auth/recovery`

### **Paso 2: Solicitar recuperación**

1. Ingresa tu **email** registrado
2. Haz clic en **"Enviar Solicitud de Recuperación"**
3. Verás el mensaje:

> **Email de recuperación enviado**
> 
> Hemos enviado un enlace de recuperación a tu email.
> Revisa tu bandeja de entrada.

### **Paso 3: Revisar email de recuperación**

Busca un email de **"noreply@comunikit.com"** con asunto:

> **Recupera tu contraseña en ComuniKIT**

El email contiene un botón azul:

> **[Cambiar Contraseña]**

Haz clic en él.

### **Paso 4: Cambiar tu contraseña**

Se abrirá una página con el formulario:

| Campo | Descripción |
|-------|-------------|
| **Nueva Contraseña** | Tu nueva contraseña |
| **Confirmar Contraseña** | Repite la nueva contraseña |

**Recuerda**: La contraseña debe tener:
- Mínimo 8 caracteres
- Mayúscula, minúscula, número y símbolo

### **Paso 5: Cambio completado**

Verás el mensaje:

> **Contraseña cambiada exitosamente**
> 
> Ya puedes iniciar sesión con tu nueva contraseña.  
> **[Ir a Login]**

---

## Usar la Aplicación

### **Página de Inicio (Home)**

Después de iniciar sesión, ves tu panel personal con:

- **Bienvenida personalizada**: Hola [Tu Nombre]

### **Navegación**

**Barra de navegación superior**:
- Logo de ComuniKIT (te devuelve al inicio)
- Tu nombre con menú desplegable
- Botón de logout

### **Menú de Usuario**

Haz clic en tu **nombre** (arriba a la derecha) para ver:

- Mi Perfil
- Editar Perfil
- Cambiar Contraseña
- Mis Dependientes
- Cerrar Sesión

### **Reproducción de pictogramas y TTS**

- En la interfaz Play, al tocar un pictograma se reproduce su texto mediante síntesis de voz (TTS).
- Se han mejorado selectores de voz y el desbloqueo del `AudioContext` para móviles; aun así, en algunos dispositivos es necesario un toque previo a la reproducción para permitir audio automático.
- Recomendaciones si no se oye audio:
   - Comprueba que el dispositivo no esté en modo silencio y el volumen activado.
   - En Android usa preferentemente Chrome para mejor compatibilidad de voces.
   - Si no suena, pulsa una vez en la pantalla y vuelve a tocar el pictograma.

---

## Gestionar tu Perfil

### **Editar tu Perfil**

1. Haz clic en tu **nombre** (arriba a la derecha)
2. Selecciona **"Editar Perfil"**
3. Modifica los campos que desees:

| Campo | Descripción |
|-------|-------------|
| **Nombre** | Tu nombre completo |
| **Email** | Tu correo electrónico |
| **Idioma** | Tu idioma preferido |

4. Haz clic en **"Guardar Cambios"**
5. Verás un mensaje de confirmación

### **Cambiar tu Contraseña**

1. Haz clic en tu **nombre** (arriba a la derecha)
2. Selecciona **"Cambiar Contraseña"**
3. Completa el formulario:

| Campo | Descripción |
|-------|-------------|
| **Contraseña Actual** | Tu contraseña actual |
| **Nueva Contraseña** | Mínimo 8 caracteres |
| **Confirmar Contraseña** | Repite la nueva contraseña |

4. Haz clic en **"Cambiar Contraseña"**
5. La próxima vez que inicies sesión, usa tu nueva contraseña

### **Eliminar tu Cuenta**

⚠️ **Advertencia**: Esta acción es **irreversible** y eliminará:
- Tu cuenta de tutor
- Todas las cuentas dependientes asociadas
- Todos los pictogramas e imágenes
- Todas las secciones y tableros

1. Haz clic en tu **nombre** (arriba a la derecha)
2. Selecciona **"Eliminar Cuenta"**
3. Ingresa tu **contraseña** para confirmar
4. Haz clic en **"Eliminar mi Cuenta"**
5. Recibirás un **email de confirmación** de la baja

---

## Crear Usuarios Dependientes

Como **Tutor**, puedes crear cuentas para tus usuarios (alumnos, familiares, etc.) que utilizarán los tableros de comunicación.

### **¿Qué es un Usuario Dependiente?**

Un usuario dependiente es una persona que:
- Utiliza pictogramas para comunicarse
- No necesita email para acceder
- Es gestionado completamente por ti (el tutor)
- Tiene un límite de **50MB** de almacenamiento para imágenes

### **Crear un Usuario Dependiente**

1. Inicia sesión con tu cuenta de **Tutor**
2. En el menú, selecciona **"Crear Usuario"** o accede a `https://comunikit.es/create-user`
3. Completa el formulario:

| Campo | Descripción | Ejemplo |
|-------|-------------|----------|
| **Nombre** | Nombre del usuario | María García |
| **Apellido** | Apellido del usuario | López |
| **Contraseña** | Contraseña de acceso (mín. 8 caracteres) | MiClave123 |
| **Confirmar Contraseña** | Repite la contraseña | MiClave123 |
| **Idioma** | Idioma del tablero | Español |

4. Haz clic en **"Crear Usuario"**
5. El sistema generará un **nombre de usuario único** automáticamente
6. Guarda las credenciales en un lugar seguro:

> **Usuario creado exitosamente**
> 
> Nombre de usuario: `maria.garcia.123`
> Contraseña: (la que ingresaste)

---

## Gestionar Usuarios Dependientes

### **Ver tus Usuarios Dependientes**

1. Haz clic en tu **nombre** (arriba a la derecha)
2. Selecciona **"Mis Dependientes"**
3. Verás una lista con todos tus usuarios:

| Nombre | Usuario | Acciones |
|--------|---------|----------|
| María García | maria.garcia.123 | 👁️ Ver \| ✏️ Editar \| 🗑️ Eliminar |
| Pedro López | pedro.lopez.456 | 👁️ Ver \| ✏️ Editar \| 🗑️ Eliminar |

### **Ver Detalle de un Dependiente**

Haz clic en **👁️ Ver** para acceder al perfil completo:

- **Información personal**: Nombre, usuario, idioma
- **Almacenamiento usado**: X MB de 50 MB
- **Acceso al Gestor**: Botón para gestionar su tablero

### **Editar Datos de un Dependiente**

1. En la lista de dependientes, haz clic en **✏️ Editar**
2. Modifica los campos necesarios:
   - Nombre
   - Idioma
3. Haz clic en **"Guardar Cambios"**

### **Cambiar Contraseña de un Dependiente**

1. Accede al detalle del dependiente
2. Haz clic en **"Cambiar Contraseña"**
3. Ingresa:
   - Tu contraseña de tutor (para verificar)
   - Nueva contraseña para el dependiente
   - Confirmar nueva contraseña
4. Haz clic en **"Cambiar Contraseña"**

### **Eliminar un Usuario Dependiente**

⚠️ **Advertencia**: Esto eliminará permanentemente:
- La cuenta del dependiente
- Todos sus pictogramas e imágenes personalizados
- Todas sus secciones

1. En la lista de dependientes, haz clic en **🗑️ Eliminar**
2. Confirma ingresando **tu contraseña de tutor**
3. Haz clic en **"Eliminar"**

---

## Gestionar Pictogramas

Los pictogramas son las imágenes con texto que los usuarios utilizan para comunicarse.

### **Acceder al Gestor de Pictogramas**

1. Ve a **"Mis Dependientes"**
2. Selecciona el dependiente
3. Haz clic en **"Gestionar Tablero"** o el botón de acceso al gestor
4. En el gestor, selecciona la pestaña **"Pictogramas"**

### **Subir una Imagen**

Antes de crear un pictograma, necesitas subir la imagen:

1. En la pestaña **"Pictogramas"**, haz clic en **"Subir Imagen"**
2. Se abrirá un modal para seleccionar archivo
3. Selecciona una imagen de tu dispositivo

**Requisitos de la imagen**:
| Requisito | Valor |
|-----------|-------|
| **Formatos** | PNG, JPG, JPEG |
| **Tamaño máximo** | 5 MB |
| **Recomendación** | Imágenes cuadradas, fondo claro |

4. Haz clic en **"Subir"**
5. La imagen se guardará y estará disponible para crear pictogramas

### **Crear un Pictograma**

1. En la pestaña **"Pictogramas"**, haz clic en **"Crear Pictograma"**
2. Completa el formulario:

| Campo | Descripción | Ejemplo |
|-------|-------------|----------|
| **Nombre** | Texto que representa el pictograma | Casa |
| **Idioma** | Idioma del texto | Español |
| **Imagen** | Selecciona de tus imágenes subidas | 🏠 |

3. Haz clic en **"Crear"**
4. El pictograma aparecerá en tu lista

### **Editar un Pictograma**

1. En la lista de pictogramas, haz clic en **✏️ Editar**
2. Modifica:
   - Nombre/texto
   - Idioma
   - Imagen asociada
3. Haz clic en **"Guardar"**

### **Eliminar un Pictograma**

1. En la lista de pictogramas, haz clic en **🗑️ Eliminar**
2. Confirma la eliminación

> **Nota**: Eliminar un pictograma NO elimina la imagen. La imagen sigue disponible para otros pictogramas.

### **Eliminar una Imagen**

⚠️ **Advertencia**: Eliminar una imagen también eliminará todos los pictogramas que la usen.

1. En el selector de imágenes, localiza la imagen
2. Haz clic en **🗑️ Eliminar**
3. Confirma la eliminación

### **Control de Almacenamiento**

Cada cuenta dependiente tiene un límite de **50 MB** para imágenes.

- Puedes ver el **espacio usado** en el perfil del dependiente
- Si alcanzas el límite, deberás eliminar imágenes antes de subir nuevas
- Los pictogramas públicos (del sistema) no cuentan para tu cuota

---

## Gestionar Secciones

Las secciones organizan los pictogramas en categorías dentro del tablero.

### **¿Qué es una Sección?**

Una sección es un grupo de pictogramas relacionados. Por ejemplo:
- 🍎 **Comida**: manzana, pan, agua, leche...
- 👤 **Cuerpo**: mano, cabeza, pie, ojo...
- 🏠 **Lugares**: casa, escuela, parque...
- 😊 **Emociones**: feliz, triste, enfadado...

### **Acceder al Gestor de Secciones**

1. Ve a **"Mis Dependientes"**
2. Selecciona el dependiente
3. Haz clic en **"Gestionar Tablero"**
4. Selecciona la pestaña **"Secciones"**

### **Crear una Sección**

1. En la pestaña **"Secciones"**, haz clic en **"Crear Sección"**
2. Completa el formulario:

| Campo | Descripción | Ejemplo |
|-------|-------------|----------|
| **Nombre** | Nombre de la categoría | Comida |
| **Idioma** | Idioma de la sección | Español |
| **Imagen de portada** | Imagen representativa | 🍎 |

3. Haz clic en **"Crear"**
4. La sección se abrirá automáticamente para que añadas pictogramas

### **Añadir Pictogramas a una Sección**

1. Abre la sección que quieres editar (haz clic en **✏️ Editar**)
2. Verás una cuadrícula con **30 posiciones** disponibles
3. Haz clic en una posición vacía
4. Selecciona el pictograma que quieres añadir
5. El pictograma aparecerá en esa posición
6. Repite para añadir más pictogramas
7. Haz clic en **"Guardar"** cuando termines

**Posicionamiento entre Editor y Play**

- Los pictogramas y posiciones que se definen en el editor (formato interno 5x6) se muestran en Play sobre un grid distinto (12x7). Para evitar solapamientos y respetar las secciones definidas, los pictogramas importados desde el editor se colocan por defecto en la esquina inferior derecha del tablero Play.
- El sistema evita sobrescribir posiciones ya ocupadas por `sections` y aplica una asignación determinista para resolver colisiones cuando varias entradas compiten por la misma casilla.

### **Reorganizar Pictogramas en una Sección**

1. Abre la sección en modo edición
2. Arrastra los pictogramas a las posiciones deseadas
3. Haz clic en **"Guardar"**

### **Quitar un Pictograma de una Sección**

1. Abre la sección en modo edición
2. Haz clic en el pictograma que quieres quitar
3. Selecciona **"Quitar de sección"**
4. El pictograma se quitará de la sección (pero NO se elimina)

### **Editar una Sección**

1. En la lista de secciones, haz clic en **✏️ Editar**
2. Puedes modificar:
   - Nombre
   - Idioma
   - Imagen de portada
   - Pictogramas incluidos
3. Haz clic en **"Guardar"**

### **Eliminar una Sección**

1. En la lista de secciones, haz clic en **🗑️ Eliminar**
2. Confirma la eliminación

> **Nota**: Eliminar una sección NO elimina los pictogramas. Solo se elimina la organización.

---

## Preguntas Frecuentes

### **P: ¿Cuál es el idioma predeterminado?**

**R**: Puedes elegir entre 5 idiomas distintos durante el registro. Puedes cambiarlo luego en Configuración.

### **P: ¿Qué pasa si no activo mi cuenta?**

**R**: No podrás iniciar sesión. Debes activar tu cuenta mediante el enlace del email dentro de 24 horas. Si expira, solicita otro email de activación.

### **P: ¿Cuánto tiempo dura mi sesión?**

**R**: Tu sesión dura **24 horas**. Después deberás iniciar sesión nuevamente.

### **P: ¿Puedo cambiar mi email después de registrarme?**

**R**: Sí, ahora puedes cambiar tu email desde **Editar Perfil** en el menú de usuario.

### **P: ¿Cuántos usuarios dependientes puedo crear?**

**R**: No hay límite en la cantidad de usuarios dependientes que puedes crear.

### **P: ¿Cuánto espacio tiene cada usuario dependiente?**

**R**: Cada usuario dependiente tiene **50 MB** de almacenamiento para imágenes personalizadas.

### **P: ¿Qué formatos de imagen puedo subir?**

**R**: Puedes subir imágenes en formato **PNG, JPG o JPEG**. El tamaño máximo por imagen es **5 MB**.

### **P: ¿Los pictogramas públicos ocupan espacio de mi cuota?**

**R**: No. Los pictogramas públicos del sistema (comida, cuerpo, etc.) no cuentan para tu límite de almacenamiento.

### **P: ¿Cuántos pictogramas caben en una sección?**

**R**: Cada sección puede tener hasta **30 pictogramas** organizados en una cuadrícula.

### **P: ¿Puedo usar el mismo pictograma en varias secciones?**

**R**: Sí. Un pictograma puede estar en múltiples secciones a la vez.

### **P: ¿Qué pasa si elimino mi cuenta de tutor?**

**R**: Se eliminarán permanentemente tu cuenta, todas las cuentas dependientes asociadas y todo su contenido (pictogramas, imágenes, secciones).

### **P: ¿Es segura mi contraseña?**

**R**: Sí. Tu contraseña se **encripta** en el servidor. Los administradores nunca pueden verla.

### **P: ¿Qué hago si me olvidé mi email de registro?**

**R**: Contacta al soporte con tu nombre. Veremos si podemos recuperarlo.

---

## **Reportar Problemas**

Si encuentras un error:

1. Toma una **captura de pantalla**
2. Anota los **pasos** que repetiste para reproducir el error
3. Envía email a `jurrilo.25.22.github@gmail.com` con:
   - Tu email de registro
   - Descripción del problema
   - Captura de pantalla
   - Navegador y versión

---

## Solución rápida de problemas (novedades del sprint)

- Login por username falla: revisa la respuesta HTTP de `/auth/login/user`. Si el servidor devuelve un token, comprueba en https://jwt.io que el claim `sub` contiene el UUID; la app guarda ese UUID en `localStorage.userUuid`.
- Tablero privado no carga: confirma que `localStorage.authToken` y `localStorage.userUuid` existen y que la petición a `/board-full/{userUuid}` se envía con el header `Authorization: Bearer {token}`.
- No se oye TTS en móviles: prueba un toque previo en la página, usa Chrome en Android o Safari en iOS, y comprueba que el volumen/dispositivo no esté en silencio.

---

## Consejos de Seguridad

- Usa una **contraseña fuerte** (diferente en cada sitio)
- **No compartas tu contraseña** con nadie
- **Logout** al terminar, especialmente en computadoras compartidas
- Revisa tu **email registrado** regularmente
- **No hagas clic** en enlaces sospechosos o de emails desconocidos
- Cambia tu contraseña **cada 3 meses**

---

## Compatibilidad

ComuniKIT funciona en:
- Desktop (Windows, Mac, Linux)
- Tablet (iPad, Android tablets)
- Móvil (iPhone, Android phones)

Recomendamos una **pantalla de al menos 320px** de ancho.

---

## Resumen de Límites y Restricciones

| Concepto | Límite |
|----------|--------|
| Usuarios dependientes por tutor | Sin límite |
| Almacenamiento por dependiente | 50 MB |
| Tamaño máximo por imagen | 5 MB |
| Formatos de imagen | PNG, JPG, JPEG |
| Pictogramas por sección | 30 |
| Duración de sesión | 24 horas |
| Longitud mínima de contraseña | 8 caracteres |

---

**Última actualización**: 26 de enero de 2026

¿Necesitas ayuda? Contacta al soporte: **jurrilo.25.22.github@gmail.com**