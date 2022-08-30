package com.kalyzee.kontroller_services_api.interfaces.system.update.download;


import com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionModel;

public interface IDownloadSessionManager {
    /**
     * Creates a new download session. A download session is used to retrieve/download a binary.
     *
     * @param downloadSessionCreateSpec Specification for the download session to be created.
     * @param downloadStateChangeAsyncCallback Receives the session state of the operation invocation.
     * @return Identifier of the new update session being created.
     */
    String create(DownloadSessionModel downloadSessionCreateSpec,
                  IDownloadStateChangeHandler downloadStateChangeAsyncCallback);

    /**
     * Creates a new download session. A download session is used to retrieve/download a binary.
     *
     * @param downloadSessionCreateSpec Specification downloadStatusChangeAsyncCallbackfor the download session to be created.
     * @param downloadStateChangeAsyncCallback Receives the session state of the operation invocation.
     * @param downloadStatusChangeAsyncCallback Receives the session status (success, progress, error) of the operation invocation.
     * @return Identifier of the new update session being created.
     */
    String create(DownloadSessionModel downloadSessionCreateSpec,
                  IDownloadStateChangeHandler downloadStateChangeAsyncCallback,
                  IDownloadStatusCallback downloadStatusChangeAsyncCallback);

    /**
     * Deletes a download session with the specified identifier.
     *
     * @param downloadSessionId Identifier of the download session.
     */
    void delete(String downloadSessionId);

    /**
     * Gets the download session with the specified identifier, including the most up-to-date status
     * information for the session.
     *
     * @param downloadSessionId Identifier of the download sessio .
     * @return update session attributes.
     */
    DownloadSessionModel get(String downloadSessionId);

}
