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
        // DETECCIÓN DE PAYPAL - ELIMINADO PARA PERMITIR POPUP INTERNO
        // ============================================================
        // Anteriormente se forzaba abrir la app de PayPal o el navegador externa.
        // Se ha comentado/eliminado para permitir que la lógica de onCreateWindow
        // en ChromeClient maneje la ventana emergente DENTRO de la app.


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

        android.util.Log.d("WebViewError", "❌ Error Code: " + errorCode + ", Description: " + description + 
                          ", URL: " + url + ", Network Available: " + MyControl.NETWORK_AVAILABLE);

        // CRITICAL: Only show network error when there is ACTUALLY no internet connection
        // If we have internet, ANY error should trigger URL failover, not network error
        if (!MyControl.NETWORK_AVAILABLE) {
            // NO INTERNET CONNECTION - Show network error dialog
            MyControl.IS_NETWORK_ERROR = true;
            MyControl.FAILED_FOR_OTHER_REASON = false;
            android.util.Log.d("WebViewError", "❌ No internet connection - showing network error");
            
            if (myHelper != null) {
                myHelper.networkErrorLoading();
            }
        } else {
            // INTERNET IS AVAILABLE - This is a URL/SERVER ERROR
            MyControl.IS_NETWORK_ERROR = false;
            MyControl.FAILED_FOR_OTHER_REASON = true;
            
            // Get URLs to compare
            String primaryUrl = view.getContext().getString(com.medianet.oficinamovil.R.string.Website_Link).trim();
            String fallbackUrl = view.getContext().getString(com.medianet.oficinamovil.R.string.Website_Link_Fallback).trim();
            
            android.util.Log.d("WebViewError", "🔍 Primary URL: " + primaryUrl);
            android.util.Log.d("WebViewError", "🔍 Fallback URL: " + fallbackUrl);
            android.util.Log.d("WebViewError", "🔍 Failed URL: " + url);

            // Comparar URLs para evitar bucles, ignorando query params o diferencias menores
            boolean isO2 = url.contains("oficina2.fibextelecom.net");
            boolean isO3 = url.contains("oficina3.fibextelecom.net");
            
            if (isO2) {
                // Primary URL failed - try fallback
                android.util.Log.d("WebViewError", "🔄 Primary URL (oficina2) failed - switching to fallback (oficina3)");
                
                // Show error message before switching
                if (myHelper != null) {
                    myHelper.serverErrorLoading();
                }
                
                MyControl.CURRENT_URL = fallbackUrl;
                view.loadUrl(fallbackUrl);
                
            } else if (isO3) {
                // Fallback URL also failed - show maintenance
                android.util.Log.d("WebViewError", "❌ Fallback URL (oficina3) also failed - showing maintenance");
                
                if (myHelper != null) {
                    myHelper.maintenanceMode();
                }
            } else {
                // Unknown URL failed - try fallback as default
                android.util.Log.d("WebViewError", "⚠️ Unknown URL failed - trying fallback");
                
                // Show error message before switching
                if (myHelper != null) {
                    myHelper.serverErrorLoading();
                }
                
                MyControl.CURRENT_URL = fallbackUrl;
                view.loadUrl(fallbackUrl);
            }
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        // CRITICAL: Only hide loading overlay (calling pageFinished) if NO ERROR occurred.
        // This prevents showing the default browser error page (white screen/dino).
        if (!MyControl.FAILED_FOR_OTHER_REASON && !MyControl.IS_NETWORK_ERROR) {
            android.util.Log.d("WebViewError", "✅ Page loaded successfully: " + url);
            if (myHelper != null) {
                myHelper.pageFinished();
            }
        } else {
            android.util.Log.d("WebViewError", "⚠️ onPageFinished called but error flag is TRUE. Keeping overlay visible to hide browser error page.");
            // We do NOT call pageFinished(), so the custom loading overlay stays visible 
            // covering the ugly browser error page.
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        // Reset flags at the start of a NEW load attempt
        MyControl.FAILED_FOR_OTHER_REASON = false;
        
        // Note: We don't reset IS_NETWORK_ERROR here because it depends on the actual connection state
        // checked in onReceivedError, but we assume it's a fresh attempt.
        
        MyControl.CURRENT_URL = url;
        android.util.Log.d("WebViewError", "🔄 Loading started: " + url);
        
        if (myHelper != null) {
            myHelper.pageStarted();
        }
    }
} // HelloWebViewClient end here ======================
