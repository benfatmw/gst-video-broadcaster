package com.kalyzee.kontroller;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kalyzee.kontroller.registration.LocalWebsocketServer;
import com.kalyzee.panel_connection_manager.CredentialsManager;
import com.kalyzee.panel_connection_manager.ICredentialsUpdatedListener;
import com.kalyzee.panel_connection_manager.SessionManager;
import com.kalyzee.panel_connection_manager.mappers.session.ILoginStatusListener;
import com.kalyzee.visca_over_ip.ViscaCamera;
import com.kalyzee.visca_over_ip.ViscaSpecification;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "MainActivity";

    private static final String FAILED_TO_LOGIN_AFTER_REGISTRATION = "Failed to login after credentials update.";
    private static final String FAILED_TO_SETUP_PANEL_CONNECTION = "Failed to create and set up panel connection.";
    private static final String FAILED_TO_START_SESSION = "Failed to start session.";
    private static final String FAILED_TO_STOP_SESSION = "Failed to stop session.";

    private static final String CREDENTIALS_PREFERENCE = "credentials";

    private SessionManager sessionManager;
    private CredentialsManager credentialsManager;
    private LocalWebsocketServer wsServer;
    private String macAddress;
    private String[] protocolSpecs = {"VISCA-SPEC-A", "VISCA-SPEC-B"};

    private Spinner protocolSpecSpin;
    private EditText rtspUriEditText;
    private EditText iPEditText;
    private EditText visaPortEditText;
    private Button loginButton;
    private Button logoutButton;
    private TextView statusMessageTextView;
    private ProgressBar progressBar;
    private ViscaSpecification selectedViscaSpec;

    private ILoginStatusListener loginStatusListener = new LoginStatusListener();
    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    public class LoginStatusListener implements ILoginStatusListener {

        @Override
        public void onLoginResult(boolean status) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (status == true) {
                        loginButton.setEnabled(false);
                        logoutButton.setEnabled(true);
                        rtspUriEditText.setEnabled(false);
                        protocolSpecSpin.setEnabled(true);
                        iPEditText.setEnabled(false);
                        visaPortEditText.setEnabled(false);
                        statusMessageTextView.setTextColor(GREEN);
                        statusMessageTextView.setText(R.string.online_status);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.login_failed, Toast.LENGTH_LONG).show();;
                    }
                }
            });

        }

        @Override
        public void onLogoutResult(boolean status) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (status) {
                        loginButton.setEnabled(true);
                        logoutButton.setEnabled(false);
                        rtspUriEditText.setEnabled(true);
                        protocolSpecSpin.setEnabled(true);
                        iPEditText.setEnabled(true);
                        visaPortEditText.setEnabled(true);
                        statusMessageTextView.setTextColor(RED);
                        statusMessageTextView.setText(R.string.offline_status);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.logout_failed, Toast.LENGTH_LONG).show();;
                    }
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * Take the instance of Spinner and
         * apply OnItemSelectedListener on it which
         * tells which item of spinner is clicked
         * */
        protocolSpecSpin = findViewById(R.id.protocol_spin);
        protocolSpecSpin.setOnItemSelectedListener(this);
        /**
         * Create the instance of ArrayAdapter
         * having the list of courses
         */
        ArrayAdapter ad = new ArrayAdapter(this, R.layout.spinner_list, protocolSpecs);
        /**
         * Set simple layout resource file
         * for each item of spinner
         */
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        /**
         * Set the ArrayAdapter (ad) data on the
         * Spinner which binds data to spinner
         */
        protocolSpecSpin.setAdapter(ad);
        rtspUriEditText = findViewById(R.id.uri);
        iPEditText = findViewById(R.id.ip);
        visaPortEditText = findViewById(R.id.visca_port);
        statusMessageTextView = findViewById(R.id.status_message);
        statusMessageTextView.setTextColor(RED);
        statusMessageTextView.setText(R.string.offline_status);
        progressBar = findViewById(R.id.progress_spinner);
        progressBar.setVisibility(View.INVISIBLE);

        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                executorService.execute(new Runnable() {
                    public void run() {
                        try {
                            sessionManager = new SessionManagerBuilder(credentialsManager,
                                    loginStatusListener,
                                    getApplicationContext(),
                                    iPEditText.getText().toString(),
                                    Integer.parseInt(visaPortEditText.getText().toString()),
                                    rtspUriEditText.getText().toString()).build();
                            sessionManager.startSession(macAddress);

                        } catch (Exception e) {
                            loginStatusListener.onLoginResult(false);
                            Log.e(TAG, FAILED_TO_START_SESSION, e);
                        }
                    }
                });
            }
        });

        logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                executorService.execute(new Runnable() {
                    public void run() {
                        try {
                            sessionManager.stopSession();
                        } catch (Exception e) {
                            Log.e(TAG, FAILED_TO_STOP_SESSION, e);
                        }
                    }
                });
            }
        });

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            /**
             * here to request the missing permissions, and then overriding
             *  public void onRequestPermissionsResult(int requestCode, String[] permissions,
             *                                          int[] grantResults)
             * to handle the case where the user grants the permission. See the documentation
             * for ActivityCompat#requestPermissions for more details.
             */
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loginButton.setEnabled(true);
                                logoutButton.setEnabled(false);
                                rtspUriEditText.setEnabled(true);
                                protocolSpecSpin.setEnabled(true);
                                iPEditText.setEnabled(true);
                                Toast.makeText(getApplicationContext(),
                                        "Device has been registered successfully.",
                                        Toast.LENGTH_LONG).show();

                            }
                        });

                    } catch (Exception e) {
                        Log.e(TAG, FAILED_TO_LOGIN_AFTER_REGISTRATION, e);
                    }
                }
            });

            if (!credentialsManager.isRegistered()) {
                loginButton.setEnabled(false);
                logoutButton.setEnabled(false);
                rtspUriEditText.setEnabled(false);
                protocolSpecSpin.setEnabled(false);
                iPEditText.setEnabled(false);
                visaPortEditText.setEnabled(false);
                statusMessageTextView.setTextColor(RED);
                statusMessageTextView.setText(R.string.device_not_registered);
                wsServer = new LocalWebsocketServer(Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()),
                        macAddress,
                        credentialsManager);
                wsServer.start();
            } else {
                logoutButton.setEnabled(false);
            }
        } catch (Exception e) {
            Log.e(TAG, FAILED_TO_SETUP_PANEL_CONNECTION, e);
        }
    }

    protected void onDestroy() {
        sessionManager.stopSession();
        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.i(TAG, "Selected visca specification: " + protocolSpecs[i]);
        ViscaCamera.currentSpec = ViscaSpecification.value(protocolSpecs[i]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}