package com.kalyzee.kontroller_services_api.exceptions.video;

public class RemoveRecordFileByIdFailureException extends RuntimeException {
    public RemoveRecordFileByIdFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
