package com.kalyzee.panel_connection_manager.executors.video;


import static com.kalyzee.panel_connection_manager.mappers.ResponseType.ERROR;
import static com.kalyzee.panel_connection_manager.mappers.ResponseType.SUCCESS;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalyzee.kontroller_services_api.interfaces.ContextChangedListener;
import com.kalyzee.kontroller_services_api.interfaces.video.VideoManager;
import com.kalyzee.panel_connection_manager.executors.PanelRequestsExecutor;
import com.kalyzee.panel_connection_manager.mappers.ErrorResponseContent;
import com.kalyzee.panel_connection_manager.mappers.ResponseObject;
import com.kalyzee.panel_connection_manager.mappers.video.CreateWebrtcFeedbackConnectionContent;
import com.kalyzee.panel_connection_manager.mappers.video.GetRecordSessionContextRequestContent;
import com.kalyzee.panel_connection_manager.mappers.video.GetUploadSessionContextRequestContent;
import com.kalyzee.panel_connection_manager.mappers.video.RemoveRecordByIdRequestContent;
import com.kalyzee.panel_connection_manager.mappers.video.StartLiveRequestContent;
import com.kalyzee.panel_connection_manager.mappers.video.StartLiveResponseContent;
import com.kalyzee.panel_connection_manager.mappers.video.StartRecordResponseContent;
import com.kalyzee.panel_connection_manager.mappers.video.StopLiveRequestContent;
import com.kalyzee.panel_connection_manager.mappers.video.StopRecordRequestContent;
import com.kalyzee.panel_connection_manager.mappers.video.StopRecordResponseContent;
import com.kalyzee.panel_connection_manager.mappers.video.SwitchSceneRequestContent;
import com.kalyzee.panel_connection_manager.mappers.video.UploadVideoByIdRequestContent;
import com.kalyzee.panel_connection_manager.mappers.video.UploadVideoByIdResponseContent;
import com.kalyzee.panel_connection_manager.mappers.video.VideoAction;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class VideoRequestsExecutor implements PanelRequestsExecutor {

    private VideoManager videoManager;

    private int lastStreamId;

    public VideoRequestsExecutor(VideoManager videoManager) {
        this.videoManager = videoManager;
    }

    @Override
    public JSONObject execute(String action, Object actionContent) throws JSONException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String gsonResponse;
        try {
            Object responseContent = null;
            switch (VideoAction.value(action)) {
                case START_RECORD:
                    responseContent = new StartRecordResponseContent(videoManager.startRecord());
                    break;
                case STOP_RECORD:
                    StopRecordRequestContent stopRecParams = objectMapper.readValue(
                            objectMapper.writeValueAsString(actionContent),
                            StopRecordRequestContent.class);
                    responseContent = new StopRecordResponseContent(videoManager.stopRecord(stopRecParams.getSessionId()));
                    break;
                case REMOVE_RECORD_BY_ID:
                    RemoveRecordByIdRequestContent removeRecParams = objectMapper.readValue(
                            objectMapper.writeValueAsString(actionContent),
                            RemoveRecordByIdRequestContent.class);
                    videoManager.removeRecordFileById(removeRecParams.getVideoId());
                    break;
                case GET_RECORD_SESSION_CONTEXT:
                    GetRecordSessionContextRequestContent getRecordCtxParams = objectMapper.readValue(
                            objectMapper.writeValueAsString(actionContent),
                            GetRecordSessionContextRequestContent.class);
                    responseContent = videoManager.geRecordSessionContext(getRecordCtxParams.getSessionId());
                    break;
                case START_LIVE:
                    StartLiveRequestContent startLiveParams = objectMapper.readValue(
                            objectMapper.writeValueAsString(actionContent),
                            StartLiveRequestContent.class);
                    lastStreamId = videoManager.startLive(startLiveParams.getLiveProfile());
                    responseContent = new StartLiveResponseContent(videoManager.startLive(startLiveParams.getLiveProfile()));
                    break;
                case STOP_LIVE:
                    StopLiveRequestContent stopLiveParams = objectMapper.readValue(
                            objectMapper.writeValueAsString(actionContent),
                            StopLiveRequestContent.class);
                    if (stopLiveParams == null) {
                        videoManager.stopLive(lastStreamId);
                    } else {
                        videoManager.stopLive(stopLiveParams.getSessionId());
                    }
                    break;
                case UPLOAD_VIDEO_BY_ID:
                    UploadVideoByIdRequestContent uploadVideoByIdParams = objectMapper.readValue(
                            objectMapper.writeValueAsString(actionContent),
                            UploadVideoByIdRequestContent.class);
                    responseContent = new UploadVideoByIdResponseContent(videoManager.uploadVideoById(
                            uploadVideoByIdParams.getVideoId(),
                            uploadVideoByIdParams.getUploadProfile()));
                    break;
                case GET_UPLOAD_SESSION_CONTEXT:
                    GetUploadSessionContextRequestContent getUploadCtxParams = objectMapper.readValue(
                            objectMapper.writeValueAsString(actionContent),
                            GetUploadSessionContextRequestContent.class);
                    responseContent = videoManager.getUploadSessionContext(getUploadCtxParams.getSessionId());
                    break;
                case GET_VIDEO_CONTEXT:
                    responseContent = videoManager.getVideoContext();
                    break;
                case CREATE_WEBRTC_CONNECTION:
                    CreateWebrtcFeedbackConnectionContent createWebrtcConnectionParams =
                            objectMapper.readValue(objectMapper.writeValueAsString(actionContent),
                            CreateWebrtcFeedbackConnectionContent.class);
                    videoManager.createWebrtcFeedbackConnection(createWebrtcConnectionParams.getUri());
                    break;
                case SWITCH_SCENE:
                    SwitchSceneRequestContent switchSceneParams = objectMapper.readValue(
                            objectMapper.writeValueAsString(actionContent),
                            SwitchSceneRequestContent.class);
                    videoManager.switchScene(switchSceneParams.getId());
                    break;
            }
            gsonResponse = objectMapper.writeValueAsString(new ResponseObject<Object>(SUCCESS, null,
                    null, responseContent));
        } catch (Exception e) {
            gsonResponse = objectMapper.writeValueAsString(new ResponseObject<ErrorResponseContent>(ERROR, null,
                    null, new ErrorResponseContent(ExceptionUtils.getStackTrace(e))));
        }
        return new JSONObject(gsonResponse);
    }

    @Override
    public void shutdown() {
        videoManager.cleanup();
    }

    @Override
    public void registerEventListener(ContextChangedListener listener) {
        videoManager.registerContextChangedListener(listener);
    }

    @Override
    public void unregisterEventListener(ContextChangedListener listener) {
        videoManager.unregisterContextChangedListener(listener);
    }
}
