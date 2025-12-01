# 📨 Ejemplos de Notificaciones OneSignal para Fibex Telecom

Este archivo contiene ejemplos listos para usar de notificaciones push que puedes enviar usando la API de OneSignal.

## 🔑 Información Necesaria

Antes de usar estos ejemplos, necesitas:
- **OneSignal App ID**: Lo obtienes en Settings → Keys & IDs
- **REST API Key**: Lo obtienes en Settings → Keys & IDs

## 📋 Cómo Usar Estos Ejemplos

### Opción 1: Usando cURL (Terminal/CMD)

Copia el comando completo, reemplaza los valores necesarios y pégalo en tu terminal:

```bash
curl --request POST \
  --url https://onesignal.com/api/v1/notifications \
  --header 'Authorization: Basic TU_REST_API_KEY' \
  --header 'Content-Type: application/json' \
  --data 'PEGA_AQUI_EL_JSON'
```

### Opción 2: Usando Postman

1. Crea una nueva request POST
2. URL: `https://onesignal.com/api/v1/notifications`
3. Headers:
   - `Authorization: Basic TU_REST_API_KEY`
   - `Content-Type: application/json`
4. Body: Selecciona "raw" y "JSON", luego pega el JSON del ejemplo

---

## 📱 Ejemplos de Notificaciones

### 1. Notificación Simple a Todos los Usuarios

```json
{
  "app_id": "TU_ONESIGNAL_APP_ID",
  "included_segments": ["All"],
  "headings": {
    "en": "¡Hola desde Fibex Telecom!"
  },
  "contents": {
    "en": "Gracias por usar nuestra aplicación"
  }
}
```

**cURL completo:**
```bash
curl --request POST \
  --url https://onesignal.com/api/v1/notifications \
  --header 'Authorization: Basic TU_REST_API_KEY' \
  --header 'Content-Type: application/json' \
  --data '{
    "app_id": "TU_ONESIGNAL_APP_ID",
    "included_segments": ["All"],
    "headings": {"en": "¡Hola desde Fibex Telecom!"},
    "contents": {"en": "Gracias por usar nuestra aplicación"}
  }'
```

---

### 2. Notificación con Imagen Grande

```json
{
  "app_id": "TU_ONESIGNAL_APP_ID",
  "included_segments": ["All"],
  "headings": {
    "en": "🎉 Oferta Especial de Navidad"
  },
  "contents": {
    "en": "50% de descuento en todos nuestros planes de Internet Hogar"
  },
  "big_picture": "https://fibextelecom.com/images/oferta-navidad.jpg",
  "large_icon": "https://fibextelecom.com/images/logo.png"
}
```

---

### 3. Notificación que Abre una URL Específica

```json
{
  "app_id": "TU_ONESIGNAL_APP_ID",
  "included_segments": ["All"],
  "headings": {
    "en": "📄 Nueva Factura Disponible"
  },
  "contents": {
    "en": "Tu factura de diciembre ya está disponible. Toca para ver detalles."
  },
  "url": "https://fibextelecom.com/facturas",
  "data": {
    "url": "https://fibextelecom.com/facturas"
  }
}
```

---

### 4. Notificación con Botones de Acción

```json
{
  "app_id": "TU_ONESIGNAL_APP_ID",
  "included_segments": ["All"],
  "headings": {
    "en": "💬 ¿Cómo calificarías nuestro servicio?"
  },
  "contents": {
    "en": "Tu opinión es importante para nosotros"
  },
  "buttons": [
    {
      "id": "excelente",
      "text": "⭐ Excelente",
      "icon": "ic_menu_share"
    },
    {
      "id": "bueno",
      "text": "👍 Bueno",
      "icon": "ic_menu_send"
    },
    {
      "id": "mejorar",
      "text": "📝 Puede mejorar",
      "icon": "ic_menu_info_details"
    }
  ]
}
```

---

### 5. Notificación Programada (Envío Futuro)

```json
{
  "app_id": "TU_ONESIGNAL_APP_ID",
  "included_segments": ["All"],
  "headings": {
    "en": "⏰ Recordatorio de Pago"
  },
  "contents": {
    "en": "Tu factura vence en 3 días. No olvides realizar tu pago."
  },
  "send_after": "2025-12-05 10:00:00 GMT-0400"
}
```

---

### 6. Notificación a un Usuario Específico (por Player ID)

```json
{
  "app_id": "TU_ONESIGNAL_APP_ID",
  "include_player_ids": ["PLAYER_ID_DEL_USUARIO"],
  "headings": {
    "en": "👋 Hola, Juan"
  },
  "contents": {
    "en": "Tu plan Premium ha sido activado exitosamente"
  }
}
```

---

### 7. Notificación con Sonido Personalizado

```json
{
  "app_id": "TU_ONESIGNAL_APP_ID",
  "included_segments": ["All"],
  "headings": {
    "en": "🔔 Alerta Importante"
  },
  "contents": {
    "en": "Mantenimiento programado para esta noche de 2am a 4am"
  },
  "android_sound": "notification_sound",
  "priority": 10
}
```

---

### 8. Notificación con Datos Personalizados

```json
{
  "app_id": "TU_ONESIGNAL_APP_ID",
  "included_segments": ["All"],
  "headings": {
    "en": "🎁 Tienes un regalo"
  },
  "contents": {
    "en": "Hemos agregado 5GB extra a tu plan este mes"
  },
  "data": {
    "type": "bonus",
    "amount": "5GB",
    "expiry": "2025-12-31",
    "url": "https://fibextelecom.com/mi-cuenta"
  }
}
```

---

### 9. Notificación Silenciosa (Sin Sonido)

```json
{
  "app_id": "TU_ONESIGNAL_APP_ID",
  "included_segments": ["All"],
  "headings": {
    "en": "📊 Actualización de Datos"
  },
  "contents": {
    "en": "Tus datos han sido sincronizados correctamente"
  },
  "android_sound": "null",
  "priority": 5
}
```

---

### 10. Notificación con Emoji y Estilo

```json
{
  "app_id": "TU_ONESIGNAL_APP_ID",
  "included_segments": ["All"],
  "headings": {
    "en": "🚀 ¡Velocidad Mejorada!"
  },
  "contents": {
    "en": "Tu conexión ahora es 2x más rápida. Disfruta de la mejor experiencia en streaming y gaming 🎮📺"
  },
  "big_picture": "https://fibextelecom.com/images/velocidad.jpg",
  "android_accent_color": "FF0000FF",
  "android_led_color": "FF0000FF"
}
```

---

### 11. Notificación de Bienvenida (Para Nuevos Usuarios)

```json
{
  "app_id": "TU_ONESIGNAL_APP_ID",
  "filters": [
    {
      "field": "session_count",
      "relation": "=",
      "value": "1"
    }
  ],
  "headings": {
    "en": "👋 ¡Bienvenido a Fibex Telecom!"
  },
  "contents": {
    "en": "Gracias por instalar nuestra app. Aquí podrás consultar tus facturas, planes y mucho más."
  },
  "url": "https://fibextelecom.com/bienvenida"
}
```

---

### 12. Notificación con Múltiples Idiomas

```json
{
  "app_id": "TU_ONESIGNAL_APP_ID",
  "included_segments": ["All"],
  "headings": {
    "en": "Special Offer",
    "es": "Oferta Especial"
  },
  "contents": {
    "en": "Get 20% off on all plans this month",
    "es": "Obtén 20% de descuento en todos los planes este mes"
  }
}
```

---

### 13. Notificación de Pago Recibido

```json
{
  "app_id": "TU_ONESIGNAL_APP_ID",
  "include_player_ids": ["PLAYER_ID_DEL_USUARIO"],
  "headings": {
    "en": "✅ Pago Confirmado"
  },
  "contents": {
    "en": "Hemos recibido tu pago de $50.00. Gracias por tu preferencia."
  },
  "data": {
    "type": "payment_confirmed",
    "amount": "50.00",
    "invoice_id": "INV-2025-001",
    "url": "https://fibextelecom.com/recibos/INV-2025-001"
  }
}
```

---

### 14. Notificación de Mantenimiento

```json
{
  "app_id": "TU_ONESIGNAL_APP_ID",
  "included_segments": ["All"],
  "headings": {
    "en": "⚠️ Mantenimiento Programado"
  },
  "contents": {
    "en": "Realizaremos mantenimiento el 15 de diciembre de 2am a 4am. El servicio podría verse afectado."
  },
  "android_accent_color": "FFFF9800",
  "priority": 10
}
```

---

### 15. Notificación de Promoción con Cuenta Regresiva

```json
{
  "app_id": "TU_ONESIGNAL_APP_ID",
  "included_segments": ["All"],
  "headings": {
    "en": "⏰ ¡Última Oportunidad!"
  },
  "contents": {
    "en": "Solo quedan 24 horas para aprovechar nuestra oferta de Black Friday. ¡No te lo pierdas!"
  },
  "big_picture": "https://fibextelecom.com/images/black-friday.jpg",
  "url": "https://fibextelecom.com/ofertas",
  "priority": 10,
  "ttl": 86400
}
```

---

## 🎯 Segmentación Avanzada

### Enviar a Usuarios con Tag Específico

```json
{
  "app_id": "TU_ONESIGNAL_APP_ID",
  "filters": [
    {
      "field": "tag",
      "key": "plan_type",
      "relation": "=",
      "value": "premium"
    }
  ],
  "headings": {
    "en": "💎 Beneficio Exclusivo Premium"
  },
  "contents": {
    "en": "Como usuario Premium, tienes acceso anticipado a nuestras nuevas funciones"
  }
}
```

### Enviar a Usuarios Inactivos

```json
{
  "app_id": "TU_ONESIGNAL_APP_ID",
  "filters": [
    {
      "field": "last_session",
      "relation": ">",
      "hours_ago": "168"
    }
  ],
  "headings": {
    "en": "😢 Te extrañamos"
  },
  "contents": {
    "en": "Hace tiempo que no te vemos. ¡Tenemos novedades para ti!"
  }
}
```

---

## 🧪 Plantilla de Prueba Rápida

Usa esta plantilla para hacer pruebas rápidas:

```bash
curl --request POST \
  --url https://onesignal.com/api/v1/notifications \
  --header 'Authorization: Basic TU_REST_API_KEY' \
  --header 'Content-Type: application/json' \
  --data '{
    "app_id": "TU_ONESIGNAL_APP_ID",
    "included_segments": ["All"],
    "headings": {"en": "Prueba"},
    "contents": {"en": "Esta es una notificación de prueba"}
  }'
```

---

## 📊 Verificar Envío

Después de enviar una notificación, recibirás una respuesta como esta:

```json
{
  "id": "458dcec4-cf53-11e3-add2-000c2940e62c",
  "recipients": 3,
  "external_id": null
}
```

- **id**: ID de la notificación (úsalo para rastrear estadísticas)
- **recipients**: Número de dispositivos que recibirán la notificación

---

## 🔍 Consejos

1. **Prueba primero con un usuario**: Usa `include_player_ids` para enviar a tu dispositivo antes de enviar a todos
2. **Usa emojis**: Hacen las notificaciones más atractivas
3. **Sé breve**: Títulos de 30-40 caracteres y mensajes de 100-150 caracteres funcionan mejor
4. **Incluye acción**: Siempre da una razón para que el usuario abra la notificación
5. **Horarios óptimos**: Envía notificaciones entre 10am-8pm para mejor engagement

---

## 📝 Notas

- Reemplaza `TU_ONESIGNAL_APP_ID` con tu App ID real
- Reemplaza `TU_REST_API_KEY` con tu REST API Key real
- Reemplaza `PLAYER_ID_DEL_USUARIO` con el Player ID del usuario específico
- Las URLs deben ser válidas y accesibles
- Las imágenes deben ser HTTPS y de tamaño razonable (< 1MB)

¡Buena suerte con tus notificaciones! 🚀
