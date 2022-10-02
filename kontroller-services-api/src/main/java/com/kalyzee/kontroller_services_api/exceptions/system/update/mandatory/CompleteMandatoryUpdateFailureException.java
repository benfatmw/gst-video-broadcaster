package com.kalyzee.kontroller_services_api.exceptions.system.update.mandatory;

public class CompleteMandatoryUpdateFailureException extends RuntimeException {
    public CompleteMandatoryUpdateFailureException(String errorMessage) {
        super(errorMessage);
    }
    public CompleteMandatoryUpdateFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
