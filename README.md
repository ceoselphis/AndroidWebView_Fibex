# Fibex Oficina Móvil App (WebView)

Este proyecto es una aplicación Android que proporciona una experiencia móvil nativa para el portal de oficina de Fibex Telecom. Utiliza un WebView para cargar la aplicación web y la mejora con funcionalidades nativas.

## 📋 Descripción

La aplicación envuelve el portal web `https://oficina2.fibextelecom.net/` permitiendo a los usuarios acceder a sus servicios de Fibex Telecom directamente desde sus dispositivos móviles. Está diseñada para ofrecer una experiencia de usuario fluida, gestionando la conectividad y las notificaciones de manera eficiente.

## ✨ Características Principales

- **Integración WebView:** Carga y visualiza el portal de Fibex Telecom de manera optimizada.
- **Notificaciones Push:** Integración con **OneSignal** para recibir alertas y notificaciones importantes.
- **Manejo de Conectividad:** Detecta automáticamente el estado de la red y muestra una pantalla personalizada de "Sin Internet" cuando es necesario.
- **Carga de Archivos:** Soporte nativo para subir archivos desde la cámara o la galería del dispositivo a través del WebView.
- **Pantalla de Carga:** Utiliza animaciones **Lottie** para indicar el progreso de carga de manera visualmente atractiva.
- **Splash Screen:** Pantalla de bienvenida al iniciar la aplicación.
- **Navegación Intuitiva:** Gestión del botón "Atrás" para navegar dentro del historial del WebView y lógica de "presionar dos veces para salir".

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Java
- **Plataforma:** Android (SDK mínimo 21)
- **Librerías Clave:**
    - `OneSignal`: Para gestión de notificaciones push.
    - `Lottie`: Para animaciones vectoriales de alta calidad.
    - `SwipeRefreshLayout`: Para la funcionalidad de "deslizar para actualizar".
    - `AndroidX`: Componentes modernos de desarrollo Android.

## 🚀 Instalación y Configuración

1. **Clonar el repositorio:**
   ```bash
   git clone <url-del-repositorio>
   ```
2. **Abrir en Android Studio:**
   Abre el proyecto seleccionando la carpeta raíz.
3. **Sincronizar Gradle:**
   Espera a que Android Studio descargue las dependencias y configure el proyecto.
4. **Ejecutar:**
   Conecta un dispositivo físico o inicia un emulador y ejecuta la aplicación.

## 📱 Estructura del Proyecto

El código fuente principal se encuentra en `Web2App_PRO/app/src/main/java/com/oficina2/fibex_telecom/`.
- `MainActivity.java`: Actividad principal que maneja el WebView y la lógica de la app.
- `MyApplication.java`: Clase de aplicación para inicializaciones globales.
- `SplashScreen.java`: Pantalla de inicio.
- `OneSignalJavaScriptInterface.java`: Interfaz JavaScript para comunicación con OneSignal.

## 🔔 Integración OneSignal

Esta app incluye una integración completa con **OneSignal** para notificaciones push. La implementación permite:

- ✅ Suscripción automática a notificaciones
- ✅ Captura del OneSignal ID del dispositivo
- ✅ Asignación de External ID (ID del usuario desde el WebView)
- ✅ Comunicación bidireccional entre JavaScript y OneSignal
- ✅ Gestión de tags para segmentación de usuarios
- ✅ Logging detallado para debugging

### 🚀 Inicio Rápido

Desde tu aplicación web (oficina2.fibextelecom.net), puedes interactuar con OneSignal así:

```javascript
// Verificar si estamos en la app
if (typeof Android !== 'undefined') {
    // Obtener OneSignal ID
    const onesignalId = Android.getOneSignalId();
    
    // Asignar External ID cuando el usuario inicie sesión
    const userId = '12345'; // ID del usuario en tu sistema
    Android.setExternalUserId(userId);
    
    // Remover External ID cuando el usuario cierre sesión
    Android.removeExternalUserId();
}
```

Para más detalles, consulta la [documentación completa](./ONESIGNAL_INTEGRATION_GUIDE.md).