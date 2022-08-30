package com.kalyzee.kontroller_services_api.exceptions.video;

public class SwitchSceneFailureException extends RuntimeException {
    public SwitchSceneFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}