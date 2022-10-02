package com.kalyzee.kontroller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.text.format.Formatter;
import android.util.Log;

import com.kalyzee.kontroller.registration.LocalWebsocketServer;
import com.kalyzee.panel_connection_manager.CredentialsManager;
import com.kalyzee.panel_connection_manager.ICredentialsUpdatedListener;
import com.kalyzee.panel_connection_manager.SessionManager;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String FAILED_TO_LOGIN_AFTER_REGISTRATION = "Failed to login after credentials update.";
    private static final String FAILED_TO_SETUP_PANEL_CONNECTION = "Failed to create and set up panel connection.";
    private static final String DEVICE_IS_NOT_REGISTERED = "Device is not registered.";

    private static final String CREDENTIALS_PREFERENCE = "credentials";

    private SessionManager sessionManager;
    private CredentialsManager credentialsManager;
    private LocalWebsocketServer wsServer;
    private String macAddress;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e(TAG, "Required permissions are not grant by the system");
        }
        macAddress = wifiManager.getConnectionInfo().getMacAddress();

        /**
         * If the camera has been already registered -->
         * Instantiate the Socket io manager and start panel connection worker thread
         * Login/authenticate to Panel if the camera has been registered
         * and set up a listener to handle its requests/commands.
         */
        try {
            credentialsManager = new CredentialsManager(CREDENTIALS_PREFERENCE, getApplicationContext());
            credentialsManager.registerCredentialsUpdatedListener(new ICredentialsUpdatedListener() {
                @Override
                public void onUpdated(String panelUri, String certificate) {
                    try {
                        /** Stop the current session if there is any */
                        if (sessionManager != null) {
                            sessionManager.stopSession();
                            sessionManager = null;
                        }
                        sessionManager = new SessionManagerBuilder(credentialsManager, getApplicationContext()).build();
                        sessionManager.startSession(macAddress);
                    } catch (Exception e) {
                        Log.e(TAG, FAILED_TO_LOGIN_AFTER_REGISTRATION, e);
                    }
                }
            });

            if (credentialsManager.isRegistered()) {
                sessionManager = new SessionManagerBuilder(credentialsManager, getApplicationContext()).build();
                sessionManager.startSession(macAddress);
            } else {
                Log.i(TAG, FAILED_TO_SETUP_PANEL_CONNECTION);
                wsServer = new LocalWebsocketServer(Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()),
                        macAddress,
                        credentialsManager);
                wsServer.start();
            }
        } catch (Exception e) {
            Log.e(TAG, FAILED_TO_SETUP_PANEL_CONNECTION, e);
        }
    }

    protected void onDestroy() {
        sessionManager.stopSession();
        super.onDestroy();
    }

}