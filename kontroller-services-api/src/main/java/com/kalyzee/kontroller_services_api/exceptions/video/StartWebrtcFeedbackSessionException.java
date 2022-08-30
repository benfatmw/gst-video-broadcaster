package com.kalyzee.kontroller_services_api.exceptions.video;

public class StartWebrtcFeedbackSessionException extends RuntimeException {
    public StartWebrtcFeedbackSessionException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public StartWebrtcFeedbackSessionException(String errorMessage) {
        super(errorMessage);
    }
}
