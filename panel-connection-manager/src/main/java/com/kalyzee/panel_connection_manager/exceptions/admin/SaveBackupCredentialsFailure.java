package com.kalyzee.panel_connection_manager.exceptions.admin;

public class SaveBackupCredentialsFailure extends RuntimeException {

    public SaveBackupCredentialsFailure(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public SaveBackupCredentialsFailure(String errorMessage) {
        super(errorMessage);
    }
}
