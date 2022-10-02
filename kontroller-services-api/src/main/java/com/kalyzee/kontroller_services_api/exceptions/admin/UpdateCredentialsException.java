package com.kalyzee.kontroller_services_api.exceptions.admin;

public class UpdateCredentialsException extends RuntimeException {
    public UpdateCredentialsException(String errorMessage) {
        super(errorMessage);
    }
    public UpdateCredentialsException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
