package com.kalyzee.kontroller_services_api.exceptions.camera;

public class SetPresetViewFailureException extends RuntimeException {
    public SetPresetViewFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}