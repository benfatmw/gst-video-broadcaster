package com.kalyzee.kontroller_services_api.exceptions.system;

public class RebootFailureException extends RuntimeException {
    public RebootFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
