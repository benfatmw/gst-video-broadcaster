package com.kalyzee.panel_connection_manager;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.kalyzee.panel_connection_manager.exceptions.session.LoginUnauthorizedAccessException;
import com.kalyzee.panel_connection_manager.mappers.session.LoginRequestContent;


public class PanelConnectionTask implements Runnable {
    private static final String TAG = "PanelConnectionTask";
    private static final String FAILED_TO_ESTABLISH_CONNECTION_WITH_THE_PANEL
            = "Failed to establish connection with the Panel.";
    private static final String ATTEMPTING_TO_RESTART_APPLICATION = "---------------- " +
            "Device credentials have been revoked. Attempting to restart the application.";
    private volatile boolean looping = true;
    private final String deviceId;
    private final String deviceCertificate;
    private final Session session;
    private final CredentialsManager credentialsManager;

    public PanelConnectionTask(String deviceId, Session session, CredentialsManager credentialsManager) {
        this.deviceId = deviceId;
        this.session = session;
        this.credentialsManager = credentialsManager;
        this.deviceCertificate = credentialsManager.getDeviceCertificate();
    }

    public void terminate() {
        looping = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
            try {
                /** Check if the camera is logged in */
                if (!session.isLoggedIn()) {
                    /** Authenticate/log in using credentials */
                    session.login(new LoginRequestContent(deviceId, deviceCertificate));
                    /**
                     *  The camera is successfully logged in/authenticated to the Panel.
                     *  --> Start listening to Panel requests
                     */
                    session.handlePanelRequests();
                }
                Thread.sleep(10000);
            } catch (LoginUnauthorizedAccessException e) {
                /**
                 * Device credentials are revoked.
                 * --> Must delete the current credentials (so that the device reconnects to the fallback server)
                 * then exit the application.
                 */
                if (credentialsManager.isFallbackAllowed()) {
                    credentialsManager.deleteCredentialsFile();
                }
                Session.onLoginStatusChanged(false);
            } catch (Exception e) {
                Session.onLoginStatusChanged(false);
                Log.e(TAG, FAILED_TO_ESTABLISH_CONNECTION_WITH_THE_PANEL, e);
            }
    }
}
