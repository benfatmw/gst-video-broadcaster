package com.kalyzee.kontroller_services_api.exceptions.system.update.silent;

public class CancelScheduledUpdateException extends RuntimeException {
    public CancelScheduledUpdateException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
