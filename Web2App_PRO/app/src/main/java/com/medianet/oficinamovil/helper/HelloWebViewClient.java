package com.medianet.oficinamovil.helper;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.medianet.oficinamovil.controller.MyControl;
import com.medianet.oficinamovil.controller.MyMethods;

import java.net.URISyntaxException;

public class HelloWebViewClient extends WebViewClient {

    Activity activity;
    static MyHelper myHelper;

    public HelloWebViewClient(MyHelper myHelper) {
        HelloWebViewClient.myHelper = myHelper;
    }

    public HelloWebViewClient(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        if (!MyMethods.isConnected(activity)) {
            myHelper.finishLoading();
        } else {
            myHelper.loading();
        }

        // ============================================================
        // DETECCIÓN DE PAYPAL - ABRIR EN APP NATIVA O NAVEGADOR
        // ============================================================
        // Si la URL contiene PayPal, intentar abrir la app nativa primero
        // Si no está instalada, abrir en el navegador como fallback
        if (url.contains("paypal.com") || url.contains("sandbox.paypal.com") || url.contains("paypalobjects.com")) {
            try {
                // Crear intent para abrir la URL
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                
                // Intentar abrir con la app de PayPal si está instalada
                // Package name de la app oficial de PayPal
                intent.setPackage("com.paypal.android.p2pmobile");
                
                // Verificar si la app de PayPal está instalada
                PackageManager pm = activity.getPackageManager();
                if (intent.resolveActivity(pm) != null) {
                    // La app de PayPal está instalada, abrirla
                    activity.startActivity(intent);
                    android.util.Log.d("PayPal", "✅ Abriendo PayPal en app nativa: " + url);
                } else {
                    // La app de PayPal NO está instalada, abrir en navegador
                    intent.setPackage(null); // Remover el package específico
                    activity.startActivity(intent);
                    android.util.Log.d("PayPal", "✅ App de PayPal no instalada, abriendo en navegador: " + url);
                }
                
                return true; // Indica que manejamos la URL
            } catch (Exception e) {
                android.util.Log.e("PayPal", "❌ Error al abrir PayPal: " + e.getMessage());
                // Si falla completamente, intentar cargar en el WebView
                return false;
            }
        }

        if (url.startsWith("http")) return false;//open web links as usual
        //try to find browse activity to handle uri
        Uri parsedUri = Uri.parse(url);
        PackageManager packageManager = activity.getPackageManager();
        Intent browseIntent = new Intent(Intent.ACTION_VIEW).setData(parsedUri);
        if (browseIntent.resolveActivity(packageManager) != null) {
            activity.startActivity(browseIntent);
            return true;
        }
        //if not activity found, try to parse intent://
        if (url.startsWith("intent:")) {
            try {
                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    activity.startActivity(intent);
                    return true;
                }

                //try to find fallback url
                String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                if (fallbackUrl != null) {
                    myHelper.webLoadUrl(fallbackUrl);
                    return true;
                }

                //invite to install
                Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(
                        Uri.parse("market://details?id=" + intent.getPackage()));
                if (marketIntent.resolveActivity(packageManager) != null) {
                    activity.startActivity(marketIntent);
                    return true;
                }
            } catch (URISyntaxException e) {
                //not an intent uri
            }
        }

        return true;
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String url) {
        MyControl.LOAD_ERROR_REASON = description;

        // Detect if this is a network error or server error based on error code
        // Network error codes: -2 (HOST_LOOKUP), -6 (CONNECT), -7 (IO), -8 (TIMEOUT), -9 (REDIRECT_LOOP)
        boolean isNetworkError = (errorCode == -2 || errorCode == -6 || errorCode == -7 || 
                                  errorCode == -8 || errorCode == -9);

        android.util.Log.d("WebViewError", "Error Code: " + errorCode + ", Description: " + description + 
                          ", URL: " + url + ", IsNetworkError: " + isNetworkError);

        if (isNetworkError || !MyControl.NETWORK_AVAILABLE) {
            // This is a NETWORK ERROR - no internet connection
            MyControl.IS_NETWORK_ERROR = true;
            MyControl.FAILED_FOR_OTHER_REASON = false;
            android.util.Log.d("WebViewError", "❌ Network error detected");
            
            if (myHelper != null) {
                myHelper.networkErrorLoading();
            }
        } else {
            // This is a SERVER/URL ERROR - internet is available but URL failed to load
            MyControl.IS_NETWORK_ERROR = false;
            MyControl.FAILED_FOR_OTHER_REASON = true;
            
            android.util.Log.d("WebViewError", "❌ Server error detected, retry count: " + MyControl.URL_RETRY_COUNT);

            // Attempt URL failover: oficina2 -> oficina3
            if (MyControl.URL_RETRY_COUNT < MyControl.MAX_RETRY_ATTEMPTS) {
                MyControl.URL_RETRY_COUNT++;
                
                // Try the fallback URL
                String fallbackUrl = view.getContext().getString(com.medianet.oficinamovil.R.string.Website_Link_Fallback);
                android.util.Log.d("WebViewError", "🔄 Attempting fallback URL: " + fallbackUrl);
                
                MyControl.CURRENT_URL = fallbackUrl;
                view.loadUrl(fallbackUrl);
                
            } else {
                // Both URLs failed - show maintenance mode
                android.util.Log.d("WebViewError", "❌ Both URLs failed - entering maintenance mode");
                
                if (myHelper != null) {
                    myHelper.maintenanceMode();
                }
            }
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        // do your stuff here
        android.util.Log.d("WebViewError", "✅ Page loaded successfully: " + url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        MyControl.FAILED_FOR_OTHER_REASON = false;
        MyControl.CURRENT_URL = url;
        android.util.Log.d("WebViewError", "🔄 Loading started: " + url);
    }
} // HelloWebViewClient end here ======================
