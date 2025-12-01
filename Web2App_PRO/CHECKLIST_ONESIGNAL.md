# ✅ Checklist de Implementación OneSignal

Usa este checklist para asegurarte de completar todos los pasos necesarios.

---

## 📋 Fase 1: Configuración de OneSignal (Web)

### Crear Cuenta y App

- [ ] Ir a https://onesignal.com/
- [ ] Crear cuenta gratuita o iniciar sesión
- [ ] Hacer clic en "New App/Website"
- [ ] Ingresar nombre: "Fibex Telecom"
- [ ] Seleccionar plataforma: "Google Android (FCM)"

### Configurar Firebase

**Opción A: Configuración Automática (Recomendada)**
- [ ] Seguir el asistente de OneSignal
- [ ] Conectar con Firebase automáticamente

**Opción B: Configuración Manual**
- [ ] Ir a https://console.firebase.google.com/
- [ ] Crear proyecto o seleccionar existente
- [ ] Ir a Configuración del proyecto → Cloud Messaging
- [ ] Copiar el Server Key
- [ ] Pegar en OneSignal

### Obtener Credenciales

- [ ] En OneSignal, ir a Settings → Keys & IDs
- [ ] Copiar el **OneSignal App ID** (formato: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx)
- [ ] Copiar el **REST API Key** (para enviar notificaciones por API)
- [ ] Guardar estas credenciales en un lugar seguro

---

## 📋 Fase 2: Configuración del Proyecto Android

### Actualizar Código

- [ ] Abrir el archivo: `app/src/main/java/com/oficina2/fibex_telecom/MyApplication.java`
- [ ] Buscar la línea 12: `private static final String ONESIGNAL_APP_ID = "TU_ONESIGNAL_APP_ID_AQUI";`
- [ ] Reemplazar `"TU_ONESIGNAL_APP_ID_AQUI"` con tu App ID real
- [ ] Guardar el archivo

### Verificar Archivos Modificados

- [ ] Verificar `build.gradle` (raíz) - debe tener buildscript con OneSignal
- [ ] Verificar `app/build.gradle` - debe tener plugin y dependencia de OneSignal
- [ ] Verificar `AndroidManifest.xml` - debe tener permisos y android:name=".MyApplication"
- [ ] Verificar `MainActivity.java` - debe tener código para URLs desde notificaciones

---

## 📋 Fase 3: Compilación e Instalación

### Sincronizar Gradle

- [ ] Abrir Android Studio
- [ ] Hacer clic en "Sync Project with Gradle Files"
- [ ] Esperar a que termine la sincronización
- [ ] Verificar que no haya errores

### Compilar el Proyecto

- [ ] Abrir terminal en la carpeta del proyecto
- [ ] Ejecutar: `./gradlew clean`
- [ ] Ejecutar: `./gradlew build`
- [ ] Verificar que la compilación sea exitosa

### Preparar Dispositivo

- [ ] Conectar dispositivo Android por USB (NO usar emulador)
- [ ] Habilitar "Opciones de desarrollador" en el dispositivo
- [ ] Habilitar "Depuración USB"
- [ ] Verificar que el dispositivo aparezca en Android Studio

### Instalar la App

- [ ] Ejecutar: `./gradlew installDebug` o usar el botón Run en Android Studio
- [ ] Esperar a que la instalación termine
- [ ] Verificar que la app se haya instalado correctamente

---

## 📋 Fase 4: Pruebas Iniciales

### Primera Ejecución

- [ ] Abrir la app en el dispositivo
- [ ] Esperar a que aparezca el diálogo de permisos
- [ ] Aceptar el permiso de notificaciones
- [ ] Verificar que la app funcione normalmente

### Verificar Registro en OneSignal

- [ ] Ir al dashboard de OneSignal
- [ ] Hacer clic en "Audience" → "All Users"
- [ ] Verificar que aparezca al menos 1 usuario
- [ ] Hacer clic en el usuario para ver detalles
- [ ] Copiar el **Player ID** (útil para pruebas)

### Revisar Logs (Opcional pero Recomendado)

- [ ] Abrir Android Studio
- [ ] Ir a la pestaña "Logcat"
- [ ] Filtrar por "OneSignal"
- [ ] Verificar que no haya errores
- [ ] Buscar mensaje de inicialización exitosa

---

## 📋 Fase 5: Enviar Notificación de Prueba

### Método 1: Dashboard de OneSignal (Más Fácil)

- [ ] Ir a OneSignal Dashboard → Messages
- [ ] Hacer clic en "New Push"
- [ ] Seleccionar "Send to All Subscribed Users"
- [ ] Título: "¡Hola desde Fibex!"
- [ ] Mensaje: "Esta es una notificación de prueba"
- [ ] Hacer clic en "Review & Send"
- [ ] Hacer clic en "Send Message"
- [ ] Verificar que la notificación llegue al dispositivo

### Método 2: API REST con cURL

- [ ] Abrir terminal
- [ ] Copiar el comando de ejemplo de `NOTIFICATION_EXAMPLES.md`
- [ ] Reemplazar `TU_ONESIGNAL_APP_ID` con tu App ID
- [ ] Reemplazar `TU_REST_API_KEY` con tu REST API Key
- [ ] Ejecutar el comando
- [ ] Verificar que la notificación llegue al dispositivo

### Verificar Recepción

- [ ] La notificación aparece en la barra de notificaciones
- [ ] Al tocar la notificación, se abre la app
- [ ] La app carga correctamente
- [ ] No hay errores en Logcat

---

## 📋 Fase 6: Pruebas Avanzadas (Opcional)

### Probar Notificación con URL

- [ ] Enviar notificación con campo "url" o "data.url"
- [ ] Verificar que al tocar la notificación se abra la URL en el WebView
- [ ] Verificar que la URL se cargue correctamente

### Probar Notificación con Imagen

- [ ] Enviar notificación con campo "big_picture"
- [ ] Verificar que la imagen se muestre en la notificación
- [ ] Verificar que la imagen se cargue correctamente

### Probar Notificación con Botones

- [ ] Enviar notificación con campo "buttons"
- [ ] Verificar que los botones aparezcan en la notificación
- [ ] Tocar cada botón y verificar el comportamiento

### Probar Segmentación con Tags

- [ ] Verificar que los tags se hayan agregado en OneSignal
- [ ] Ir a Audience → All Users → seleccionar usuario → ver Tags
- [ ] Enviar notificación filtrada por tag
- [ ] Verificar que solo los usuarios con ese tag la reciban

---

## 📋 Fase 7: Preparación para Producción

### Optimizar Configuración

- [ ] Abrir `MyApplication.java`
- [ ] Cambiar `LogLevel.VERBOSE` a `LogLevel.WARN` o `LogLevel.ERROR`
- [ ] Guardar el archivo

### Actualizar Versión

- [ ] Abrir `app/build.gradle`
- [ ] Incrementar `versionCode` (ejemplo: de 2 a 3)
- [ ] Actualizar `versionName` si es necesario (ejemplo: de "2.0" a "2.1")
- [ ] Guardar el archivo

### Compilar Release

- [ ] Ejecutar: `./gradlew assembleRelease`
- [ ] Firmar el APK con tu keystore
- [ ] Probar el APK release en un dispositivo
- [ ] Verificar que las notificaciones funcionen en release

### Documentación

- [ ] Documentar el OneSignal App ID en un lugar seguro
- [ ] Documentar el REST API Key
- [ ] Documentar el proceso de envío de notificaciones
- [ ] Compartir `NOTIFICATION_EXAMPLES.md` con tu equipo

---

## 📋 Checklist de Verificación Final

### Funcionalidad Básica

- [ ] ✅ La app se instala correctamente
- [ ] ✅ La app solicita permisos de notificaciones
- [ ] ✅ El usuario aparece en el dashboard de OneSignal
- [ ] ✅ Las notificaciones llegan al dispositivo
- [ ] ✅ Al tocar una notificación, se abre la app

### Funcionalidad Avanzada

- [ ] ✅ Las notificaciones con URL abren la URL correcta
- [ ] ✅ Las notificaciones con imagen muestran la imagen
- [ ] ✅ Los tags se registran correctamente
- [ ] ✅ La segmentación funciona
- [ ] ✅ Los logs no muestran errores

### Preparación para Producción

- [ ] ✅ LogLevel cambiado a WARN o ERROR
- [ ] ✅ Versión de la app actualizada
- [ ] ✅ APK release compilado y probado
- [ ] ✅ Documentación completada
- [ ] ✅ Equipo capacitado en el uso de OneSignal

---

## 🎉 ¡Felicidades!

Si completaste todos los checkboxes, ¡tu implementación de OneSignal está lista!

### Próximos Pasos Recomendados

1. **Crear Segmentos**: Define segmentos de usuarios en OneSignal para enviar notificaciones más relevantes
2. **Programar Notificaciones**: Programa notificaciones para fechas/horas específicas
3. **Analizar Métricas**: Revisa las estadísticas de tus notificaciones (tasa de apertura, clics, etc.)
4. **Optimizar Mensajes**: Usa A/B testing para mejorar tus mensajes
5. **Automatizar**: Integra OneSignal con tu backend para enviar notificaciones automáticas

### Recursos Útiles

- 📖 **Guía de Configuración**: `ONESIGNAL_SETUP_GUIDE.md`
- 📨 **Ejemplos de Notificaciones**: `NOTIFICATION_EXAMPLES.md`
- 📋 **Resumen**: `README_ONESIGNAL.md`
- 🌐 **Documentación Oficial**: https://documentation.onesignal.com/

---

## 🆘 ¿Problemas?

Si algo no funciona, revisa:

1. **Guía de Solución de Problemas**: En `ONESIGNAL_SETUP_GUIDE.md`
2. **Logs de Android**: Logcat en Android Studio, filtrar por "OneSignal"
3. **Dashboard de OneSignal**: Revisa estadísticas y errores
4. **Documentación Oficial**: https://documentation.onesignal.com/docs/troubleshooting-android

---

**Última actualización**: Diciembre 2025  
**Versión de OneSignal SDK**: 5.x  
**Versión mínima de Android**: API 24 (Android 7.0)
