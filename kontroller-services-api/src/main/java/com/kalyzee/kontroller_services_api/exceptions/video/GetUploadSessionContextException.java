package com.kalyzee.kontroller_services_api.exceptions.video;

public class GetUploadSessionContextException extends RuntimeException {
    public GetUploadSessionContextException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}