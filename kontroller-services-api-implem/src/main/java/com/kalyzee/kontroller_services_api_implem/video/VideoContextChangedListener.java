package com.kalyzee.kontroller_services_api_implem.video;

import static com.kalyzee.panel_connection_manager.mappers.RequestCategory.VIDEO;
import static com.kalyzee.panel_connection_manager.mappers.video.VideoAction.ON_VIDEO_CONTEXT_UPDATED;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalyzee.kontroller_services_api.dtos.video.VideoContext;
import com.kalyzee.kontroller_services_api.interfaces.ContextChangedListener;
import com.kalyzee.panel_connection_manager.mappers.EventObject;
import com.kalyzee.panel_connection_manager.mappers.video.VideoAction;

import org.json.JSONObject;

import io.socket.client.Socket;

public class VideoContextChangedListener implements ContextChangedListener<VideoContext> {

    private static final String TAG = "VideoCtxChangedListener";
    private static final String CAMERA_MESSAGE = "camera_message";
    private static final String FAILED_TO_EMIT_VIDEO_CONTEXT_UPDATED_EVENT = "Failed to emit video context updated event.";
    private static final String SEND_VIDEO_CONTEXT_UPDATED_EVENT = "Send a context updated event: ";

    private Socket socket;

    public VideoContextChangedListener(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void onContextUpdated(VideoContext videoContext) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            EventObject<VideoAction, VideoContext> contextEvent = new EventObject<VideoAction, VideoContext>(VIDEO, ON_VIDEO_CONTEXT_UPDATED, videoContext);
            socket.emit(CAMERA_MESSAGE, new JSONObject(objectMapper.writeValueAsString(contextEvent)));
            Log.i(TAG, SEND_VIDEO_CONTEXT_UPDATED_EVENT + objectMapper.writeValueAsString(contextEvent));
        } catch (Exception e) {
            Log.e(TAG, FAILED_TO_EMIT_VIDEO_CONTEXT_UPDATED_EVENT, e);
        }
    }
}

