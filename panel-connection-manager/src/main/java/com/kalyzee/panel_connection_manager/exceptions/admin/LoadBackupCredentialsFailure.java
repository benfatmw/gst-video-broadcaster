package com.kalyzee.panel_connection_manager.exceptions.admin;

public class LoadBackupCredentialsFailure extends RuntimeException {

    public LoadBackupCredentialsFailure(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public LoadBackupCredentialsFailure(String errorMessage) {
        super(errorMessage);
    }
}
