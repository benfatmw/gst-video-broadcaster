package com.kalyzee.kontroller_services_api.exceptions.camera;

public class StopMovingCameraFailureException extends RuntimeException {
    public StopMovingCameraFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
