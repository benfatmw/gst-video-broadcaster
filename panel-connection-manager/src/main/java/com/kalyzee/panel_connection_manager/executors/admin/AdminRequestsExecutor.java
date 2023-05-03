package com.kalyzee.panel_connection_manager.executors.admin;


import static com.kalyzee.panel_connection_manager.mappers.ResponseType.ERROR;
import static com.kalyzee.panel_connection_manager.mappers.ResponseType.SUCCESS;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalyzee.kontroller_services_api.dtos.admin.UpdateCredentialsContent;
import com.kalyzee.kontroller_services_api.interfaces.ContextChangedListener;
import com.kalyzee.kontroller_services_api.interfaces.admin.AdminManager;
import com.kalyzee.panel_connection_manager.executors.PanelRequestsExecutor;
import com.kalyzee.panel_connection_manager.mappers.ErrorResponseContent;
import com.kalyzee.panel_connection_manager.mappers.ResponseObject;
import com.kalyzee.panel_connection_manager.mappers.admin.AdminAction;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class AdminRequestsExecutor implements PanelRequestsExecutor {

    private AdminManager adminManager;

    public AdminRequestsExecutor(AdminManager adminManager) {
        this.adminManager = adminManager;
    }

    @Override
    public JSONObject execute(String action, Object actionContent) throws JSONException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String gsonResponse;

        try {
            switch (AdminAction.value(action)) {
                case UPDATE_CREDENTIALS:
                    UpdateCredentialsContent credentials = objectMapper.readValue(
                            objectMapper.writeValueAsString(actionContent), UpdateCredentialsContent.class);
                    adminManager.updateCredentials(credentials);
                    break;
            }
            gsonResponse = objectMapper.writeValueAsString(
                    new ResponseObject<Object>(SUCCESS, null, null, null));
        } catch (Exception e) {
            gsonResponse = objectMapper.writeValueAsString(
                    new ResponseObject<ErrorResponseContent>(ERROR, null, null,
                            new ErrorResponseContent(ExceptionUtils.getStackTrace(e))));
        }
        return new JSONObject(gsonResponse);
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void registerEventListener(ContextChangedListener listener) {
        return;
    }

    @Override
    public void unregisterEventListener(ContextChangedListener listener) {
        return;
    }

}
