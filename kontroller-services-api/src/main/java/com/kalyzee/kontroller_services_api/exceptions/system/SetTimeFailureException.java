package com.kalyzee.kontroller_services_api.exceptions.system;

public class SetTimeFailureException extends RuntimeException {
    public SetTimeFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}