package com.kalyzee.kontroller_services_api.exceptions.system;

public class SetTimeZoneFailureException extends RuntimeException {
    public SetTimeZoneFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
