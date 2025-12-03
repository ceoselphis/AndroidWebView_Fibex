package com.oficina2.fibex_telecom.helper;

import android.util.Log;
import android.webkit.JavascriptInterface;
import com.onesignal.OneSignal;
import org.json.JSONObject;

/**
 * Puente de comunicación entre Android y el WebView (Ionic Angular)
 * 
 * Esta clase permite que tu aplicación web Ionic Angular:
 * 1. Obtenga el OneSignal Player ID
 * 2. Asigne External IDs a los usuarios
 * 3. Agregue tags personalizados
 * 4. Verifique el estado de suscripción
 */
public class OneSignalBridge {
    
    private static final String TAG = "OneSignalBridge";
    
    /**
     * Obtiene el OneSignal Player ID (Subscription ID)
     * 
     * DESDE IONIC ANGULAR:
     * window.OneSignalBridge.getPlayerId()
     * 
     * @return El Player ID o null si no está disponible
     */
    @JavascriptInterface
    public String getPlayerId() {
        try {
            String playerId = OneSignal.getUser().getOnesignalId();
            Log.d(TAG, "📱 WebView solicitó Player ID: " + playerId);
            return playerId;
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al obtener Player ID: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Asigna un External ID al usuario de OneSignal
     * 
     * DESDE IONIC ANGULAR:
     * window.OneSignalBridge.setExternalUserId('12345')
     * 
     * @param externalId El ID del usuario en tu sistema (ej: ID de oficina2.fibextelecom)
     */
    @JavascriptInterface
    public void setExternalUserId(String externalId) {
        try {
            if (externalId == null || externalId.trim().isEmpty()) {
                Log.w(TAG, "⚠️ External ID vacío o nulo");
                return;
            }
            
            Log.d(TAG, "═══════════════════════════════════════════════");
            Log.d(TAG, "🔗 ASIGNANDO EXTERNAL ID");
            Log.d(TAG, "═══════════════════════════════════════════════");
            Log.d(TAG, "🆔 External ID: " + externalId);
            Log.d(TAG, "📱 Player ID actual: " + OneSignal.getUser().getOnesignalId());
            
            // Asignar el External ID
            OneSignal.login(externalId);
            
            Log.d(TAG, "✅ External ID asignado exitosamente");
            Log.d(TAG, "═══════════════════════════════════════════════");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al asignar External ID: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Elimina el External ID del usuario
     * 
     * DESDE IONIC ANGULAR:
     * window.OneSignalBridge.removeExternalUserId()
     */
    @JavascriptInterface
    public void removeExternalUserId() {
        try {
            Log.d(TAG, "🗑️ Eliminando External ID");
            OneSignal.logout();
            Log.d(TAG, "✅ External ID eliminado exitosamente");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al eliminar External ID: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene el External ID actual del usuario
     * 
     * DESDE IONIC ANGULAR:
     * window.OneSignalBridge.getExternalUserId()
     * 
     * @return El External ID o null si no está asignado
     */
    @JavascriptInterface
    public String getExternalUserId() {
        try {
            String externalId = OneSignal.getUser().getExternalId();
            Log.d(TAG, "📱 WebView solicitó External ID: " + externalId);
            return externalId;
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al obtener External ID: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Agrega un tag al usuario
     * 
     * DESDE IONIC ANGULAR:
     * window.OneSignalBridge.addTag('plan_type', 'premium')
     * 
     * @param key Clave del tag
     * @param value Valor del tag
     */
    @JavascriptInterface
    public void addTag(String key, String value) {
        try {
            if (key == null || key.trim().isEmpty()) {
                Log.w(TAG, "⚠️ Tag key vacío o nulo");
                return;
            }
            
            Log.d(TAG, "🏷️ Agregando tag: " + key + " = " + value);
            OneSignal.getUser().addTag(key, value);
            Log.d(TAG, "✅ Tag agregado exitosamente");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al agregar tag: " + e.getMessage());
        }
    }
    
    /**
     * Elimina un tag del usuario
     * 
     * DESDE IONIC ANGULAR:
     * window.OneSignalBridge.removeTag('plan_type')
     * 
     * @param key Clave del tag a eliminar
     */
    @JavascriptInterface
    public void removeTag(String key) {
        try {
            if (key == null || key.trim().isEmpty()) {
                Log.w(TAG, "⚠️ Tag key vacío o nulo");
                return;
            }
            
            Log.d(TAG, "🗑️ Eliminando tag: " + key);
            OneSignal.getUser().removeTag(key);
            Log.d(TAG, "✅ Tag eliminado exitosamente");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al eliminar tag: " + e.getMessage());
        }
    }
    
    /**
     * Verifica si el usuario está suscrito a notificaciones push
     * 
     * DESDE IONIC ANGULAR:
     * window.OneSignalBridge.isSubscribed()
     * 
     * @return "true" si está suscrito, "false" si no
     */
    @JavascriptInterface
    public String isSubscribed() {
        try {
            // Simplificado: asumimos que si hay Player ID, está suscrito
            String playerId = OneSignal.getUser().getOnesignalId();
            boolean isSubscribed = (playerId != null && !playerId.isEmpty());
            Log.d(TAG, "📱 WebView solicitó estado de suscripción: " + isSubscribed);
            return String.valueOf(isSubscribed);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al verificar suscripción: " + e.getMessage());
            return "false";
        }
    }
    
    /**
     * Obtiene el Push Token
     * 
     * DESDE IONIC ANGULAR:
     * window.OneSignalBridge.getPushToken()
     * 
     * @return El Push Token o null si no está disponible
     */
    @JavascriptInterface
    public String getPushToken() {
        try {
            // Simplificado: no disponible en esta versión de OneSignal
            Log.d(TAG, "📱 WebView solicitó Push Token: no disponible");
            return null;
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al obtener Push Token: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Obtiene información completa del usuario en formato JSON
     * 
     * DESDE IONIC ANGULAR:
     * const info = JSON.parse(window.OneSignalBridge.getUserInfo())
     * 
     * @return JSON con toda la información del usuario
     */
    @JavascriptInterface
    public String getUserInfo() {
        try {
            JSONObject info = new JSONObject();
            
            String onesignalId = OneSignal.getUser().getOnesignalId();
            String externalId = OneSignal.getUser().getExternalId();
            
            info.put("onesignalId", onesignalId);
            info.put("externalId", externalId);
            info.put("isSubscribed", (onesignalId != null && !onesignalId.isEmpty()));
            
            String jsonString = info.toString();
            Log.d(TAG, "📱 WebView solicitó información completa del usuario");
            Log.d(TAG, "📊 Info: " + jsonString);
            
            return jsonString;
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al obtener información del usuario: " + e.getMessage());
            return "{}";
        }
    }
    
    /**
     * Solicita permisos de notificaciones (si aún no se han solicitado)
     * 
     * DESDE IONIC ANGULAR:
     * window.OneSignalBridge.requestPermission()
     */
    @JavascriptInterface
    public void requestPermission() {
        try {
            Log.d(TAG, "🔔 WebView solicitó permisos de notificaciones");
            OneSignal.getNotifications().requestPermission(true, com.onesignal.Continue.with(result -> {
                Log.d(TAG, "✅ Respuesta de permisos recibida");
            }));
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al solicitar permisos: " + e.getMessage());
        }
    }
    
    /**
     * Método de prueba para verificar que el puente funciona
     * 
     * DESDE IONIC ANGULAR:
     * window.OneSignalBridge.test()
     * 
     * @return "OneSignal Bridge is working!"
     */
    @JavascriptInterface
    public String test() {
        String message = "✅ OneSignal Bridge is working!";
        Log.d(TAG, message);
        return message;
    }
}
