package com.kalyzee.kontroller_services_api_implem.system.update.download;


import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionState.ABORTED;
import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionState.DOWNLOADING;
import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionState.ERROR;
import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionState.IDLE;
import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionState.WAITING_FOR_INSTALL;

import android.util.Log;

import com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionModel;
import com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionState;
import com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionState;
import com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadStateChangedEvent;
import com.kalyzee.kontroller_services_api.interfaces.system.update.download.IDownloadStateListener;
import com.kalyzee.kontroller_services_api_implem.system.update.dao.UpdateSessionDao;

public class DownloadStateListener implements IDownloadStateListener {

    private static final String TAG = "DownloadStateListener";
    private static final String INVALID_DOWNLOAD_SESSION_STATE = "This block should not be reached. " +
            "Invalid Download session state!";

    private final UpdateSessionModel updateSession;
    private final UpdateSessionDao updateSessionDao;

    public DownloadStateListener(UpdateSessionModel updateSession,
                                 UpdateSessionDao updateSessionDao) {
        this.updateSession = updateSession;
        this.updateSessionDao = updateSessionDao;
    }

    @Override
    public void stateChanged(DownloadStateChangedEvent event) {
        int updateState;
        switch (event.getNewState()) {
            case DownloadSessionState.IDLE:
                updateState = IDLE;
                break;
            case DownloadSessionState.ERROR:
                updateState = ERROR;
                break;
            case DownloadSessionState.RUNNING:
                updateState = DOWNLOADING;
                break;
            case DownloadSessionState.FINISHED:
                updateState = WAITING_FOR_INSTALL;
                break;
            case DownloadSessionState.ABORTED:
                updateState = ABORTED;
                break;
            default:
                throw new IllegalStateException(INVALID_DOWNLOAD_SESSION_STATE);
        }
        updateSessionDao.updateState(UpdateSessionState.getStateText(updateState),
                updateSession.getSessionId());
        updateSession.setState(UpdateSessionState.getStateText(updateState));
        Log.d(TAG, "Download state transition: "
                + DownloadSessionState.getStateText(event.getOldState())
                + " --> " + DownloadSessionState.getStateText(event.getNewState()));
    }
}
