package com.kalyzee.kontroller_services_api.exceptions.video;

public class StopVodFailureException extends RuntimeException {
    public StopVodFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
