package com.kalyzee.kontroller_services_api.exceptions.video;

public class StartRecordFailureException extends RuntimeException {
    public StartRecordFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
