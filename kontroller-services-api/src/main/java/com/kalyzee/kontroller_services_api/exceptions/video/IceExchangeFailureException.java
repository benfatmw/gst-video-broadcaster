package com.kalyzee.kontroller_services_api.exceptions.video;

public class IceExchangeFailureException extends RuntimeException {
    public IceExchangeFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}