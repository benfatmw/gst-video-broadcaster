package com.kalyzee.panel_connection_manager.executors.system;


import static com.kalyzee.kontroller_services_api.dtos.system.update.UpdateMode.MANDATORY;
import static com.kalyzee.panel_connection_manager.mappers.ResponseType.ERROR;
import static com.kalyzee.panel_connection_manager.mappers.ResponseType.SUCCESS;

import com.google.gson.Gson;
import com.kalyzee.kontroller_services_api.dtos.system.update.UpdateSessionModel;
import com.kalyzee.kontroller_services_api.dtos.system.update.download.DownloadSessionModel;
import com.kalyzee.kontroller_services_api.interfaces.ContextChangedListener;
import com.kalyzee.kontroller_services_api.interfaces.system.SystemManager;
import com.kalyzee.kontroller_services_api.interfaces.system.update.IUpdateSessionManager;
import com.kalyzee.panel_connection_manager.executors.PanelRequestsExecutor;
import com.kalyzee.panel_connection_manager.mappers.ErrorResponseContent;
import com.kalyzee.panel_connection_manager.mappers.ResponseObject;
import com.kalyzee.panel_connection_manager.mappers.system.SystemAction;
import com.kalyzee.panel_connection_manager.mappers.system.update.AbortMandatoryUpdateRequestContent;
import com.kalyzee.panel_connection_manager.mappers.system.update.GetSoftwareUpdateContextRequestContent;
import com.kalyzee.panel_connection_manager.mappers.system.update.InstallMandatoryUpdateRequestContent;
import com.kalyzee.panel_connection_manager.mappers.system.update.StartMandatoryDownloadRequestContent;
import com.kalyzee.panel_connection_manager.mappers.system.update.StartMandatoryDownloadResponseContent;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class SystemRequestsExecutor implements PanelRequestsExecutor {

    private SystemManager systemManager;
    private IUpdateSessionManager updateSessionManager;

    public SystemRequestsExecutor(SystemManager systemManager, IUpdateSessionManager updateSessionManager) {
        this.systemManager = systemManager;
        this.updateSessionManager = updateSessionManager;
    }

    @Override
    public JSONObject execute(String action, Object actionContent) throws JSONException {
        Gson gson = new Gson();
        String gsonResponse;
        try {
            Object responseContent = null;
            switch (SystemAction.value(action)) {
                case GET_SYSTEM_INFORMATION:
                    return new JSONObject(gson.toJson(new ResponseObject<Object>(SUCCESS, null,
                            null, systemManager.getSystemInfo())));
                case REBOOT:
                    systemManager.reboot();
                    break;
                case FACTORY_RESET:
                    systemManager.factoryReset();
                    break;
                case GET_SOFTWARE_UPDATE_CONTEXT:
                    GetSoftwareUpdateContextRequestContent getSwUpdateCtxArgs = gson.fromJson(actionContent.toString(),
                            GetSoftwareUpdateContextRequestContent.class);
                    return new JSONObject(gson.toJson(new ResponseObject<Object>(SUCCESS, null, null,
                            updateSessionManager.get(getSwUpdateCtxArgs.getSessionId()))));
                case START_MANDATORY_SOFTWARE_DOWNLOAD:
                    StartMandatoryDownloadRequestContent startMandatoryDownloadArgs = gson.fromJson(actionContent.toString(),
                            StartMandatoryDownloadRequestContent.class);
                    String sessionId = updateSessionManager.create(new UpdateSessionModel(MANDATORY,
                                    startMandatoryDownloadArgs.getImageType(),
                                    startMandatoryDownloadArgs.getVersionCode()),
                            new DownloadSessionModel(startMandatoryDownloadArgs.getUrl(),
                                    startMandatoryDownloadArgs.getSha256Fingerprint(),
                                    "/cache/",
                                    "update.zip"));
                    return new JSONObject(gson.toJson(new ResponseObject<Object>(SUCCESS,
                            null, null, new StartMandatoryDownloadResponseContent(sessionId))));
                case INSTALL_MANDATORY_SOFTWARE_UPDATE:
                    InstallMandatoryUpdateRequestContent installMandatoryUpdateArgs = gson.fromJson(actionContent.toString(),
                            InstallMandatoryUpdateRequestContent.class);
                    updateSessionManager.complete(installMandatoryUpdateArgs.getSessionId());
                    break;
                case ABORT_MANDATORY_SOFTWARE_UPDATE:
                    AbortMandatoryUpdateRequestContent abortMandatoryUpdateArgs = gson.fromJson(actionContent.toString(),
                            AbortMandatoryUpdateRequestContent.class);
                    updateSessionManager.delete(abortMandatoryUpdateArgs.getSessionId());
                    break;
            }
            gsonResponse = gson.toJson(new ResponseObject<Object>(SUCCESS, null,
                    null, responseContent));
        } catch (Exception e) {
            gsonResponse = gson.toJson(new ResponseObject<ErrorResponseContent>(ERROR,
                    null, null,
                    new ErrorResponseContent(ExceptionUtils.getStackTrace(e))));
        }
        return new JSONObject(gsonResponse);
    }

    @Override
    public void registerEventListener(ContextChangedListener listener) {
        systemManager.registerContextChangedListener(listener);
    }

    @Override
    public void unregisterEventListener(ContextChangedListener listener) {
        systemManager.unregisterContextChangedListener(listener);
    }
}
