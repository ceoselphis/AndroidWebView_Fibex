package com.oficina2.fibex_telecom;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.onesignal.OneSignal;
import org.json.JSONObject;

/**
 * Actividad opcional para manejar notificaciones con deep linking
 * 
 * Esta actividad se puede usar para abrir URLs específicas cuando
 * el usuario hace clic en una notificación.
 * 
 * Para usarla, agrega esto en tu AndroidManifest.xml dentro de <application>:
 * 
 * <activity
 *     android:name=".NotificationOpenedActivity"
 *     android:exported="false"
 *     android:noHistory="true" />
 */
public class NotificationOpenedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtener el intent que abrió esta actividad
        Intent intent = getIntent();
        
        // Verificar si hay datos de la notificación
        if (intent != null && intent.getExtras() != null) {
            Bundle extras = intent.getExtras();
            
            // Aquí puedes obtener datos personalizados de la notificación
            String customUrl = extras.getString("url");
            
            if (customUrl != null && !customUrl.isEmpty()) {
                // Abrir MainActivity con la URL personalizada
                Intent mainIntent = new Intent(this, MainActivity.class);
                mainIntent.putExtra("notification_url", customUrl);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
            } else {
                // Si no hay URL personalizada, simplemente abrir MainActivity
                Intent mainIntent = new Intent(this, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
            }
        } else {
            // Si no hay datos, abrir MainActivity normalmente
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
        }
        
        // Cerrar esta actividad
        finish();
    }
}
