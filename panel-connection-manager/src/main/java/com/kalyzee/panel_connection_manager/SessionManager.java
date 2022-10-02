package com.kalyzee.panel_connection_manager;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SessionManager {

    private static final String TAG = "SessionManager";
    private static final String FAILED_TO_START_PANEL_SESSION = "Failed to start panel session.";
    private static final String FAILED_TO_STOP_PANEL_SESSION = "Failed to stop panel session.";

    private final Session session;
    private final CredentialsManager credentialsManager;
    private PanelConnectionTask panelConnectionTask;

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private Future future;

    public SessionManager(Session session, CredentialsManager credentialsManager) {
        this.session = session;
        this.credentialsManager = credentialsManager;
    }

    public void startSession(String cameraId) {
        try {
            panelConnectionTask = new PanelConnectionTask(cameraId, session, credentialsManager);
            future = executorService.submit(panelConnectionTask);
        } catch (Exception e) {
            Log.e(TAG, FAILED_TO_START_PANEL_SESSION, e);
        }
    }

    public void stopSession() {
        try {
            /** First logout */
            if (session.isLoggedIn()) {
                session.logout();
            }
            /** Stop properly panelConnectionRunnable. */
            if (panelConnectionTask != null) {
                panelConnectionTask.terminate();
            }
            if (future != null) {
                future.cancel(true);
            }
        } catch (Exception e) {
            Log.e(TAG, FAILED_TO_STOP_PANEL_SESSION, e);
        }
    }

}
