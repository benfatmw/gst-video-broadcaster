package com.kalyzee.panel_connection_manager.executors.network;

import static com.kalyzee.panel_connection_manager.mappers.ResponseType.ERROR;
import static com.kalyzee.panel_connection_manager.mappers.ResponseType.SUCCESS;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalyzee.kontroller_services_api.interfaces.ContextChangedListener;
import com.kalyzee.kontroller_services_api.interfaces.network.NetworkSettingManager;
import com.kalyzee.panel_connection_manager.executors.PanelRequestsExecutor;
import com.kalyzee.panel_connection_manager.mappers.ErrorResponseContent;
import com.kalyzee.panel_connection_manager.mappers.ResponseObject;
import com.kalyzee.panel_connection_manager.mappers.network.NetworkAction;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class NetworkRequestsExecutor implements PanelRequestsExecutor {

    private NetworkSettingManager networkSettingManager;

    public NetworkRequestsExecutor(NetworkSettingManager networkSettingManager) {
        this.networkSettingManager = networkSettingManager;
    }

    @Override
    public JSONObject execute(String action, Object action_content) throws JSONException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String gsonResponse;
        try {
            switch (NetworkAction.value(action)) {
                case GET_INFORMATION:
                    return new JSONObject(
                            objectMapper.writeValueAsString(
                                    new ResponseObject<Object>(SUCCESS, null, null,
                                            networkSettingManager.getInformation())));
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
    public void registerEventListener(ContextChangedListener listener) {

    }

    @Override
    public void unregisterEventListener(ContextChangedListener listener) {

    }
}
