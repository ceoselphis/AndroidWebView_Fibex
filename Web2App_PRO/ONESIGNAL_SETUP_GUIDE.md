# 📱 Guía Completa de Implementación de OneSignal

## ✅ Cambios Realizados en el Proyecto

### 1. **build.gradle (Proyecto raíz)**
- ✅ Agregado el plugin de OneSignal Gradle

### 2. **app/build.gradle**
- ✅ Agregado el plugin de OneSignal
- ✅ Agregada la dependencia del SDK de OneSignal

### 3. **AndroidManifest.xml**
- ✅ Agregados permisos para notificaciones push
- ✅ Registrada la clase MyApplication

### 4. **MyApplication.java**
- ✅ Creada clase Application para inicializar OneSignal

---

## 🔧 Configuración de OneSignal (Pasos Importantes)

### Paso 1: Crear una Cuenta en OneSignal

1. Ve a [https://onesignal.com/](https://onesignal.com/)
2. Crea una cuenta gratuita o inicia sesión
3. Haz clic en **"New App/Website"**
4. Ingresa el nombre de tu app: **"Fibex Telecom"**
5. Selecciona la plataforma: **Google Android (FCM)**

### Paso 2: Configurar Firebase Cloud Messaging (FCM)

OneSignal usa Firebase para enviar notificaciones en Android. Necesitas:

#### Opción A: Configuración Automática (Recomendada)
1. En el dashboard de OneSignal, selecciona **"Google Android (FCM)"**
2. OneSignal te pedirá que conectes tu proyecto con Firebase
3. Sigue el asistente de configuración automática

#### Opción B: Configuración Manual
1. Ve a [Firebase Console](https://console.firebase.google.com/)
2. Crea un nuevo proyecto o selecciona uno existente
3. Ve a **Configuración del proyecto** (ícono de engranaje)
4. En la pestaña **"Cloud Messaging"**, copia el **Server Key**
5. Pega este Server Key en OneSignal cuando te lo pida

### Paso 3: Obtener tu OneSignal App ID

1. Una vez configurado, ve al dashboard de OneSignal
2. Haz clic en **Settings** → **Keys & IDs**
3. Copia el **OneSignal App ID** (algo como: `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx`)

### Paso 4: Agregar el App ID a tu Proyecto

1. Abre el archivo: `app/src/main/java/com/oficina2/fibex_telecom/MyApplication.java`
2. Busca esta línea:
   ```java
   private static final String ONESIGNAL_APP_ID = "TU_ONESIGNAL_APP_ID_AQUI";
   ```
3. Reemplaza `"TU_ONESIGNAL_APP_ID_AQUI"` con tu App ID real:
   ```java
   private static final String ONESIGNAL_APP_ID = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx";
   ```

---

## 🚀 Compilar y Probar la App

### 1. Sincronizar Gradle
```bash
./gradlew clean
./gradlew build
```

### 2. Instalar en un Dispositivo Real
**IMPORTANTE:** Las notificaciones push NO funcionan en emuladores. Necesitas un dispositivo Android físico.

1. Conecta tu dispositivo Android por USB
2. Habilita la depuración USB en tu dispositivo
3. Ejecuta la app desde Android Studio o con:
   ```bash
   ./gradlew installDebug
   ```

### 3. Verificar la Instalación
1. Abre la app en tu dispositivo
2. Acepta los permisos de notificaciones cuando se te solicite
3. Ve al dashboard de OneSignal
4. En **Audience** → **All Users**, deberías ver al menos 1 usuario registrado

---

## 📨 Enviar una Notificación de Prueba

### Método 1: Desde el Dashboard de OneSignal (Más Fácil)

1. **Ir a Messages**
   - En el dashboard de OneSignal, haz clic en **"Messages"** en el menú lateral
   - Haz clic en **"New Push"**

2. **Configurar el Mensaje**
   - **Audience**: Selecciona **"Send to All Subscribed Users"** o **"Send to Test Users"**
   - **Title**: Escribe un título, por ejemplo: `"¡Hola desde Fibex!"`
   - **Message**: Escribe el mensaje, por ejemplo: `"Esta es una notificación de prueba"`
   - **Icon** (opcional): Puedes agregar un ícono o imagen

3. **Configurar Opciones Adicionales** (Opcional)
   - **Launch URL**: Si quieres que al tocar la notificación abra una URL específica
   - **Sound**: Selecciona el sonido de la notificación
   - **Action Buttons**: Puedes agregar botones de acción

4. **Enviar**
   - Haz clic en **"Review & Send"**
   - Revisa los detalles
   - Haz clic en **"Send Message"**

5. **Verificar**
   - En unos segundos deberías recibir la notificación en tu dispositivo
   - Si no la recibes, verifica que:
     - La app esté instalada y abierta al menos una vez
     - Hayas aceptado los permisos de notificaciones
     - Tu dispositivo tenga conexión a Internet

### Método 2: Usando la API REST de OneSignal

Puedes enviar notificaciones programáticamente usando cURL o Postman:

```bash
curl --request POST \
  --url https://onesignal.com/api/v1/notifications \
  --header 'Authorization: Basic TU_REST_API_KEY' \
  --header 'Content-Type: application/json' \
  --data '{
    "app_id": "TU_ONESIGNAL_APP_ID",
    "included_segments": ["All"],
    "headings": {"en": "¡Hola desde Fibex!"},
    "contents": {"en": "Esta es una notificación de prueba desde la API"}
  }'
```

**Nota:** Reemplaza `TU_REST_API_KEY` y `TU_ONESIGNAL_APP_ID` con tus valores reales.

### Método 3: Enviar a un Usuario Específico (por Player ID)

1. **Obtener el Player ID**
   - En el dashboard de OneSignal, ve a **Audience** → **All Users**
   - Haz clic en un usuario para ver su **Player ID**

2. **Enviar la Notificación**
   ```bash
   curl --request POST \
     --url https://onesignal.com/api/v1/notifications \
     --header 'Authorization: Basic TU_REST_API_KEY' \
     --header 'Content-Type: application/json' \
     --data '{
       "app_id": "TU_ONESIGNAL_APP_ID",
       "include_player_ids": ["PLAYER_ID_AQUI"],
       "headings": {"en": "Notificación Personal"},
       "contents": {"en": "Esta notificación es solo para ti"}
     }'
   ```

---

## 🧪 Probar Diferentes Tipos de Notificaciones

### 1. Notificación Simple
```json
{
  "app_id": "TU_APP_ID",
  "included_segments": ["All"],
  "headings": {"en": "Título Simple"},
  "contents": {"en": "Mensaje simple"}
}
```

### 2. Notificación con Imagen
```json
{
  "app_id": "TU_APP_ID",
  "included_segments": ["All"],
  "headings": {"en": "Oferta Especial"},
  "contents": {"en": "50% de descuento en planes de Internet"},
  "big_picture": "https://ejemplo.com/imagen-oferta.jpg"
}
```

### 3. Notificación con URL de Destino
```json
{
  "app_id": "TU_APP_ID",
  "included_segments": ["All"],
  "headings": {"en": "Nueva Factura"},
  "contents": {"en": "Tu factura de diciembre está disponible"},
  "url": "https://fibextelecom.com/facturas"
}
```

### 4. Notificación con Botones de Acción
```json
{
  "app_id": "TU_APP_ID",
  "included_segments": ["All"],
  "headings": {"en": "¿Te gustó nuestro servicio?"},
  "contents": {"en": "Déjanos saber tu opinión"},
  "buttons": [
    {"id": "si", "text": "Sí, me encanta"},
    {"id": "no", "text": "Necesita mejorar"}
  ]
}
```

---

## 🔍 Solución de Problemas

### La app no recibe notificaciones

1. **Verifica los permisos**
   - Ve a Configuración → Apps → Fibex Telecom → Notificaciones
   - Asegúrate de que las notificaciones estén habilitadas

2. **Verifica la conexión a Internet**
   - OneSignal requiere conexión a Internet para recibir notificaciones

3. **Verifica el App ID**
   - Asegúrate de haber reemplazado `TU_ONESIGNAL_APP_ID_AQUI` en `MyApplication.java`

4. **Revisa los logs**
   - Abre Android Studio
   - Ve a Logcat
   - Filtra por "OneSignal"
   - Busca errores o mensajes de inicialización

5. **Verifica Firebase**
   - Asegúrate de que Firebase esté correctamente configurado en OneSignal
   - Verifica que el Server Key sea correcto

### No aparecen usuarios en el dashboard

1. **Abre la app al menos una vez**
   - La app debe ejecutarse al menos una vez para registrarse

2. **Acepta los permisos**
   - Asegúrate de aceptar el permiso de notificaciones cuando se solicite

3. **Espera unos minutos**
   - Puede tomar unos minutos para que el usuario aparezca en el dashboard

---

## 📊 Características Avanzadas (Opcional)

### 1. Segmentar Usuarios
Puedes crear segmentos de usuarios basados en:
- Ubicación geográfica
- Idioma
- Versión de la app
- Tags personalizados

### 2. Programar Notificaciones
Puedes programar notificaciones para enviarlas en un momento específico.

### 3. A/B Testing
OneSignal permite hacer pruebas A/B para optimizar tus mensajes.

### 4. Analíticas
Revisa las estadísticas de:
- Tasa de entrega
- Tasa de apertura
- Tasa de clics

---

## 📝 Notas Importantes

1. **Dispositivos Reales**: Las notificaciones push solo funcionan en dispositivos reales, NO en emuladores.

2. **Google Play Services**: Asegúrate de que el dispositivo tenga Google Play Services instalado.

3. **Modo Debug**: El código actual tiene el modo verbose activado para debugging. Antes de publicar la app, cambia esto en `MyApplication.java`:
   ```java
   // Cambiar de VERBOSE a WARN o ERROR en producción
   OneSignal.getDebug().setLogLevel(LogLevel.WARN);
   ```

4. **Límites del Plan Gratuito**: OneSignal ofrece un plan gratuito generoso, pero tiene límites. Revisa los planes si necesitas más funcionalidades.

---

## ✅ Checklist Final

- [ ] Cuenta de OneSignal creada
- [ ] Firebase configurado
- [ ] OneSignal App ID agregado en `MyApplication.java`
- [ ] App compilada sin errores
- [ ] App instalada en dispositivo real
- [ ] Permisos de notificaciones aceptados
- [ ] Usuario aparece en el dashboard de OneSignal
- [ ] Notificación de prueba enviada y recibida

---

## 🆘 Soporte

Si tienes problemas:
1. Revisa la [documentación oficial de OneSignal](https://documentation.onesignal.com/docs/android-sdk-setup)
2. Revisa los logs en Android Studio (Logcat)
3. Verifica la configuración en el dashboard de OneSignal

¡Buena suerte con tu implementación! 🚀
