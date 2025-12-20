package com.medianet.oficinamovil.controller;


import android.net.Uri;
import android.webkit.ValueCallback;

public class MyControl {

    //==================================================
    //For WebView upload
    public static String file_type     = "*/*";
    public static String cam_file_data = null;
    public static ValueCallback<Uri> file_data;
    public static ValueCallback<Uri[]> file_path;
    public final static int file_req_code = 1;
    public static String USER_AGENT = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Mobile Safari/537.36";
    public static String LOAD_ERROR_REASON = "";

    public static boolean NETWORK_AVAILABLE = true;
    public static boolean FAILED_FOR_OTHER_REASON = false;

    // Error state tracking
    public static boolean IS_NETWORK_ERROR = false;
    public static String CURRENT_URL = "";
    // Note: We removed URL_RETRY_COUNT - now using URL detection instead


} // MyControl End Here =========
