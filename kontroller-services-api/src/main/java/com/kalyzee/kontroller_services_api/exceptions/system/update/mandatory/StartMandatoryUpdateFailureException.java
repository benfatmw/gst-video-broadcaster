package com.kalyzee.kontroller_services_api.exceptions.system.update.mandatory;

public class StartMandatoryUpdateFailureException extends RuntimeException {
    public StartMandatoryUpdateFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}