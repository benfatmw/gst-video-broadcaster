package com.kalyzee.kontroller_services_api.exceptions.system;

public class FactoryResetFailureException extends RuntimeException {
    public FactoryResetFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}