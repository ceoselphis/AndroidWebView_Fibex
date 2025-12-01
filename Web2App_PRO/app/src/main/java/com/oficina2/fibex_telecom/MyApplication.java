package com.oficina2.fibex_telecom;

import android.app.Application;
import android.util.Log;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
import org.json.JSONObject;

public class MyApplication extends Application {
    
    // IMPORTANTE: Reemplaza este valor con tu OneSignal App ID
    // Lo puedes obtener en: https://app.onesignal.com/ -> Settings -> Keys & IDs
    private static final String ONESIGNAL_APP_ID = "d4c9d29c-6b43-4826-abf8-fc69aaf91319";
    
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        // Verbose Logging set to help debug issues, remove before releasing your app.
        // En producción, cambia a LogLevel.WARN o LogLevel.ERROR
        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);

        // OneSignal Initialization
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);

        // NO solicitamos permisos aquí - se solicitarán en MainActivity
        // para evitar que el WebView se sobreponga al diálogo de permisos


        // Listener para cuando se recibe una notificación (la app está abierta)
        OneSignal.getNotifications().addForegroundLifecycleListener(event -> {
            Log.d(TAG, "Notificación recibida en primer plano: " + event.getNotification().getTitle());
            // Aquí puedes personalizar el comportamiento cuando se recibe una notificación
            // mientras la app está abierta
        });

        // Listener para cuando el usuario hace clic en una notificación
        OneSignal.getNotifications().addClickListener(event -> {
            Log.d(TAG, "Usuario hizo clic en la notificación");
            
            // Obtener datos de la notificación
            String title = event.getNotification().getTitle();
            String body = event.getNotification().getBody();
            
            Log.d(TAG, "Título: " + title);
            Log.d(TAG, "Mensaje: " + body);
            
            // Si la notificación tiene datos adicionales
            JSONObject additionalData = event.getNotification().getAdditionalData();
            if (additionalData != null) {
                Log.d(TAG, "Datos adicionales: " + additionalData.toString());
                
                // Ejemplo: Si enviaste una URL en los datos adicionales
                // puedes abrirla aquí
                if (additionalData.has("url")) {
                    try {
                        String url = additionalData.getString("url");
                        Log.d(TAG, "URL recibida: " + url);
                        // Aquí puedes abrir la URL en tu WebView
                    } catch (Exception e) {
                        Log.e(TAG, "Error al obtener URL: " + e.getMessage());
                    }
                }
            }
        });

        // Obtener el Player ID (ID único del dispositivo en OneSignal)
        // Útil para enviar notificaciones a dispositivos específicos
        String playerId = OneSignal.getUser().getOnesignalId();
        if (playerId != null) {
            Log.d(TAG, "OneSignal Player ID: " + playerId);
            // Puedes guardar este ID en tu servidor para enviar notificaciones personalizadas
        }

        // Agregar tags al usuario (útil para segmentación)
        // Ejemplo: Puedes etiquetar usuarios por tipo de plan, ciudad, etc.
        OneSignal.getUser().addTag("app_version", "2.0");
        OneSignal.getUser().addTag("platform", "android");
        
        // Ejemplo de cómo agregar más tags
        // OneSignal.getUser().addTag("plan_type", "premium");
        // OneSignal.getUser().addTag("city", "Caracas");
    }
}
