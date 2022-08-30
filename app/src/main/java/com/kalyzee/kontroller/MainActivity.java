package com.kalyzee.kontroller;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.kalyzee.kontroller.databinding.ActivityMainBinding;
import com.kalyzee.panel_connection_manager.SocketIoManager;
import com.kalyzee.panel_connection_manager.mappers.admin.RegisterRequestContent;
import com.kalyzee.panel_connection_manager.mappers.session.LoginRequestContent;
import com.kalyzee.panel_connection_manager.utils.CredentialsUtils;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String CREDENTIALS_PREFERENCE = "credentials";

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private SharedPreferences credentialsPref;

    // Socket IO manager dependencies
    private SocketIoManager socketMgr;
    private PanelConnectionRunnable panelConnectionRunnable;
    private Thread panelConnectionWorkerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        try {
            credentialsPref = EncryptedSharedPreferences.create(
                    CREDENTIALS_PREFERENCE,
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            Log.e(TAG, "Failed to create Credentials shared preferences.", e);
        }

        // Start PAMERA socket client:
        // Login/authenticate to Panel if the camera has been registered
        // and set up a listener to handle its requests/commands.
        startPameraSocket();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // Pamera connection methods
    private void startPameraSocket() {
        // First check if the camera has been already registered
        if (!CredentialsUtils.isCameraRegistered(credentialsPref)) {
            Log.e(TAG, "Failed to start Pamera socket. The camera is not yet registered.");
            return;
        }

        // Instantiate the Socket io manager
        try {
            // Credentials are stored in a shared preferences after encrypting them.
            // The encryption key is managed by the Android key management framework: KeyStore.
            // Credentials must be retrieved from a sharedPreferences
            socketMgr = new SocketIoManagerBuilder(CredentialsUtils.getPanelUri(credentialsPref)).build();

            // The camera is successfully logged in/authenticated to the Panel.
            // --> Start listening to Panel requests
            panelConnectionRunnable = new PanelConnectionRunnable();
            panelConnectionWorkerThread = new Thread(panelConnectionRunnable);
            panelConnectionWorkerThread.start();
        } catch (Exception e) {
            Log.e(TAG, "Failed to start Pamera connection.", e);
        }
    }

    private void stopPameraSocket() {
        // First logout
        if (socketMgr != null) {
            socketMgr.logout();
            socketMgr = null;
        }
        // Stop properly panelConnectionWorkerThread.
        if (panelConnectionWorkerThread != null) {
            panelConnectionRunnable.terminate();
            try {
                panelConnectionWorkerThread.join();
            } catch (InterruptedException e) {
                Log.e(TAG, "Failed to stop panelConnectionWorkerThread.", e);
            } finally {
                panelConnectionWorkerThread = null;
            }
        }
    }

    // Called from native code register camera.
    public boolean registerCamera(String panel_uri, String camera_id, String camera_password, String room_id) {
        Log.i(TAG, "Registering camera." + "panel_uri: " + panel_uri + " camera_id: " + camera_id + " camera_password: " + camera_password + " room_id: " + room_id);

        try {
            // Stop the current Pamera socket session
            stopPameraSocket();
            socketMgr = new SocketIoManagerBuilder(panel_uri).build();
            socketMgr.register(new RegisterRequestContent(camera_id, camera_password, room_id));
            // Credentials are stored in a shared preferences after encrypting them.
            // The encryption key is managed by the Android key management framework: KeyStore.
            CredentialsUtils.storeCredentials(credentialsPref, panel_uri, camera_id, camera_password, room_id);
            // Restart a new Pamera socket session
            startPameraSocket();
        } catch (Exception e) {
            Log.e(TAG, "Failed to register camera. ", e);
            return false;
        }
        return true;
    }

    private class PanelConnectionRunnable implements Runnable {

        private volatile boolean looping = true;

        public void terminate() {
            looping = false;
        }

        @Override
        public void run() {
            while (looping) {
                try {
                    if (!socketMgr.isLoggedIn()) {
                        // Authenticate/log in using credentials
                        String camera_id = CredentialsUtils.getCameraId(credentialsPref);
                        String camera_password = CredentialsUtils.getCameraPassword(credentialsPref);
                        socketMgr.login(new LoginRequestContent(camera_id, camera_password));
                        socketMgr.handlePanelRequests();
                    }
                    Thread.sleep(10000);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to establish connection with the Panel.", e);
                }
            }
        }
    }
}