package com.kalyzee.kontroller_services_api.exceptions.video;

public class SdpExchangeFailureException extends RuntimeException {
    public SdpExchangeFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}