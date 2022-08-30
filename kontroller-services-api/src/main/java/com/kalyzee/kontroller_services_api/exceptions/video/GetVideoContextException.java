package com.kalyzee.kontroller_services_api.exceptions.video;

public class GetVideoContextException extends RuntimeException {
    public GetVideoContextException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
