package com.kalyzee.kontroller_services_api.exceptions.system.update.download;

public class DownloadBinaryFailureException extends RuntimeException {
    public DownloadBinaryFailureException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public DownloadBinaryFailureException(String errorMessage) {
        super(errorMessage);
    }
}