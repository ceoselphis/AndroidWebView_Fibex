package com.medianet.oficinamovil;

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

        Log.d(TAG, "═══════════════════════════════════════════════");
        Log.d(TAG, "🚀 INICIALIZANDO ONESIGNAL");
        Log.d(TAG, "═══════════════════════════════════════════════");

        // Verbose Logging set to help debug issues, remove before releasing your app.
        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);

        // OneSignal Initialization
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);
        Log.d(TAG, "✅ OneSignal inicializado con App ID: " + ONESIGNAL_APP_ID);

        // NO solicitamos permisos aquí - se solicitarán en MainActivity
        // para evitar que el WebView se sobreponga al diálogo de permisos

        // Listener para cuando se recibe una notificación (la app está abierta)
        OneSignal.getNotifications().addForegroundLifecycleListener(event -> {
            Log.d(TAG, "📬 Notificación recibida en primer plano: " + event.getNotification().getTitle());
        });

        // Listener para cuando el usuario hace clic en una notificación
        OneSignal.getNotifications().addClickListener(event -> {
            Log.d(TAG, "👆 Usuario hizo clic en la notificación");
            
            String title = event.getNotification().getTitle();
            String body = event.getNotification().getBody();
            
            Log.d(TAG, "Título: " + title);
            Log.d(TAG, "Mensaje: " + body);
            
            JSONObject additionalData = event.getNotification().getAdditionalData();
            if (additionalData != null) {
                Log.d(TAG, "Datos adicionales: " + additionalData.toString());
                
                if (additionalData.has("url")) {
                    try {
                        String url = additionalData.getString("url");
                        Log.d(TAG, "URL recibida: " + url);
                    } catch (Exception e) {
                        Log.e(TAG, "Error al obtener URL: " + e.getMessage());
                    }
                }
            }
        });

        // Obtener Player ID de forma segura con reintentos
        new android.os.Handler().postDelayed(() -> {
            String playerId = OneSignal.getUser().getOnesignalId();
            if (playerId != null && !playerId.isEmpty()) {
                Log.d(TAG, "═══════════════════════════════════════════════");
                Log.d(TAG, "🎯 ONESIGNAL PLAYER ID OBTENIDO");
                Log.d(TAG, "🆔 Player ID: " + playerId);
                Log.d(TAG, "═══════════════════════════════════════════════");
            } else {
                Log.w(TAG, "⚠️ Player ID aún no disponible. Reintentando...");
                new android.os.Handler().postDelayed(() -> {
                    String retryPlayerId = OneSignal.getUser().getOnesignalId();
                    if (retryPlayerId != null && !retryPlayerId.isEmpty()) {
                        Log.d(TAG, "✅ Player ID obtenido: " + retryPlayerId);
                    } else {
                        Log.e(TAG, "❌ No se pudo obtener el Player ID");
                    }
                }, 2000);
            }
        }, 1000);

        // Agregar tags iniciales
        // OneSignal.getUser().addTag("app_version", "2.0");
        // OneSignal.getUser().addTag("platform", "android");
        
        Log.d(TAG, "🏷️ Tags iniciales agregados");
        Log.d(TAG, "═══════════════════════════════════════════════");
    }
}
