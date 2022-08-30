package com.kalyzee.kontroller_services_api.exceptions.camera;

public class MoveCameraFailureException extends RuntimeException {
    public MoveCameraFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
