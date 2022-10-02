package com.kalyzee.kontroller_services_api_implem.system.update.download;


import static com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionState.IDLE;
import static com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionState.getStateText;

import android.util.Log;


import com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionModel;
import com.kalyzee.kontroller_services_api.interfaces.system.update.download.IDownloadSessionManager;
import com.kalyzee.kontroller_services_api.interfaces.system.update.download.IDownloadStateListener;
import com.kalyzee.kontroller_services_api.interfaces.system.update.download.IDownloadStatusCallback;
import com.kalyzee.kontroller_services_api_implem.system.update.dao.DownloadSessionDao;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.OkHttpClient;

public class DownloadSessionManager implements IDownloadSessionManager {

    private static final String TAG = "DownloadSessionManager";
    private static final String NULL_INPUT_PARAMETERS = "Null input parameters.";
    private static final String UNKNOWN_SESSION_ID = "Unknown session Id.";

    private final OkHttpClient httpClient;
    private final DownloadSessionDao downloadSessionDao;

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private final HashMap<String, Future> packageDownloaderFutures = new HashMap<String, Future>();
    private final HashMap<String, PackageDownloader> packageDownloaders = new HashMap<String, PackageDownloader>();

    public DownloadSessionManager(OkHttpClient httpClient, DownloadSessionDao downloadSessionDao) {
        this.httpClient = httpClient;
        this.downloadSessionDao = downloadSessionDao;
    }

    @Override
    public String create(DownloadSessionModel downloadSession) {
        /** Sanity checks */
        if (downloadSession == null) {
            throw new IllegalArgumentException(NULL_INPUT_PARAMETERS);
        }
        /** Generate a session id for the caller */
        String sessionId = UUID.randomUUID().toString();
        downloadSession.setSessionId(sessionId);
        downloadSession.setState(getStateText(IDLE));
        downloadSessionDao.insert(downloadSession);
        return sessionId;
    }

    @Override
    public void start(String sessionId, IDownloadStateListener downloadStateListener) {
        /** Sanity checks */
        if ((sessionId == null) || (downloadStateListener == null)) {
            throw new IllegalArgumentException(NULL_INPUT_PARAMETERS);
        }

        DownloadSessionModel downloadSession = get(sessionId);
        downloadSession.addStateListener(downloadStateListener);
        PackageDownloader packageDownloader = new PackageDownloader(httpClient,
                downloadSession);
        Future future = executorService.submit(packageDownloader);
        packageDownloaderFutures.put(sessionId, future);
        packageDownloaders.put(sessionId, packageDownloader);
    }

    @Override
    public void start(String sessionId,
                      IDownloadStateListener downloadStateListener,
                      IDownloadStatusCallback downloadStatusCallback) {
        /** Sanity checks */
        if (sessionId == null
                || downloadStateListener == null
                || downloadStatusCallback == null) {
            throw new IllegalArgumentException(NULL_INPUT_PARAMETERS);
        }
        DownloadSessionModel downloadSession = get(sessionId);
        downloadSession.addStateListener(downloadStateListener);
        PackageDownloader packageDownloader = new PackageDownloader(httpClient,
                downloadSession,
                downloadStatusCallback);
        Future future = executorService.submit(packageDownloader);
        packageDownloaderFutures.put(sessionId, future);
        packageDownloaders.put(sessionId, packageDownloader);
    }

    @Override
    public void cancel(String sessionId) {
        /** Sanity checks */
        if (sessionId == null) {
            throw new IllegalArgumentException(NULL_INPUT_PARAMETERS);
        }
        if (packageDownloaderFutures.containsKey(sessionId)) {
            /**
             * The ExecutorService does not know anything about what the Runnable is doing.
             * Cancelling the Future allows to cancel the thread from the point of view of
             * the Executor service.
             * We must also cancel the related Runnable and terminate the run() method.
             */
            packageDownloaders.get(sessionId).terminate();
            packageDownloaderFutures.get(sessionId).cancel(true);
        } else {
            Log.w(TAG, UNKNOWN_SESSION_ID);
        }

    }

    @Override
    public void destroy(String sessionId) {
        /** Sanity checks */
        if (sessionId == null) {
            throw new IllegalArgumentException(NULL_INPUT_PARAMETERS);
        }
        if (packageDownloaderFutures.containsKey(sessionId)) {
            downloadSessionDao.deleteById(sessionId);
        } else {
            Log.w(TAG, UNKNOWN_SESSION_ID);
        }
    }

    @Override
    public DownloadSessionModel get(String sessionId) {
        /** Sanity checks */
        if (sessionId == null) {
            throw new IllegalArgumentException(NULL_INPUT_PARAMETERS);
        }
        DownloadSessionModel downloadSession = downloadSessionDao.getById(sessionId);
        if (downloadSession == null) {
            throw new IllegalStateException(UNKNOWN_SESSION_ID);
        }
        return downloadSession;
    }
}
