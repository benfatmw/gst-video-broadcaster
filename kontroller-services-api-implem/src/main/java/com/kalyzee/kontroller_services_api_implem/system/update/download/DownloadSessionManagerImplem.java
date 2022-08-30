package com.kalyzee.kontroller_services_api_implem.system.update.download;


import com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionModel;
import com.kalyzee.kontroller_services_api.interfaces.system.update.download.IDownloadSessionManager;
import com.kalyzee.kontroller_services_api.interfaces.system.update.download.IDownloadStateChangeHandler;
import com.kalyzee.kontroller_services_api.interfaces.system.update.download.IDownloadStatusCallback;

public class DownloadSessionManagerImplem implements IDownloadSessionManager {

    @Override
    public String create(DownloadSessionModel downloadSessionCreateSpec, IDownloadStateChangeHandler downloadStateChangeAsyncCallback) {
        return null;
    }

    @Override
    public String create(DownloadSessionModel downloadSessionCreateSpec, IDownloadStateChangeHandler downloadStateChangeAsyncCallback, IDownloadStatusCallback downloadStatusChangeAsyncCallback) {
        return null;
    }

    @Override
    public void delete(String downloadSessionId) {

    }

    @Override
    public DownloadSessionModel get(String downloadSessionId) {
        return null;
    }
}
