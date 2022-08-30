package com.kalyzee.kontroller_services_api.exceptions.video;

public class UnregisterContextChangedListenerFailureException extends RuntimeException {
    public UnregisterContextChangedListenerFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
