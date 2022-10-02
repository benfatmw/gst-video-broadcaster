package com.kalyzee.kontroller_services_api.exceptions.system;

public class GetSystemContextException extends RuntimeException {
    public GetSystemContextException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
