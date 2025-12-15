package com.medianet.oficinamovil.helper;

public interface MyHelper {
    void loading();
    void finishLoading();
    void webGoBack();

    void webLoadUrl(String url);
    void errorLoading();

    // New callbacks for specific error types
    void networkErrorLoading();
    void serverErrorLoading();
    void maintenanceMode();

} // myHelper End Here ===========
