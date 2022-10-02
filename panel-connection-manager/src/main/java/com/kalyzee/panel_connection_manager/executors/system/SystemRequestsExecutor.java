package com.kalyzee.panel_connection_manager.executors.system;


import static com.kalyzee.panel_connection_manager.mappers.ResponseType.ERROR;
import static com.kalyzee.panel_connection_manager.mappers.ResponseType.SUCCESS;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalyzee.kontroller_services_api.interfaces.ContextChangedListener;
import com.kalyzee.kontroller_services_api.interfaces.system.SystemManager;
import com.kalyzee.kontroller_services_api.interfaces.system.update.IUpdateSessionManager;
import com.kalyzee.kontroller_services_api.interfaces.system.update.mandatory.IMandatoryUpdateManager;
import com.kalyzee.kontroller_services_api.interfaces.system.update.silent.IScheduledUpdateManager;
import com.kalyzee.panel_connection_manager.executors.PanelRequestsExecutor;
import com.kalyzee.panel_connection_manager.mappers.ErrorResponseContent;
import com.kalyzee.panel_connection_manager.mappers.ResponseObject;
import com.kalyzee.panel_connection_manager.mappers.system.SetTimeRequestContent;
import com.kalyzee.panel_connection_manager.mappers.system.SetTimeZoneRequestContent;
import com.kalyzee.panel_connection_manager.mappers.system.SystemAction;
import com.kalyzee.panel_connection_manager.mappers.system.update.AbortMandatoryUpdateRequestContent;
import com.kalyzee.panel_connection_manager.mappers.system.update.AbortSilentUpdateRequestContent;
import com.kalyzee.panel_connection_manager.mappers.system.update.GetSoftwareUpdateContextRequestContent;
import com.kalyzee.panel_connection_manager.mappers.system.update.InstallMandatoryUpdateRequestContent;
import com.kalyzee.panel_connection_manager.mappers.system.update.ScheduleSilentUpdateRequestContent;
import com.kalyzee.panel_connection_manager.mappers.system.update.ScheduleSilentUpdateResponseContent;
import com.kalyzee.panel_connection_manager.mappers.system.update.StartMandatoryDownloadRequestContent;
import com.kalyzee.panel_connection_manager.mappers.system.update.StartMandatoryDownloadResponseContent;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class SystemRequestsExecutor implements PanelRequestsExecutor {

    private final SystemManager systemManager;
    private final IUpdateSessionManager updateSessionManager;
    private final IScheduledUpdateManager scheduledUpdateManager;
    private final IMandatoryUpdateManager mandatoryUpdateManager;

    public SystemRequestsExecutor(SystemManager systemManager,
                                  IUpdateSessionManager updateSessionManager,
                                  IScheduledUpdateManager scheduledUpdateManager,
                                  IMandatoryUpdateManager mandatoryUpdateManager) {
        this.systemManager = systemManager;
        this.updateSessionManager = updateSessionManager;
        this.scheduledUpdateManager = scheduledUpdateManager;
        this.mandatoryUpdateManager = mandatoryUpdateManager;
    }

    @Override
    public JSONObject execute(String action, Object actionContent) throws JSONException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String gsonResponse;
        try {
            Object responseContent = null;
            switch (SystemAction.value(action)) {
                case GET_INFORMATION:
                    return new JSONObject(objectMapper.writeValueAsString(new ResponseObject<Object>(SUCCESS,
                            null, null, systemManager.getSystemInfo())));
                case GET_CONTEXT:
                    return new JSONObject(objectMapper.writeValueAsString(new ResponseObject<Object>(SUCCESS,
                            null, null, systemManager.getSystemContext())));
                case REBOOT:
                    systemManager.reboot();
                    break;
                case FACTORY_RESET:
                    systemManager.factoryReset();
                    break;
                case SET_TIME:
                    SetTimeRequestContent setTimeRequestArgs = objectMapper.readValue(objectMapper.writeValueAsString(actionContent),
                            SetTimeRequestContent.class);
                    systemManager.setSystemTime(setTimeRequestArgs.getTimeInMs());
                    break;
                case SET_TIME_ZONE:
                    SetTimeZoneRequestContent setTimeZoneRequestArgs = objectMapper.readValue(objectMapper.writeValueAsString(actionContent),
                            SetTimeZoneRequestContent.class);
                    systemManager.setTimeZone(setTimeZoneRequestArgs.getTimeZone());
                    break;
                case GET_SOFTWARE_UPDATE_CONTEXT:
                    GetSoftwareUpdateContextRequestContent getSwUpdateCtxArgs =
                            objectMapper.readValue(objectMapper.writeValueAsString(actionContent),
                                    GetSoftwareUpdateContextRequestContent.class);
                    return new JSONObject(objectMapper.writeValueAsString(new ResponseObject<Object>(
                            SUCCESS, null, null,
                            updateSessionManager.getById(getSwUpdateCtxArgs.getSessionId()))));
                case START_MANDATORY_SOFTWARE_DOWNLOAD:
                    StartMandatoryDownloadRequestContent startMandatoryDownloadArgs =
                            objectMapper.readValue(objectMapper.writeValueAsString(actionContent),
                                    StartMandatoryDownloadRequestContent.class);
                    String mandatoryUpdateSessionId = mandatoryUpdateManager.start(startMandatoryDownloadArgs);
                    return new JSONObject(objectMapper.writeValueAsString(new ResponseObject<Object>(SUCCESS,
                            null, null,
                            new StartMandatoryDownloadResponseContent(mandatoryUpdateSessionId))));
                case INSTALL_MANDATORY_SOFTWARE_UPDATE:
                    InstallMandatoryUpdateRequestContent installMandatoryUpdateArgs =
                            objectMapper.readValue(objectMapper.writeValueAsString(actionContent),
                                    InstallMandatoryUpdateRequestContent.class);
                    mandatoryUpdateManager.complete(installMandatoryUpdateArgs.getSessionId());
                    break;
                case ABORT_MANDATORY_SOFTWARE_UPDATE:
                    AbortMandatoryUpdateRequestContent abortMandatoryUpdateArgs =
                            objectMapper.readValue(objectMapper.writeValueAsString(actionContent),
                                    AbortMandatoryUpdateRequestContent.class);
                    mandatoryUpdateManager.cancel(abortMandatoryUpdateArgs.getSessionId());
                    break;
                case SCHEDULE_SILENT_SOFTWARE_UPDATE:
                    ScheduleSilentUpdateRequestContent scheduleSilentUpdateArgs =
                            objectMapper.readValue(objectMapper.writeValueAsString(actionContent),
                                    ScheduleSilentUpdateRequestContent.class);
                    String silentSessionId = scheduledUpdateManager.schedule(scheduleSilentUpdateArgs);
                    return new JSONObject(objectMapper.writeValueAsString(new ResponseObject<Object>(SUCCESS,
                            null, null,
                            new ScheduleSilentUpdateResponseContent(silentSessionId))));
                case ABORT_SILENT_SOFTWARE_UPDATE:
                    AbortSilentUpdateRequestContent abortSilentUpdateArgs =
                            objectMapper.readValue(objectMapper.writeValueAsString(actionContent),
                                    AbortSilentUpdateRequestContent.class);
                    scheduledUpdateManager.cancel(abortSilentUpdateArgs.getSessionId());
                    break;
            }
            gsonResponse = objectMapper.writeValueAsString(new ResponseObject<Object>(SUCCESS, null,
                    null, null));
        } catch (Exception e) {
            gsonResponse = objectMapper.writeValueAsString(new ResponseObject<ErrorResponseContent>(ERROR,
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
