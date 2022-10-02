package com.kalyzee.panel_connection_manager.mappers.video;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kalyzee.panel_connection_manager.exceptions.InvalidRequestActionException;

public enum VideoAction {

    @JsonProperty("start_record")
    START_RECORD ("start_record"),
    @JsonProperty("on_record_failure")
    ON_RECORD_FAILURE("on_record_failure"),
    @JsonProperty("stop_record")
    STOP_RECORD ("stop_record"),
    @JsonProperty("remove_record_by_id")
    REMOVE_RECORD_BY_ID("remove_record_by_id"),
    @JsonProperty("get_record_session_context")
    GET_RECORD_SESSION_CONTEXT("get_record_session_context"),
    @JsonProperty("start_live")
    START_LIVE ("start_live"),
    @JsonProperty("on_live_failure")
    ON_LIVE_FAILURE("on_live_failure"),
    @JsonProperty("stop_live")
    STOP_LIVE ("stop_live"),
    @JsonProperty("upload_video_by_id")
    UPLOAD_VIDEO_BY_ID("upload_video_by_id"),
    @JsonProperty("get_upload_session_context")
    GET_UPLOAD_SESSION_CONTEXT("get_upload_session_context"),
    @JsonProperty("on_video_upload_progress")
    ON_VIDEO_UPLOAD_PROGRESS("on_video_upload_progress"),
    @JsonProperty("on_video_upload_success")
    ON_VIDEO_UPLOAD_SUCCESS("on_video_upload_success"),
    @JsonProperty("on_video_upload_failure")
    ON_VIDEO_UPLOAD_FAILURE("on_video_upload_failure"),
    @JsonProperty("switch_scene")
    SWITCH_SCENE ("switch_scene"),
    @JsonProperty("on_video_context_updated")
    ON_VIDEO_CONTEXT_UPDATED ("on_video_context_updated"),
    @JsonProperty("get_video_context")
    GET_VIDEO_CONTEXT ("get_video_context"),
    @JsonProperty("create_webrtc_connection")
    CREATE_WEBRTC_CONNECTION ("create_webrtc_connection");

    private String videoAction;

    private VideoAction(String videoAction) {
        this.videoAction = videoAction;
    }

    public String getString() {
        return videoAction;
    }

    @JsonValue
    public static VideoAction value(String action) {
        for (VideoAction e : values()) {
            if (e.videoAction.equals(action)) {
                return e;
            }
        }
        throw new InvalidRequestActionException("Input action: " + action + " is not supported.");
    }
}
