package com.kalyzee.kontroller_services_api_implem.system.update;


import static com.kalyzee.kontroller_services_api.dtos.system.update.ImageType.OS;
import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateMode.MANDATORY;
import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateMode.SILENT;
import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionState.ABORTED;
import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionState.DOWNLOADING;
import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionState.IDLE;
import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionState.INSTALLING;
import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionState.WAITING_FOR_INSTALL;
import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionState.getStateInt;
import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionState.getStateText;

import android.util.Log;

import com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionModel;
import com.kalyzee.kontroller_services_api.dtos.system.update.UpdateStateChangedEvent;
import com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionModel;
import com.kalyzee.kontroller_services_api.interfaces.system.SystemManager;
import com.kalyzee.kontroller_services_api.interfaces.system.update.IUpdateSessionManager;
import com.kalyzee.kontroller_services_api.interfaces.system.update.IUpdateStateListener;
import com.kalyzee.kontroller_services_api.interfaces.system.update.download.IDownloadSessionManager;
import com.kalyzee.kontroller_services_api.interfaces.system.update.install.IUpdateInstaller;
import com.kalyzee.kontroller_services_api_implem.system.update.dao.UpdateSessionDao;
import com.kalyzee.kontroller_services_api_implem.system.update.download.DownloadStateListener;
import com.kalyzee.kontroller_services_api_implem.system.update.download.DownloadStatusCallback;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.UUID;

import io.socket.client.Socket;

public class UpdateSessionManager implements IUpdateSessionManager {

    private static final String TAG = "UpdateSessionManager";
    private static final String NULL_INPUT_PARAMETERS = "Null input parameters.";
    private static final String UNKNOWN_SESSION_ID = "Unknown update session Id.";
    private static final String UNKNOWN_UPDATE_MODE = "Unknown update mode.";
    private static final String UNSUPPORTED_IMAGE_TYPE = "Unsupported image type.";
    private static final String OS_VERSION_ALREADY_INSTALLED = "OS version already installed.";
    private static final String NOT_ALLOWED_IN_CURRENT_STATE = "Not allowed in current state. Current state:";
    private static final String FAILED_TO_CREATE_RANDOM_FILE = "Failed to create a random file.";

    private static final String OS_UPDATE_PATH = "/data/data/com.kalyzee.kontroller/update.zip";

    private static final long OS_UPDATE_PACKAGE_RESERVED_SPACE = 500 * 1024 * 1024;

    private static final String UPDATE_SESSION_CREATED = "Update session created. ";
    private static final String UPDATE_SESSION_STARTED = "Update session started. ";
    private static final String UPDATE_SESSION_CANCELLED = "Update session cancelled. ";
    private static final String UPDATE_SESSION_COMPLETED = "Update session completed. ";
    private static final String UPDATE_SESSION_DESTROYED = "Update session destroyed. ";
    private static final String UPDATE_SESSION_RESUMED = "Update session resumed. ";
    private static final String NOT_ALLOWED_ACTION = "Not allowed action: attempting to resume an aborted session. ";

    private final IDownloadSessionManager downloadSessionManager;
    private final IUpdateInstaller updateInstaller;
    private final SystemManager systemManager;
    private final Socket socket;
    private final UpdateSessionDao updateSessionDao;

    static {
        /**
         * If there is no downloaded/empty images in cache flash partition,
         * Update manager creates an empty image in order to reserve space
         * for OS system image download.
         * These empty image is replaced by the downloaded images
         * when a OS download session is started.
         */
        createRandomFileIfNotExists(OS_UPDATE_PATH, OS_UPDATE_PACKAGE_RESERVED_SPACE);
    }

    public UpdateSessionManager(IDownloadSessionManager downloadSessionManager,
                                SystemManager systemManager,
                                Socket socket,
                                UpdateSessionDao updateSessionDao,
                                IUpdateInstaller updateInstaller) {
        this.downloadSessionManager = downloadSessionManager;
        this.systemManager = systemManager;
        this.socket = socket;
        this.updateSessionDao = updateSessionDao;
        this.updateInstaller = updateInstaller;
    }

    @Override
    public String create(UpdateSessionModel updateSession,
                         DownloadSessionModel downloadSession) {
        /** Sanity checks */
        if (updateSession == null || downloadSession == null) {
            throw new IllegalArgumentException(NULL_INPUT_PARAMETERS);
        }

        /**
         * Current update manager supports only OS update.
         * @todo APK update will be implemented in future versions
         */
        if (updateSession.getImageType() != OS) {
            throw new IllegalArgumentException(UNSUPPORTED_IMAGE_TYPE);
        }

        /**
         * Os version code is an internal version number used only to determine whether
         * a version is more recent than another.
         * A higher number indicates a more recent version.
         */
        int currentOsVersionCode = systemManager.getSystemInfo().getOsVersionCode();
        int targetingOsVersionCode = updateSession.getVersionCode();
        Log.i(TAG, "Current OS version code: " + currentOsVersionCode + ", " +
                "targeting OS version code: " + targetingOsVersionCode);
        if (targetingOsVersionCode == currentOsVersionCode) {
            throw new IllegalArgumentException(OS_VERSION_ALREADY_INSTALLED);
        }

        /** Generate a update session id for the caller */
        String downloadSessionId = downloadSessionManager.create(downloadSession);
        String sessionId = UUID.randomUUID().toString();
        updateSession.setSessionId(sessionId);
        /** Set download session id attribute in #updateSessionCreateSpec */
        updateSession.setDownloadSessionId(downloadSessionId);
        updateSession.setState(getStateText(IDLE));
        updateSessionDao.insert(updateSession);
        Log.i(TAG, UPDATE_SESSION_CREATED + updateSession.toString());
        return sessionId;
    }

    @Override
    public void start(String sessionId) {
        /** Sanity checks */
        if (sessionId == null) {
            throw new IllegalArgumentException(NULL_INPUT_PARAMETERS);
        }
        UpdateSessionModel updateSession = getById(sessionId);
        startUpdateSession(updateSession);
    }

    @Override
    public void start(String sessionId,
                      IUpdateStateListener updateStateListener) {
        /** Sanity checks */
        if (sessionId == null || updateStateListener == null) {
            throw new IllegalArgumentException(NULL_INPUT_PARAMETERS);
        }
        UpdateSessionModel updateSession = getById(sessionId);
        /** Set update state changes listener */
        updateSession.addStateListener(updateStateListener);
        startUpdateSession(updateSession);
    }

    private void startUpdateSession(UpdateSessionModel updateSession) {
        /**
         * Starts a download session based on the update mode .
         * In case of mandatory update, #DownloadStatusCallback is used to send status notifications
         * (success, progress, error)
         */
        if (updateSession.getUpdateMode() == MANDATORY) {
            downloadSessionManager.start(updateSession.getDownloadSessionId(),
                    new DownloadStateListener(updateSession, updateSessionDao),
                    new DownloadStatusCallback(socket, updateSession.getSessionId()));
        } else if (updateSession.getUpdateMode() == SILENT) {
            downloadSessionManager.start(updateSession.getDownloadSessionId(),
                    new DownloadStateListener(updateSession, updateSessionDao));
        } else {
            throw new IllegalArgumentException(UNKNOWN_UPDATE_MODE);
        }
        Log.i(TAG, UPDATE_SESSION_STARTED + updateSession.toString());
    }

    @Override
    public void complete(String sessionId) {
        /** Sanity checks */
        if (sessionId == null) {
            throw new IllegalArgumentException(NULL_INPUT_PARAMETERS);
        }
        UpdateSessionModel updateSession = getById(sessionId);
        /** Ensure that the update session is in #WAITING_FOR_INSTALL state */
        if (getStateInt(updateSession.getState()) != WAITING_FOR_INSTALL) {
            throw new IllegalStateException(NOT_ALLOWED_IN_CURRENT_STATE + updateSession.getState());
        }
        if (updateSession.getImageType() == OS) {
            DownloadSessionModel downloadSession =
                    downloadSessionManager.get(updateSession.getDownloadSessionId());
            updateSession.setState(getStateText(INSTALLING));
            updateInstaller.installOsUpdate(downloadSession.getFileLocation());
        } else {
            throw new IllegalArgumentException(UNSUPPORTED_IMAGE_TYPE);
        }
        Log.i(TAG, UPDATE_SESSION_COMPLETED + updateSession.toString());
    }

    public void resume(String sessionId,
                       IUpdateStateListener updateStateListener) {
        /** Sanity checks */
        if (sessionId == null || updateStateListener == null) {
            throw new IllegalArgumentException(NULL_INPUT_PARAMETERS);
        }
        UpdateSessionModel updateSession = updateSessionDao.getById(sessionId);
        int currentState = getStateInt(updateSession.getState());
        if (currentState == ABORTED || currentState == INSTALLING) {
            Log.w (TAG, NOT_ALLOWED_ACTION + updateSession.toString());
            return;
        } else if (currentState == WAITING_FOR_INSTALL) {
            updateStateListener.stateChanged(sessionId, new UpdateStateChangedEvent(IDLE, WAITING_FOR_INSTALL));
        } else {
            /**
             * Cancel the ongoing session before restarting it
             * @todo implement download resuming mechanism
             */
            cancel(sessionId);
            start(sessionId, updateStateListener);
        }
        Log.i(TAG, UPDATE_SESSION_RESUMED + updateSession.toString());
    }

    @Override
    public void cancel(String sessionId) {
        /** Sanity checks */
        if (sessionId == null) {
            throw new IllegalArgumentException(NULL_INPUT_PARAMETERS);
        }
        UpdateSessionModel updateSession = getById(sessionId);
        if (getStateInt(updateSession.getState()) == DOWNLOADING) {
            downloadSessionManager.cancel(updateSession.getDownloadSessionId());
        }
        updateSessionDao.updateState(getStateText(ABORTED), updateSession.getSessionId());
        Log.i(TAG, UPDATE_SESSION_CANCELLED + updateSession.toString());
    }

    @Override
    public void destroy(String sessionId) {
        /** Sanity checks */
        if (sessionId == null) {
            throw new IllegalArgumentException(NULL_INPUT_PARAMETERS);
        }
        UpdateSessionModel updateSession = getById(sessionId);
        downloadSessionManager.destroy(updateSession.getDownloadSessionId());
        updateSessionDao.deleteById(sessionId);
        Log.i(TAG, UPDATE_SESSION_DESTROYED + updateSession.toString());
    }

    @Override
    public UpdateSessionModel getById(String sessionId) {
        /** Sanity checks */
        if (sessionId == null) {
            throw new IllegalArgumentException(NULL_INPUT_PARAMETERS);
        }
        UpdateSessionModel updateSession = updateSessionDao.getById(sessionId);
        if (updateSession == null) {
            throw new IllegalArgumentException(UNKNOWN_SESSION_ID);
        }
        return updateSession;
    }

    public List<UpdateSessionModel> getAll() {
        return updateSessionDao.getAll();
    }

    private static void createRandomFileIfNotExists(String fileName, long size) {
        File f = new File(fileName);
        if (f.exists()) {
            Log.i(TAG, "File " + fileName + " already exists.");
            return;
        }
        try (RandomAccessFile rafile = new RandomAccessFile(fileName, "rw")) {
            rafile.setLength(size);
        } catch (Exception e) {
            Log.e(TAG, FAILED_TO_CREATE_RANDOM_FILE, e);
        }
    }
}

