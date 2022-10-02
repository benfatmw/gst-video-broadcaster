package com.kalyzee.kontroller_services_api.interfaces.video;

public interface IRecordErrorListener {
    void onRecordError(int sessionId, String errorMessage, int errorCode);
}
