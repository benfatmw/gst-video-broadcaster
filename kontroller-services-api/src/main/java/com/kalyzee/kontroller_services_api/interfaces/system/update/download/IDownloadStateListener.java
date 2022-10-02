package com.kalyzee.kontroller_services_api.interfaces.system.update.download;

import com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadStateChangedEvent;

public interface IDownloadStateListener {
    void stateChanged(DownloadStateChangedEvent event);
}
