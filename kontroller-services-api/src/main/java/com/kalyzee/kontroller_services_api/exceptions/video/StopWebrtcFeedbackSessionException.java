package com.kalyzee.kontroller_services_api.exceptions.video;

public class StopWebrtcFeedbackSessionException extends RuntimeException {
    public StopWebrtcFeedbackSessionException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}