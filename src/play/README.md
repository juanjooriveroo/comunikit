# ComuniKit Play 🎮

Aplicación de comunicación aumentativa y alternativa (CAA) diseñada para personas con discapacidad. Interfaz amigable e infantil construida con Astro.

## 🚀 Características

- **Diseño accesible e infantil**: Interfaz colorida y fácil de usar
- **Modo invitado**: Prueba la aplicación sin registro
- **Soporte multiidioma**: Español, Inglés, Francés, Alemán y Portugués
- **Síntesis de voz**: Lee en voz alta las frases construidas
- **Responsive**: Funciona en cualquier dispositivo

## 📦 Instalación

```bash
# Instalar dependencias
npm install

# Iniciar servidor de desarrollo
npm run dev

# Construir para producción
npm run build

# Previsualizar build
npm run preview
```

## 🔧 Configuración

Crea un archivo `.env` basado en `.env.example`:

```env
PUBLIC_API_URL=http://localhost:8080
```

## 🐳 Docker

```bash
# Construir imagen
docker build -t comunikit-play .

# Ejecutar contenedor
docker run -p 4321:80 comunikit-play
```

## 📁 Estructura

```
src/
├── components/     # Componentes reutilizables
├── layouts/        # Layouts de página
├── pages/          # Páginas de la aplicación
└── env.d.ts        # Tipos de TypeScript
public/
└── favicon.svg     # Favicon de la aplicación
```

## 🎨 Paleta de Colores

- **Primary**: #6C63FF (Púrpura)
- **Secondary**: #FF6B9D (Rosa)
- **Accent Yellow**: #FFD93D
- **Accent Green**: #6BCB77
- **Accent Blue**: #4D96FF

## ♿ Accesibilidad

- Navegación por teclado completa
- Etiquetas ARIA
- Soporte para lectores de pantalla
- Respeto a preferencias de movimiento reducido
- Alto contraste en elementos interactivos

## 📄 Licencia

MIT
