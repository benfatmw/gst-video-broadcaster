package com.kalyzee.kontroller_services_api.interfaces.system.update.download;

public interface IDownloadStatusCallback {

    void onProgress(long currentSize, long totalSize);

    void onSuccess();

    void onFailure(String errorMessage);
}
