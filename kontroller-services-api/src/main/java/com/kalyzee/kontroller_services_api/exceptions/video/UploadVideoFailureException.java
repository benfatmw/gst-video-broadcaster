package com.kalyzee.kontroller_services_api.exceptions.video;

public class UploadVideoFailureException extends RuntimeException {
    public UploadVideoFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
