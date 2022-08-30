package com.kalyzee.kontroller_services_api.exceptions.video;

public class StopLiveFailureException extends RuntimeException {
    public StopLiveFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
