package com.kalyzee.kontroller_services_api.exceptions.video;

public class StartLiveFailureException  extends RuntimeException {
    public StartLiveFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
