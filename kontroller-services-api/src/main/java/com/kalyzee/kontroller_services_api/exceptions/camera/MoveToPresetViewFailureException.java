package com.kalyzee.kontroller_services_api.exceptions.camera;

public class MoveToPresetViewFailureException extends RuntimeException {
    public MoveToPresetViewFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
