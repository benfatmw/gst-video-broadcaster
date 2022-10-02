package com.kalyzee.kontroller_services_api.interfaces.video;

public interface IVideoUploadStatusChangedListener {

    void onVideoUploadProgress(int videoId, long currentSize, long totalSize);

    void onVideoUploadSuccess(int videoId);

    void onVideoUploadFailure(int videoId, String errorMessage, int errorCode);
}
