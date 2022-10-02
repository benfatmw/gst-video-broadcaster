package com.kalyzee.kontroller_services_api_implem.system.update.silent;

import static com.kalyzee.kontroller_services_api.dtos.system.update.ImageType.OS;
import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionState.WAITING_FOR_INSTALL;
import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionState.getStateText;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;


import com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionModel;
import com.kalyzee.kontroller_services_api.dtos.system.update.UpdateStateChangedEvent;
import com.kalyzee.kontroller_services_api.interfaces.system.update.IUpdateSessionManager;
import com.kalyzee.kontroller_services_api.interfaces.system.update.IUpdateStateListener;
import com.kalyzee.kontroller_services_api_implem.system.update.dao.UpdateSessionDao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SilentUpdateStateListener implements IUpdateStateListener {

    private static final String TAG = "SilentUpdtStateListener";
    private static final String IMAGE_TYPE_UPDATE_NOT_SUPPORTED = "Image type update is not supported.";
    private static final String UNSUPPORTED_DATE_FORMAT = "Invalid date format.";
    private static final String SCHEDULED_UPDATE_HAS_BEEN_CANCELLED = "Scheduled update has been cancelled. ";

    private final UpdateSessionDao updateSessionDao;
    private final IUpdateSessionManager updateSessionMgr;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private final HashMap<String, ScheduledFuture> scheduledFutures = new HashMap<String, ScheduledFuture>();

    public SilentUpdateStateListener(IUpdateSessionManager updateSessionMgr, UpdateSessionDao updateSessionDao) {
        this.updateSessionMgr = updateSessionMgr;
        this.updateSessionDao = updateSessionDao;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void stateChanged(String sessionId, UpdateStateChangedEvent event) {
        if (event.getNewState() == WAITING_FOR_INSTALL) {
            scheduleInstall(sessionId);
        }
        Log.i(TAG, "Update session: " + sessionId + " Update state transition: "
                + getStateText(event.getOldState()) + " --> " + getStateText(event.getNewState()));
    }

    @Override
    public void cancel(String sessionId) {
        UpdateSessionModel updateSession = updateSessionDao.getById(sessionId);
        if (scheduledFutures.containsKey(updateSession.getSessionId())) {
            scheduledFutures.get(sessionId).cancel(true);
            Log.i(TAG, SCHEDULED_UPDATE_HAS_BEEN_CANCELLED + updateSession.toString());
        }
    }

    @Override
    public void cancelAll() {
        for (String sessionId : scheduledFutures.keySet()) {
            cancel(sessionId);
        }
    }

    private boolean isValid(String dateStr) {
        DateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    private long getTimeInMs(String dateStr) {

        /** Check that the input date string contains the date in a valid format "HH:mm" */
        if (!isValid(dateStr)) {
            throw new IllegalArgumentException(UNSUPPORTED_DATE_FORMAT);
        }

        String[] time = dateStr.split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                Integer.parseInt(time[0].trim()), Integer.parseInt(time[1].trim()), 0);
        /**
         *  #getTimeInMillis returns the current time as UTC milliseconds.
         *  We must add the amount of time needed to be added to the UTC to get
         *  the standard time in this TimeZone.
         */
        return calendar.getTimeInMillis();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void scheduleInstall(String sessionId) {
        /**
         * Only OS update is currently supported
         */
        UpdateSessionModel silentUpdateSession = updateSessionDao.getById(sessionId);
        if (silentUpdateSession.getImageType() != OS) {
            throw new IllegalStateException(IMAGE_TYPE_UPDATE_NOT_SUPPORTED);
        }

        long startTimeInMs = getTimeInMs(silentUpdateSession.getStartTime());
        long endTimeInMs = getTimeInMs(silentUpdateSession.getEndTime());
        /**
         * If endTimeInMs < startTimeInMs, we suppose that the start time is
         * in the day before.
         */
        if (endTimeInMs <= startTimeInMs) {
            startTimeInMs -= TimeUnit.DAYS.toMillis(1);
        }

        long currentTimeInMs = System.currentTimeMillis();

        /**
         * If the current time is within the silent installation time window
         * --> launch the installation.
         */
        if ((currentTimeInMs >= startTimeInMs) && (currentTimeInMs <= endTimeInMs)) {
            updateSessionMgr.complete(sessionId);
            return;
        }

        /**
         * If the time window [startTime, endTime] is elapsed,
         * schedule the install in the day after.
         */
        if (currentTimeInMs > endTimeInMs) {
            startTimeInMs += TimeUnit.DAYS.toMillis(1);
            endTimeInMs += TimeUnit.DAYS.toMillis(1);
        }

        long delay = startTimeInMs - currentTimeInMs;
        UpdateInstallerTask updateInstallerTask = new UpdateInstallerTask(sessionId, updateSessionMgr);
        ScheduledFuture<?> scheduledFuture =
                scheduledExecutorService.schedule(updateInstallerTask, delay, TimeUnit.MILLISECONDS);
        scheduledFutures.put(sessionId, scheduledFuture);

        Log.i(TAG, silentUpdateSession.toString()
                + " is scheduled between: " + startTimeInMs + " and " + endTimeInMs
                + ", delay: " + String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(delay),
                TimeUnit.MILLISECONDS.toMinutes(delay) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(delay)),
                TimeUnit.MILLISECONDS.toSeconds(delay) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(delay))));
    }

    private class UpdateInstallerTask implements Runnable {

        public static final String TAG = "UpdateInstallerTask";
        public static final String FAILED_TO_APPLY_INSTALLATION = "Failed to apply installation";
        public static final String INSTALLING_SCHEDULED_UPDATE = "Performing scheduled update installation.";
        final private String sessionId;
        final private IUpdateSessionManager updateSessionMgr;

        public UpdateInstallerTask(String sessionId, IUpdateSessionManager updateSessionMgr) {
            this.sessionId = sessionId;
            this.updateSessionMgr = updateSessionMgr;
        }

        @Override
        public void run() {
            try {
                Log.i(TAG, INSTALLING_SCHEDULED_UPDATE + updateSessionMgr.getById(sessionId).toString());
                updateSessionMgr.complete(sessionId);
            } catch (Exception e) {
                Log.e(TAG, FAILED_TO_APPLY_INSTALLATION, e);
            }
        }
    }

}
