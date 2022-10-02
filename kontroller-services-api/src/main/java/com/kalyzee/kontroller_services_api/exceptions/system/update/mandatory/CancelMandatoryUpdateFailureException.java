package com.kalyzee.kontroller_services_api.exceptions.system.update.mandatory;

public class CancelMandatoryUpdateFailureException extends RuntimeException {
    public CancelMandatoryUpdateFailureException(String errorMessage) {
        super(errorMessage);
    }
    public CancelMandatoryUpdateFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}