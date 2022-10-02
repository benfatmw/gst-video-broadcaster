package com.kalyzee.kontroller_services_api_implem.system.update.download;


import static com.kalyzee.panel_connection_manager.mappers.RequestCategory.SYSTEM;
import static com.kalyzee.panel_connection_manager.mappers.system.SystemAction.ON_MANDATORY_SOFTWARE_DOWNLOAD_PROGRESS;
import static com.kalyzee.panel_connection_manager.mappers.system.SystemAction.ON_MANDATORY_SOFTWARE_UPDATE_FAILURE;
import static com.kalyzee.panel_connection_manager.mappers.system.SystemAction.ON_MANDATORY_SOFTWARE_UPDATE_READY_FOR_APPLICATION;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalyzee.kontroller_services_api.interfaces.system.update.download.IDownloadStatusCallback;
import com.kalyzee.panel_connection_manager.mappers.EventObject;
import com.kalyzee.panel_connection_manager.mappers.system.SystemAction;
import com.kalyzee.panel_connection_manager.mappers.system.update.OnMandatoryDownloadProgressEventContent;
import com.kalyzee.panel_connection_manager.mappers.system.update.OnMandatoryUpdateFailureEventContent;
import com.kalyzee.panel_connection_manager.mappers.system.update.OnMandatoryUpdateReadyForApplicationEventContent;

import org.json.JSONObject;

import io.socket.client.Socket;

public class DownloadStatusCallback implements IDownloadStatusCallback {

    private static final String TAG = "DownloadStatusCallback";
    private static final String CAMERA_MESSAGE = "camera_message";
    private static final String FAILED_TO_SEND_DOWNLOAD_PROGRESS_EVENT = "Failed to emit download progress event.";
    private static final String FAILED_TO_SEND_DOWNLOAD_SUCCESS_EVENT = "Failed to emit download success event.";
    private static final String FAILED_TO_SEND_DOWNLOAD_FAILURE_EVENT = "Failed to emit download failure event.";
    private static final String SEND_DOWNLOAD_PROGRESS_EVENT = "Send download progress event: ";
    private static final String SEND_DOWNLOAD_SUCCESS_EVENT = "Send download success event: ";
    private static final String SEND_DOWNLOAD_FAILURE_EVENT = "Send download failure event: ";

    private final Socket socket;
    private final String sessionId;

    public DownloadStatusCallback(Socket socket, String sessionId) {
        this.socket = socket;
        this.sessionId = sessionId;
    }

    @Override
    public void onProgress(long currentSize, long totalSize) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            EventObject<SystemAction, OnMandatoryDownloadProgressEventContent> downloadProgressEvent =
                    new EventObject<SystemAction, OnMandatoryDownloadProgressEventContent>(SYSTEM,
                            ON_MANDATORY_SOFTWARE_DOWNLOAD_PROGRESS,
                            new OnMandatoryDownloadProgressEventContent(sessionId, currentSize, totalSize));
            socket.emit(CAMERA_MESSAGE, new JSONObject(objectMapper.writeValueAsString(downloadProgressEvent)));
            Log.i(TAG, SEND_DOWNLOAD_PROGRESS_EVENT + objectMapper.writeValueAsString(downloadProgressEvent));
        } catch (Exception e) {
            Log.e(TAG, FAILED_TO_SEND_DOWNLOAD_PROGRESS_EVENT, e);
        }
    }

    @Override
    public void onSuccess() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            EventObject<SystemAction, OnMandatoryUpdateReadyForApplicationEventContent> downloadSuccessEvent =
                    new EventObject<SystemAction, OnMandatoryUpdateReadyForApplicationEventContent>(SYSTEM,
                            ON_MANDATORY_SOFTWARE_UPDATE_READY_FOR_APPLICATION,
                            new OnMandatoryUpdateReadyForApplicationEventContent(sessionId));
            socket.emit(CAMERA_MESSAGE, new JSONObject(objectMapper.writeValueAsString(downloadSuccessEvent)));
            Log.i(TAG, SEND_DOWNLOAD_SUCCESS_EVENT + objectMapper.writeValueAsString(downloadSuccessEvent));
        } catch (Exception e) {
            Log.e(TAG, FAILED_TO_SEND_DOWNLOAD_SUCCESS_EVENT, e);
        }
    }

    @Override
    public void onFailure(String errorMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            EventObject<SystemAction, OnMandatoryUpdateFailureEventContent> downloadErrorEvent =
                    new EventObject<SystemAction, OnMandatoryUpdateFailureEventContent>(SYSTEM,
                            ON_MANDATORY_SOFTWARE_UPDATE_FAILURE,
                            new OnMandatoryUpdateFailureEventContent(sessionId, errorMessage));
            socket.emit(CAMERA_MESSAGE, new JSONObject(objectMapper.writeValueAsString(downloadErrorEvent)));
            Log.i(TAG, SEND_DOWNLOAD_FAILURE_EVENT + objectMapper.writeValueAsString(downloadErrorEvent));
        } catch (Exception e) {
            Log.e(TAG, FAILED_TO_SEND_DOWNLOAD_FAILURE_EVENT, e);
        }
    }
}
