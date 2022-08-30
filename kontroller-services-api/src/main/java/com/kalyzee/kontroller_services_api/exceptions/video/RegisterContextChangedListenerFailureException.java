package com.kalyzee.kontroller_services_api.exceptions.video;

public class RegisterContextChangedListenerFailureException extends RuntimeException {
    public RegisterContextChangedListenerFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
