package com.kalyzee.kontroller_services_api_implem.system.update;


import com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionModel;
import com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionModel;
import com.kalyzee.kontroller_services_api.interfaces.system.update.IUpdateSessionManager;
import com.kalyzee.kontroller_services_api.interfaces.system.update.IUpdateStateChangeHandler;

public class UpdateSessionManagerImplem implements IUpdateSessionManager {

    @Override
    public String create(UpdateSessionModel updateSessionCreateSpec, DownloadSessionModel downloadSessionCreateSpec) {
        return null;
    }

    @Override
    public String create(UpdateSessionModel updateSessionCreateSpec, DownloadSessionModel downloadSessionCreateSpec, IUpdateStateChangeHandler updateStateChangeHandler) {
        return null;
    }

    @Override
    public void complete(String sessionId) {

    }

    @Override
    public void delete(String sessionId) {

    }

    @Override
    public UpdateSessionModel get(String sessionId) {
        return null;
    }
}
