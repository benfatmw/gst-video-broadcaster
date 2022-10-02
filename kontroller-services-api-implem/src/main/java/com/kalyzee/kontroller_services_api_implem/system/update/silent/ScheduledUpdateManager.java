package com.kalyzee.kontroller_services_api_implem.system.update.silent;


import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateMode.SILENT;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionModel;
import com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionModel;
import com.kalyzee.kontroller_services_api.dtos.system.update.silent.SilentUpdateDescriptor;
import com.kalyzee.kontroller_services_api.exceptions.system.update.mandatory.CancelMandatoryUpdateFailureException;
import com.kalyzee.kontroller_services_api.exceptions.system.update.silent.CancelScheduledUpdateException;
import com.kalyzee.kontroller_services_api.exceptions.system.update.silent.ScheduleSilentUpdateFailureException;
import com.kalyzee.kontroller_services_api.interfaces.network.INetworkCallback;
import com.kalyzee.kontroller_services_api.interfaces.system.update.IUpdateSessionManager;
import com.kalyzee.kontroller_services_api.interfaces.system.update.IUpdateStateListener;
import com.kalyzee.kontroller_services_api.interfaces.system.update.silent.IScheduledUpdateManager;
import com.kalyzee.kontroller_services_api_implem.network.NetworkCallback;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ScheduledUpdateManager implements IScheduledUpdateManager {

    private static final String TAG = "ScheduledUpdateManager";
    private static final String FAILED_TO_SCHEDULE_SILENT_UPDATE = "Failed to start a silent update session.";
    private static final String FAILED_TO_CANCEL_SILENT_UPDATE = "Failed to cancel a silent update session.";
    private static final String NULL_INPUT_PARAMETER = "Null input parameter.";
    private static final String INVALID_UPDATE_MODE = "Invalid update mode.";
    private static final String ATTEMPTING_TO_RESUME_INTERRUPTED_SILENT_SESSIONS
            = "---------------- Attempting to resume/reschedule interrupted silent sessions: ";

    private final IUpdateSessionManager updateSessionMgr;
    private final Context context;
    private NetworkCallback NetworkCallback;
    private final IUpdateStateListener silentUpdateStateListener;
    private TimeSettingsChangedReceiver timeSettingsChangedEventReceiver;
    private NetworkSettingsChangedCallback networkSettingsChangedCallback;
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    public ScheduledUpdateManager(IUpdateSessionManager updateSessionMgr,
                                  IUpdateStateListener silentUpdateStateListener,
                                  Context context) {
        this.updateSessionMgr = updateSessionMgr;
        this.context = context;
        this.silentUpdateStateListener = silentUpdateStateListener;
        new Thread(new Runnable() {
            @Override
            public void run() {
                initUpdateScheduler();
            }
        }).start();
    }

    @Override
    public String schedule(SilentUpdateDescriptor updateDescriptor) throws ScheduleSilentUpdateFailureException {
        try {
            String sessionId = updateSessionMgr.create(new UpdateSessionModel(SILENT,
                            updateDescriptor.getImageType(),
                            updateDescriptor.getVersionCode(),
                            updateDescriptor.getStartTime(),
                            updateDescriptor.getEndTime()),
                    new DownloadSessionModel(updateDescriptor.getUrl(),
                            updateDescriptor.getSha256Fingerprint(),
                            "/data/data/com.kalyzee.kontroller/",
                            "update.zip"));
            updateSessionMgr.start(sessionId, silentUpdateStateListener);
            return sessionId;
        } catch (Exception e) {
            throw new ScheduleSilentUpdateFailureException(FAILED_TO_SCHEDULE_SILENT_UPDATE, e);
        }
    }

    @Override
    public void cancel(String sessionId) throws CancelScheduledUpdateException {

        /** Sanity checks */
        if (sessionId == null) {
            throw new CancelMandatoryUpdateFailureException(NULL_INPUT_PARAMETER);
        }
        if (updateSessionMgr.getById(sessionId).getUpdateMode() != SILENT) {
            throw new CancelMandatoryUpdateFailureException(INVALID_UPDATE_MODE);
        }
        try {
            silentUpdateStateListener.cancel(sessionId);
            updateSessionMgr.cancel(sessionId);
        } catch (Exception e) {
            throw new CancelScheduledUpdateException(FAILED_TO_CANCEL_SILENT_UPDATE, e);
        }
    }

    /**
     * Init silent update scheduler:
     * Set up time settings changes event receiver + schedule interrupted/unfinished sessions
     */
    private void initUpdateScheduler() {
        /**
         * Iterate over update sessions database entries
         * to schedule all interrupted/unfinished update sessions
         * + register time/network settings changes receiver.
         */
        handleInterruptedSessions();
        registerTimeSettingsChangedEventReceiver();
        registerNetworkSettingsChangedEventReceiver();
    }

    /**
     * Iterate over update sessions database entries
     * to reschedule/resume all interrupted update sessions
     */
    private void handleInterruptedSessions() {
        List<UpdateSessionModel> updateSessions = updateSessionMgr.getAll();
        Iterator itr = updateSessions.iterator();
        while (itr.hasNext()) {
            UpdateSessionModel updateSession = (UpdateSessionModel) itr.next();
            if (updateSession.getUpdateMode() == SILENT) {
                Log.i(TAG, ATTEMPTING_TO_RESUME_INTERRUPTED_SILENT_SESSIONS
                        + updateSession.getSessionId());
                updateSessionMgr.resume(updateSession.getSessionId(), silentUpdateStateListener);
            }
        }
    }

    /**
     * Interrupt the ongoing silent/scheduled update sessions
     */
    private void interruptScheduledSessions() {
        silentUpdateStateListener.cancelAll();
    }

    private void registerTimeSettingsChangedEventReceiver() {
        timeSettingsChangedEventReceiver = new TimeSettingsChangedReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        context.registerReceiver(timeSettingsChangedEventReceiver, intentFilter);
    }

    private void registerNetworkSettingsChangedEventReceiver() {
        networkSettingsChangedCallback = new NetworkSettingsChangedCallback();
        NetworkCallback.registerListener(networkSettingsChangedCallback);
    }


    private class TimeSettingsChangedReceiver extends BroadcastReceiver {

        public static final String TAG = "TimeSettingsChangedRecv";
        public static final String SYSTEM_TIME_SETTINGS_HAVE_CHANGED =
                "System time settings have changed. Received intent action: ";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_CHANGED)
                    || action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                Log.i(TAG, SYSTEM_TIME_SETTINGS_HAVE_CHANGED + action);
            }

            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    /**
                     * Silent update sessions scheduler is independent of system time changes
                     * as it relies on relative delays.
                     * When system time settings change, all scheduled update sessions must be rescheduled
                     * (ongoing scheduled sessions must be interrupted and rescheduled).
                     */
                    interruptScheduledSessions();
                    handleInterruptedSessions();
                }
            });
        }
    }

    private class NetworkSettingsChangedCallback implements INetworkCallback {

        public static final String TAG = "NetworkSettingsChanged";
        public static final String NETWORK_SETTINGS_HAVE_CHANGED =
                "Network settings have changed --> Handle interrupted update sessions.";

        @Override
        public void onChanged() {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, NETWORK_SETTINGS_HAVE_CHANGED);
                    handleInterruptedSessions();
                }
            });
        }
    }
}

