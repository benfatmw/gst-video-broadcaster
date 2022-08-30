package com.kalyzee.kontroller_services_api.exceptions.system.update;

public class GetSoftwareUpdateContextException extends RuntimeException {
    public GetSoftwareUpdateContextException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}