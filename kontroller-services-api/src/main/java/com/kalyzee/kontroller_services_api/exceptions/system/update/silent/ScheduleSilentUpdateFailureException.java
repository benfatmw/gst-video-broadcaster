package com.kalyzee.kontroller_services_api.exceptions.system.update.silent;

public class ScheduleSilentUpdateFailureException extends RuntimeException {
    public ScheduleSilentUpdateFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}