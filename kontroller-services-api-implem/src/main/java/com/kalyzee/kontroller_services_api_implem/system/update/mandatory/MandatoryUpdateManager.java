package com.kalyzee.kontroller_services_api_implem.system.update.mandatory;


import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateMode.MANDATORY;
import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionState.DOWNLOADING;
import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionState.getStateInt;

import android.util.Log;

import com.kalyzee.kontroller_services_api.dtos.system.update.UpdateDescriptor;
import com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionModel;
import com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionModel;
import com.kalyzee.kontroller_services_api.exceptions.system.update.mandatory.CancelMandatoryUpdateFailureException;
import com.kalyzee.kontroller_services_api.exceptions.system.update.mandatory.CompleteMandatoryUpdateFailureException;
import com.kalyzee.kontroller_services_api.exceptions.system.update.mandatory.StartMandatoryUpdateFailureException;
import com.kalyzee.kontroller_services_api.interfaces.system.update.IUpdateSessionManager;
import com.kalyzee.kontroller_services_api.interfaces.system.update.mandatory.IMandatoryUpdateManager;

import java.util.Iterator;
import java.util.List;

public class MandatoryUpdateManager implements IMandatoryUpdateManager {

    private static final String TAG = "MandatoryUpdateManager";
    private static final String FAILED_TO_START_MANDATORY_UPDATE = "Failed to start a mandatory update sessions.";
    private static final String FAILED_TO_CANCEL_MANDATORY_UPDATE = "Failed to cancel a mandatory update sessions.";
    private static final String FAILED_TO_COMPLETE_MANDATORY_UPDATE = "Failed to complete a mandatory update sessions.";
    private static final String NULL_INPUT_PARAMETER = "Null input parameter.";
    private static final String INVALID_UPDATE_MODE = "Invalid update mode.";

    private final IUpdateSessionManager updateSessionMgr;

    public MandatoryUpdateManager(IUpdateSessionManager updateSessionMgr) {
        this.updateSessionMgr = updateSessionMgr;
        new Thread(new Runnable() {
            @Override
            public void run() {
                abortInterruptedSessions();
            }
        }).start();
    }

    @Override
    public String start(UpdateDescriptor updateDescriptor) throws StartMandatoryUpdateFailureException {
        try {
            String sessionId = updateSessionMgr.create(
                    new UpdateSessionModel(MANDATORY,
                            updateDescriptor.getImageType(),
                            updateDescriptor.getVersionCode()),
                    new DownloadSessionModel(updateDescriptor.getUrl(),
                            updateDescriptor.getSha256Fingerprint(),
                            "/data/data/com.kalyzee.kontroller/",
                            "update.zip"));
            updateSessionMgr.start(sessionId);
            return sessionId;
        } catch (Exception e) {
            throw new StartMandatoryUpdateFailureException(FAILED_TO_START_MANDATORY_UPDATE, e);
        }
    }

    @Override
    public void cancel(String sessionId) throws CancelMandatoryUpdateFailureException {

        /** Sanity checks */
        if (sessionId == null) {
            throw new CancelMandatoryUpdateFailureException(NULL_INPUT_PARAMETER);
        }
        if (updateSessionMgr.getById(sessionId).getUpdateMode() != MANDATORY) {
            throw new CancelMandatoryUpdateFailureException(INVALID_UPDATE_MODE);
        }

        try {
            updateSessionMgr.cancel(sessionId);
        } catch (Exception e) {
            throw new CancelMandatoryUpdateFailureException(FAILED_TO_CANCEL_MANDATORY_UPDATE, e);
        }
    }

    @Override
    public void complete(String sessionId) throws CompleteMandatoryUpdateFailureException {

        /** Sanity checks */
        if (sessionId == null) {
            throw new CompleteMandatoryUpdateFailureException(NULL_INPUT_PARAMETER);
        }
        if (updateSessionMgr.getById(sessionId).getUpdateMode() != MANDATORY) {
            throw new CompleteMandatoryUpdateFailureException(INVALID_UPDATE_MODE);
        }

        try {
            updateSessionMgr.complete(sessionId);
        } catch (Exception e) {
            throw new CompleteMandatoryUpdateFailureException(FAILED_TO_COMPLETE_MANDATORY_UPDATE, e);
        }
    }

    /**
     * Iterate over update sessions database entries
     * to reschedule all interrupted update sessions
     */
    private void abortInterruptedSessions() {
        List<UpdateSessionModel> updateSessions = updateSessionMgr.getAll();
        Iterator itr = updateSessions.iterator();
        while (itr.hasNext()) {
            UpdateSessionModel updateSession = (UpdateSessionModel) itr.next();
            if (updateSession.getUpdateMode() == MANDATORY) {
                /**
                 * The mandatory update sessions that were interrupted
                 * while downloading are cancelled then destroyed.
                 */
                if (getStateInt(updateSession.getState()) == DOWNLOADING) {
                    updateSessionMgr.cancel(updateSession.getSessionId());
                    updateSessionMgr.destroy(updateSession.getSessionId());
                }
            }
        }
    }
}
