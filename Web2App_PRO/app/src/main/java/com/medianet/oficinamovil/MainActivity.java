package com.medianet.oficinamovil;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.medianet.oficinamovil.controller.MyControl;
import com.medianet.oficinamovil.controller.MyMethods;
import com.medianet.oficinamovil.helper.MyHelper;
import com.medianet.oficinamovil.helper.ChromeClient;
import com.medianet.oficinamovil.helper.HelloWebViewClient;
import com.medianet.oficinamovil.network.NetworkStateReceiver;
import com.onesignal.OneSignal;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    WebView webView;
    LottieAnimationView progress_loading;
    LinearLayout no_Internet;
    TextView nonetTitle, nonetDescription;
    
    // Error dialog references
    private AlertDialog currentErrorDialog;
    
    // WebView Loading Overlay components
    private LinearLayout webviewLoadingOverlay;
    private LinearLayout errorMessageContainer;
    private TextView errorMessageText;
    private com.airbnb.lottie.LottieAnimationView loadingAnimation;
    private TextView loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // For Internet
        MyMethods.startNetworkBroadcastReceiver(this);

        // variable initialize
        webView = findViewById(R.id.webView);
        progress_loading = findViewById(R.id.progress_loading);
        no_Internet = findViewById(R.id.No_Internet);
        nonetTitle = findViewById(R.id.nonetTitle);
        nonetDescription = findViewById(R.id.nonetDescription);
        
        // WebView Loading Overlay
        webviewLoadingOverlay = findViewById(R.id.webview_loading_overlay);
        errorMessageContainer = findViewById(R.id.error_message_container);
        errorMessageText = findViewById(R.id.error_message_text);
        loadingAnimation = findViewById(R.id.loading_animation);
        loadingText = findViewById(R.id.loading_text);

        // IMPORTANTE: Ocultar el WebView completamente al inicio
        // Esto evita que CUALQUIER cosa se sobreponga al diálogo de permisos
        webView.setVisibility(View.GONE);
        
        // Mostrar el loading mientras esperamos la respuesta del usuario
        progress_loading.setVisibility(View.VISIBLE);

        // IMPORTANTE: Solicitar permisos de notificaciones ANTES de configurar el WebView
        // Esto evita que el WebView se sobreponga al diálogo de permisos
        requestNotificationPermissions();
    }

    /**
     * Solicita permisos de notificaciones y luego permisos de archivos
     */
    private void requestNotificationPermissions() {
        OneSignal.getNotifications().requestPermission(true, com.onesignal.Continue.with(result -> {
            // Una vez que el usuario responde al permiso de notificaciones,
            // solicitamos los permisos de archivos/imágenes
            runOnUiThread(() -> {
                requestFilePermissions();
            });
        }));
    }

    /**
     * Solicita permisos de archivos/imágenes y luego inicializa el WebView
     */
    private void requestFilePermissions() {
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            // Android 13+ (API 33+) - Solo solicitar READ_MEDIA_IMAGES
            if (androidx.core.content.ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_MEDIA_IMAGES) != android.content.pm.PackageManager.PERMISSION_GRANTED ||
                androidx.core.content.ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CAMERA) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                
                androidx.core.app.ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                        android.Manifest.permission.READ_MEDIA_IMAGES,
                        android.Manifest.permission.CAMERA
                    },
                    100
                );
            } else {
                // Permisos ya otorgados, inicializar WebView
                initializeWebViewAfterPermissions();
            }
        } else if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Android 6.0 - 12 (API 23-32)
            if (androidx.core.content.ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED ||
                androidx.core.content.ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CAMERA) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                
                androidx.core.app.ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA
                    },
                    100
                );
            } else {
                // Permisos ya otorgados, inicializar WebView
                initializeWebViewAfterPermissions();
            }
        } else {
            // Android 5.x y anteriores - No se requieren permisos en tiempo de ejecución
            initializeWebViewAfterPermissions();
        }
    }

    /**
     * Callback para manejar la respuesta de los permisos
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == 100) {
            // Los permisos fueron respondidos (aceptados o rechazados)
            // Inicializamos el WebView de todas formas
            initializeWebViewAfterPermissions();
        }
    }

    /**
     * Inicializa el WebView después de solicitar todos los permisos
     */
    private void initializeWebViewAfterPermissions() {
        // Ocultar el loading
        progress_loading.setVisibility(View.GONE);
        
        // Mostrar el WebView
        webView.setVisibility(View.VISIBLE);
        
        // Inicializar el WebView
        initializeWebView();
    }

    /**
     * Inicializa y configura el WebView
     */
    private void initializeWebView() {
        // webview settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setUserAgentString(MyControl.USER_AGENT);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setAllowContentAccess(true);
        webSettings.setSaveFormData(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setEnableSmoothTransition(true);
        webSettings.setBlockNetworkLoads(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        
        // ============================================================
        // CONFIGURACIONES ADICIONALES PARA PAYPAL
        // ============================================================
        webSettings.setSupportMultipleWindows(true); // Permitir ventanas emergentes
        webSettings.setDatabaseEnabled(true); // Habilitar base de datos
        
        // Permitir contenido mixto (HTTPS y HTTP) - necesario para algunos flujos de PayPal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        
        // User Agent personalizado para identificar la app
        String userAgent = webSettings.getUserAgentString();
        webSettings.setUserAgentString(userAgent + " FibexOficinaMovil/1.0");

        // clear old cache/history
        webView.clearCache(true);
        webView.clearHistory();

        webView.setWebChromeClient(new ChromeClient(MainActivity.this));
        webView.setWebViewClient(new HelloWebViewClient(MainActivity.this));
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        // ============================================================
        // AGREGAR ONESIGNAL BRIDGE PARA COMUNICACIÓN CON IONIC ANGULAR
        // ============================================================
        // Este puente permite que tu aplicación Ionic Angular acceda a OneSignal
        // desde JavaScript usando: window.OneSignalBridge.metodo()
        webView.addJavascriptInterface(new com.medianet.oficinamovil.helper.OneSignalBridge(), "OneSignalBridge");
        android.util.Log.d("MainActivity", "✅ OneSignalBridge agregado al WebView");



        // HelloWebViewClient
        new HelloWebViewClient(new MyHelper() {
            @Override
            public void loading() { }

            @Override
            public void finishLoading() { }

            @Override
            public void webGoBack() {
                webView.goBack();
            }

            @Override
            public void webLoadUrl(String url) {
                webView.loadUrl(url);
            }

            @Override
            public void errorLoading() {
                if (MyControl.NETWORK_AVAILABLE) {
                    no_Internet.setVisibility(View.VISIBLE);
                    nonetTitle.setText("Website Load Failed");
                    nonetDescription.setText("Error Reason:\n" + MyControl.LOAD_ERROR_REASON);
                }
            }

            @Override
            public void networkErrorLoading() {
                runOnUiThread(() -> showNetworkErrorDialog());
            }

            @Override
            public void serverErrorLoading() {
                // Server errors trigger automatic failover
                // Show error message on loading overlay before switching
                runOnUiThread(() -> showLoadingError("Conectando al servidor alternativo..."));
            }

            @Override
            public void maintenanceMode() {
                runOnUiThread(() -> showMaintenanceDialog());
            }

            @Override
            public void pageStarted() {
                runOnUiThread(() -> showLoadingOverlay());
            }

            @Override
            public void pageFinished() {
                runOnUiThread(() -> hideLoadingOverlay());
            }
        });

        // Handle WebLoading
        new ChromeClient(new MyHelper() {
            @Override
            public void loading() {
                progress_loading.setVisibility(View.VISIBLE);
            }

            @Override
            public void finishLoading() {
                progress_loading.setVisibility(View.GONE);
            }

            @Override
            public void webGoBack() { }

            @Override
            public void webLoadUrl(String url) { }

            @Override
            public void errorLoading() { }

            @Override
            public void networkErrorLoading() { }

            @Override
            public void serverErrorLoading() { }

            @Override
            public void maintenanceMode() { }

            @Override
            public void pageStarted() { }

            @Override
            public void pageFinished() { }
        });

        // Check if opened from notification with custom URL
        String notificationUrl = getIntent().getStringExtra("notification_url");
        if (notificationUrl != null && !notificationUrl.isEmpty()) {
            // Load URL from notification
            webView.loadUrl(notificationUrl);
        } else {
            // Load default website
            webView.loadUrl(getString(R.string.Website_Link));
        }
    }

    @Override
    public void networkAvailable() {
        MyControl.NETWORK_AVAILABLE = true;

        // Dismiss error dialog if it's open
        if (currentErrorDialog != null && currentErrorDialog.isShowing()) {
            currentErrorDialog.dismiss();
        }

        if (!MyControl.FAILED_FOR_OTHER_REASON)
            no_Internet.setVisibility(View.GONE);
        else
            no_Internet.setVisibility(View.VISIBLE);

        // Auto-retry WebView loading ONLY if the error was due to network issues
        if (MyControl.IS_NETWORK_ERROR) {
            android.util.Log.d("MainActivity", "🔄 Network restored - auto-retrying WebView");
            MyControl.IS_NETWORK_ERROR = false;
            retryWebViewLoad();
        }
    }

    @Override
    public void networkUnavailable() {
        MyControl.NETWORK_AVAILABLE = false;
        no_Internet.setVisibility(View.VISIBLE);
        nonetTitle.setText("No Internet");
        nonetDescription.setText("Please Check Internet Connection and Try Again..");
    }

    @Override
    protected void onPause() {
        MyMethods.unregisterNetworkBroadcastReceiver(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        MyMethods.registerNetworkBroadcastReceiver(this);
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;

            if (resultCode == Activity.RESULT_CANCELED) {
                MyControl.file_path.onReceiveValue(null);
                return;
            }

            if (resultCode == Activity.RESULT_OK) {
                if (null == MyControl.file_path) {
                    return;
                }
                ClipData clipData;
                String stringData;

                try {
                    clipData = intent.getClipData();
                    stringData = intent.getDataString();
                } catch (Exception e) {
                    clipData = null;
                    stringData = null;
                }
                if (clipData == null && stringData == null && MyControl.cam_file_data != null) {
                    results = new Uri[]{Uri.parse(MyControl.cam_file_data)};
                } else {
                    if (clipData != null) {
                        final int numSelectedFiles = clipData.getItemCount();
                        results = new Uri[numSelectedFiles];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            results[i] = clipData.getItemAt(i).getUri();
                        }
                    } else {
                        try {
                            Bitmap cam_photo = (Bitmap) intent.getExtras().get("data");
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            cam_photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                            stringData = MediaStore.Images.Media.insertImage(this.getContentResolver(), cam_photo, null, null);
                        } catch (Exception ignored) { }

                        results = new Uri[]{Uri.parse(stringData)};
                    }
                }
            }

            MyControl.file_path.onReceiveValue(results);
            MyControl.file_path = null;
        } else {
            if (requestCode == MyControl.file_req_code) {
                if (null == MyControl.file_data) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                MyControl.file_data.onReceiveValue(result);
                MyControl.file_data = null;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    @Override
    public void onBackPressed() {
        if (!webView.canGoBack()) {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                super.onBackPressed();
                finishAndRemoveTask();
            } else {
                Toast.makeText(getBaseContext(), "Press again to exit",
                        Toast.LENGTH_SHORT).show();
            }
            mBackPressed = System.currentTimeMillis();
        } else {
            webView.goBack();
        }
    }

    /**
     * Show network error dialog with retry button
     */
    private void showNetworkErrorDialog() {
        // Dismiss previous dialog if exists
        if (currentErrorDialog != null && currentErrorDialog.isShowing()) {
            currentErrorDialog.dismiss();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_network_error, null);
        builder.setView(dialogView);
        
        currentErrorDialog = builder.create();
        currentErrorDialog.setCancelable(false);
        
        // Make dialog background transparent to show rounded corners
        if (currentErrorDialog.getWindow() != null) {
            currentErrorDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Set up close button
        ImageView btnClose = dialogView.findViewById(R.id.btn_close_dialog);
        btnClose.setOnClickListener(v -> {
            if (currentErrorDialog != null) {
                currentErrorDialog.dismiss();
            }
        });

        // Set up retry button
        Button btnRetry = dialogView.findViewById(R.id.btn_retry);
        btnRetry.setOnClickListener(v -> {
            if (currentErrorDialog != null) {
                currentErrorDialog.dismiss();
            }
            retryWebViewLoad();
        });

        currentErrorDialog.show();
    }

    /**
     * Show maintenance mode dialog
     */
    private void showMaintenanceDialog() {
        // Dismiss previous dialog if exists
        if (currentErrorDialog != null && currentErrorDialog.isShowing()) {
            currentErrorDialog.dismiss();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_maintenance, null);
        builder.setView(dialogView);
        
        currentErrorDialog = builder.create();
        currentErrorDialog.setCancelable(false);
        
        // Make dialog background transparent to show rounded corners
        if (currentErrorDialog.getWindow() != null) {
            currentErrorDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Set up close button
        ImageView btnClose = dialogView.findViewById(R.id.btn_close_dialog);
        btnClose.setOnClickListener(v -> {
            if (currentErrorDialog != null) {
                currentErrorDialog.dismiss();
            }
        });

        // Set up OK button
        Button btnOk = dialogView.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(v -> {
            if (currentErrorDialog != null) {
                currentErrorDialog.dismiss();
            }
        });

        currentErrorDialog.show();
    }

    /**
     * Retry loading the WebView with appropriate URL
     */
    private void retryWebViewLoad() {
        // Reset error state
        MyControl.IS_NETWORK_ERROR = false;
        
        // Determine which URL to load
        String urlToLoad;
        if (MyControl.CURRENT_URL != null && !MyControl.CURRENT_URL.isEmpty()) {
            // Retry the last attempted URL
            urlToLoad = MyControl.CURRENT_URL;
        } else {
            // Load the primary URL
            urlToLoad = getString(R.string.Website_Link);
        }
        
        android.util.Log.d("MainActivity", "🔄 Retrying WebView with URL: " + urlToLoad);
        webView.loadUrl(urlToLoad);
    }

    /**
     * Show loading overlay (hides WebView to prevent showing browser error page)
     */
    private void showLoadingOverlay() {
        webviewLoadingOverlay.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
        errorMessageContainer.setVisibility(View.GONE);
        loadingAnimation.setVisibility(View.VISIBLE);
        loadingText.setVisibility(View.VISIBLE);
        loadingText.setText("Cargando...");
    }

    /**
     * Hide loading overlay and show WebView (page loaded successfully)
     */
    private void hideLoadingOverlay() {
        webviewLoadingOverlay.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
    }

    /**
     * Show error message on loading overlay before failover
     */
    private void showLoadingError(String message) {
        webviewLoadingOverlay.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
        
        // Hide loading animation and text
        loadingAnimation.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);
        
        // Show error message with warning icon
        errorMessageContainer.setVisibility(View.VISIBLE);
        errorMessageText.setText(message);
        
        // Auto-hide after 2 seconds (failover will happen)
        new Handler().postDelayed(() -> {
            errorMessageContainer.setVisibility(View.GONE);
            loadingAnimation.setVisibility(View.VISIBLE);
            loadingText.setVisibility(View.VISIBLE);
        }, 2000);
    }

    // =========================================================================
    // DEBUG: TOUCH URL DEBUGGER (Triple tap to see current URL)
    // Cooment this lines to disable this feature
    // =========================================================================
    // private int debugClicks = 0;
    // private long debugLastClickTime = 0;

    // @Override
    // public boolean dispatchTouchEvent(MotionEvent ev) {
    //     if (ev.getAction() == MotionEvent.ACTION_DOWN) {
    //         long currentTime = System.currentTimeMillis();
    //         if (currentTime - debugLastClickTime < 500) {
    //             debugClicks++;
    //         } else {
    //             debugClicks = 1;
    //         }
    //         debugLastClickTime = currentTime;

    //         if (debugClicks >= 3) {
    //             String currentUrl = webView.getUrl();
    //             String msg = "Current URL: " + (currentUrl != null ? currentUrl : "Null");
    //             Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    //             debugClicks = 0;
    //         }
    //     }
    //     return super.dispatchTouchEvent(ev);
    // }
    // =========================================================================

} // Public Class End Here ==========================
