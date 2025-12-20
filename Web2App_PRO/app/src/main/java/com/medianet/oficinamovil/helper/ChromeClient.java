package com.medianet.oficinamovil.helper;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.medianet.oficinamovil.controller.MyControl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ChromeClient extends WebChromeClient {

    Activity activity;
    static MyHelper myHelper;
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    protected FrameLayout mFullscreenContainer;
    private int mOriginalOrientation;
    private int mOriginalSystemUiVisibility;

    public ChromeClient(MyHelper myHelper) {
        ChromeClient.myHelper = myHelper;
    }

    public ChromeClient(Activity activity) {
        this.activity = activity;
    }

    public ChromeClient() {
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (newProgress >= 100) {
            // Page loading finish
            /*  progressBar.setVisibility(View.GONE);*/
            myHelper.finishLoading();
        } else {
            /*progressBar.setVisibility(View.VISIBLE);*/
            myHelper.loading();
        }
    }

    public Bitmap getDefaultVideoPoster() {
        if (mCustomView == null) {
            return null;
        }
        return BitmapFactory.decodeResource(activity.getResources(), 2130837573);
    }

    public void onHideCustomView() {
        ((FrameLayout) activity.getWindow().getDecorView()).removeView(this.mCustomView);
        this.mCustomView = null;
        activity.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
        activity.setRequestedOrientation(this.mOriginalOrientation);
        this.mCustomViewCallback.onCustomViewHidden();
        this.mCustomViewCallback = null;
    }

    public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
        if (this.mCustomView != null) {
            onHideCustomView();
            return;
        }
        this.mCustomView = paramView;
        this.mOriginalSystemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
        this.mOriginalOrientation = activity.getRequestedOrientation();
        this.mCustomViewCallback = paramCustomViewCallback;
        ((FrameLayout) activity.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
        activity.getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    // ============================================================
    // SOPORTE PARA VENTANAS EMERGENTES DE PAYPAL
    // ============================================================
    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
        // Crear un nuevo WebView para el popup (interno en la app)
        final WebView newWebView = new WebView(activity);
        
        // Configuraciones necesarias para que funcione PayPal y otras webs modernas
        android.webkit.WebSettings webSettings = newWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportMultipleWindows(true); 
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        
        // Usar UserAgent del WebView padre si es posible, o el default
        if (view != null && view.getSettings() != null) {
            webSettings.setUserAgentString(view.getSettings().getUserAgentString());
        }

        // Crear un Dialog de pantalla completa para mostrar el WebView
        final android.app.Dialog dialog = new android.app.Dialog(activity, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dialog.setContentView(newWebView);
        dialog.setCancelable(true); // Permitir cerrar con BACK
        dialog.setOnCancelListener(d -> {
            newWebView.destroy();
        });
        dialog.show();

        // WebChromeClient para manejar el cierre de la ventana (window.close())
        newWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onCloseWindow(WebView window) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (window != null) {
                    window.destroy();
                }
            }
        });

        // WebViewClient para mantener la navegación dentro de este popup
        newWebView.setWebViewClient(new android.webkit.WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Verificar si es un esquema especial (mailto, tel, whatsapp, etc.) y sacarlo fuera
                if (url.startsWith("mailto:") || url.startsWith("tel:") || url.startsWith("whatsapp:")) {
                     Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                     activity.startActivity(intent);
                     return true;
                }
                // Para HTTP/HTTPS (PayPal), cargar dentro del mismo WebView popup
                return false; 
            }
        });

        // Transportar el evento al nuevo WebView
        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(newWebView);
        resultMsg.sendToTarget();

        android.util.Log.d("PayPal", "✅ Popup WebView creado y mostrado en Dialog interno");
        return true;
    }

    //============================================
    //============================================

    /*-- handling input[type="file"] requests for android API 21+ --*/
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

        Log.d(TAG, "onShowFileChooser called");
        
        if(file_permission() && Build.VERSION.SDK_INT >= 21) {
            
            // Cancel any existing file path callback
            if (MyControl.file_path != null) {
                MyControl.file_path.onReceiveValue(null);
            }
            
            MyControl.file_path = filePathCallback;
            Intent takePictureIntent = null;

            // Solo manejar imágenes
            boolean includePhoto = true;

            // Intent para tomar foto con la cámara
            takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = create_image();
                    takePictureIntent.putExtra("PhotoPath", MyControl.cam_file_data);
                } catch (IOException ex) {
                    Log.e(TAG, "Image file creation failed", ex);
                }
                if (photoFile != null) {
                    MyControl.cam_file_data = "file:" + photoFile.getAbsolutePath();
                    
                    // Use FileProvider for Android 7.0+
                    Uri photoURI;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        photoURI = androidx.core.content.FileProvider.getUriForFile(
                            activity,
                            activity.getPackageName() + ".fileprovider",
                            photoFile
                        );
                    } else {
                        photoURI = Uri.fromFile(photoFile);
                    }
                    
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    MyControl.cam_file_data = null;
                    takePictureIntent = null;
                }
            }

            // Intent para seleccionar imagen de la galería
            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("image/*"); // Solo imágenes
            
            // Allow multiple file selection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }

            // Preparar los intents adicionales (cámara)
            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Seleccionar imagen");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            
            try {
                activity.startActivityForResult(chooserIntent, MyControl.file_req_code);
                Log.d(TAG, "File chooser started successfully");
            } catch (Exception e) {
                Log.e(TAG, "Error starting file chooser", e);
                MyControl.file_path = null;
                return false;
            }
            
            return true;
        } else {
            Log.w(TAG, "File permission not granted or API level < 21");
            return false;
        }
    }

    public boolean file_permission(){

        // Android 13+ (API 33+) - Solo imágenes
        if (Build.VERSION.SDK_INT >= 33) {

            boolean hasMediaImages = ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
            boolean hasCamera = ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

            if (!hasMediaImages || !hasCamera) {
                ActivityCompat.requestPermissions(
                        activity, new String[]{
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.CAMERA
                        }, 1);
                Log.v("WebBrowser", "Permission Requested (Android 13+) - Solo imágenes");
                return false;
            } else {
                return true;
            }

        }
        // Android 6.0 - 12 (API 23-32)
        else if (Build.VERSION.SDK_INT >= 23) {

            boolean hasStorage = ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            boolean hasCamera = ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

            if (!hasStorage || !hasCamera) {
                ActivityCompat.requestPermissions(
                        activity, new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        }, 1);
                Log.v("WebBrowser", "Permission Requested (Android 6-12)");
                return false;
            } else {
                return true;
            }

        }
        // Android 5.x and below
        else {
            return true;
        }

    }

    private File create_image() throws IOException{
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_"+timeStamp+"_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName,".jpg",storageDir);
    }

} // ChromeClient End Here =============
