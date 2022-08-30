package com.kalyzee.kontroller_services_api.exceptions.video;

public class CreateWebrtcFeedbackConnectionException extends RuntimeException {
    public CreateWebrtcFeedbackConnectionException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
