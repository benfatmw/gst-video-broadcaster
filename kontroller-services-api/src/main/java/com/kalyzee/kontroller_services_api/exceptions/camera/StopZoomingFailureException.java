package com.kalyzee.kontroller_services_api.exceptions.camera;

public class StopZoomingFailureException extends RuntimeException {
    public StopZoomingFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
