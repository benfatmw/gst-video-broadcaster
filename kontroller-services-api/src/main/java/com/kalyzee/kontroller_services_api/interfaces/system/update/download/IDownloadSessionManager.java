package com.kalyzee.kontroller_services_api.interfaces.system.update.download;


import com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionModel;

public interface IDownloadSessionManager {

    /**
     * Creates a new download session. A download session is used to retrieve/download a binary.
     *
     * @param downloadSession provides information on a download session resource..
     * @return Identifier of the new update session being created.
     */
    String create(DownloadSessionModel downloadSession);

    /**
     * Creates a new download session. A download session is used to retrieve/download a binary.
     *
     * @param sessionId                     Download session identifier.
     * @param downloadStateListener         Receives the session state of the operation invocation.
     *                                      Called when the listened-to download session changes state.
     * @param downloadStatusChangedCallback Receives the session status (success, progress, error)
     *                                      of the operation invocation.
     * @return Identifier of the new update session being created.
     */
    void start(String sessionId,
               IDownloadStateListener downloadStateListener,
               IDownloadStatusCallback downloadStatusChangedCallback);

    /**
     * Starts a new download session. A download session is used to retrieve/download a binary.
     *
     * @param sessionId             Download session identifier.
     * @param downloadStateListener Receives the session state changes of the operation invocation.
     * @return Identifier of the new update session being created.
     */
    void start(String sessionId,
               IDownloadStateListener downloadStateListener);

    /**
     * cancels/aborts a download session with the specified identifier.
     *
     * @param sessionId Identifier of the download session.
     */
    void cancel(String sessionId);

    /**
     * Destroys a download session with the specified identifier.
     *
     * @param sessionId Identifier of the download session.
     */
    void destroy(String sessionId);

    /**
     * Gets the download session with the specified identifier, including the most up-to-date status
     * information for the session.
     *
     * @param sessionId Identifier of the download sessio .
     * @return update session attributes.
     */
    DownloadSessionModel get(String sessionId);

}
