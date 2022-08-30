package com.kalyzee.kontroller_services_api.exceptions.camera;

public class ZoomFailureException extends RuntimeException {
    public ZoomFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
