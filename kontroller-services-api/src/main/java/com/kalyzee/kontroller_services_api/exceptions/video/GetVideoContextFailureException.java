package com.kalyzee.kontroller_services_api.exceptions.video;

public class GetVideoContextFailureException extends RuntimeException {
    public GetVideoContextFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}