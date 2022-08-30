package com.kalyzee.panel_connection_manager.executors.video;


import static com.kalyzee.panel_connection_manager.mappers.ResponseType.ERROR;
import static com.kalyzee.panel_connection_manager.mappers.ResponseType.SUCCESS;

import com.google.gson.Gson;
import com.kalyzee.kontroller_services_api.dtos.video.CreateWebrtcFeedbackConnectionContent;
import com.kalyzee.kontroller_services_api.interfaces.ContextChangedListener;
import com.kalyzee.kontroller_services_api.interfaces.video.VideoManager;
import com.kalyzee.panel_connection_manager.executors.PanelRequestsExecutor;
import com.kalyzee.panel_connection_manager.mappers.ErrorResponseContent;
import com.kalyzee.panel_connection_manager.mappers.ResponseObject;
import com.kalyzee.panel_connection_manager.mappers.video.StartLiveRequestContent;
import com.kalyzee.panel_connection_manager.mappers.video.StartRecordRequestContent;
import com.kalyzee.panel_connection_manager.mappers.video.StartVodRequestContent;
import com.kalyzee.panel_connection_manager.mappers.video.StopLiveResponseContent;
import com.kalyzee.panel_connection_manager.mappers.video.StopVodRequestContent;
import com.kalyzee.panel_connection_manager.mappers.video.SwitchSceneRequestContent;
import com.kalyzee.panel_connection_manager.mappers.video.VideoAction;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class VideoRequestsExecutor implements PanelRequestsExecutor {
    private VideoManager videoManager;

    public VideoRequestsExecutor(VideoManager videoManager) {
        this.videoManager = videoManager;
    }

    @Override
    public JSONObject execute(String action, Object actionContent) throws JSONException {
        Gson gson = new Gson();
        String gson_response;
        try {
            Object response_content = null;
            switch (VideoAction.value(action)) {
                case START_RECORD:
                    StartRecordRequestContent start_rec_params = gson.fromJson(actionContent.toString(),
                            StartRecordRequestContent.class);
                    videoManager.startRecord(start_rec_params.getTitle());
                    break;
                case STOP_RECORD:
                    int recordId = videoManager.stopRecord();
                    response_content = new StopLiveResponseContent(recordId);
                    break;
                case START_LIVE:
                    StartLiveRequestContent start_live_params = gson.fromJson(actionContent.toString(),
                            StartLiveRequestContent.class);
                    videoManager.startLive(start_live_params.getLiveProfile());
                    break;
                case STOP_LIVE:
                    videoManager.stopLive();
                    break;
                case START_VOD:
                    StartVodRequestContent start_vod_params = gson.fromJson(actionContent.toString(),
                            StartVodRequestContent.class);
                    videoManager.startVod(start_vod_params.getVideoId(), start_vod_params.getUploadProfile());
                    break;
                case STOP_VOD:
                    StopVodRequestContent stop_vod_params = gson.fromJson(actionContent.toString(),
                            StopVodRequestContent.class);
                    videoManager.stopVod(stop_vod_params.getVideoId());
                    break;
                case GET_VIDEO_CONTEXT:
                    response_content = videoManager.getVideoContext();
                    break;
                case CREATE_WEBRTC_CONNECTION:
                    CreateWebrtcFeedbackConnectionContent create_webrtc_connection_params = gson.fromJson(actionContent.toString(),
                            CreateWebrtcFeedbackConnectionContent.class);
                    videoManager.createWebrtcFeedbackConnection(create_webrtc_connection_params.getUri());
                    break;
                case SWITCH_SCENE:
                    SwitchSceneRequestContent switch_scene_params = gson.fromJson(actionContent.toString(),
                            SwitchSceneRequestContent.class);
                    videoManager.switchScene(switch_scene_params.getId());
                    break;
            }
            gson_response = gson.toJson(new ResponseObject<Object>(SUCCESS, null,
                    null, response_content));
        } catch (Exception e) {
            gson_response = gson.toJson(new ResponseObject<ErrorResponseContent>(ERROR, null,
                    null, new ErrorResponseContent(ExceptionUtils.getStackTrace(e))));
        }
        return new JSONObject(gson_response);
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
