package com.kalyzee.kontroller_services_api.exceptions.video;

public class StartVodFailureException extends RuntimeException {
    public StartVodFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
