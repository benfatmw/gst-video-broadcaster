package com.kalyzee.kontroller_services_api.exceptions.system;

public class GetSystemInformationException extends RuntimeException {
    public GetSystemInformationException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
