package com.kalyzee.panel_connection_manager.executors.network;

import static com.kalyzee.panel_connection_manager.mappers.ResponseType.ERROR;
import static com.kalyzee.panel_connection_manager.mappers.ResponseType.SUCCESS;

import com.google.gson.Gson;
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
    public JSONObject execute(String action, Object action_content) throws JSONException {
        Gson gson = new Gson();
        String gson_response;
        try {
            Object response_content = null;
            switch (NetworkAction.value(action)) {
                case GET_INFORMATION:
                    return new JSONObject(gson.toJson(new ResponseObject<Object>(SUCCESS, null, null, networkSettingManager.getInformation())));
            }
            gson_response = gson.toJson(new ResponseObject<Object>(SUCCESS, null, null, response_content));
        } catch (Exception e) {
            gson_response = gson.toJson(new ResponseObject<ErrorResponseContent>(ERROR, null, null, new ErrorResponseContent(ExceptionUtils.getStackTrace(e))));
        }
        return new JSONObject(gson_response);
    }

    @Override
    public void registerEventListener(ContextChangedListener listener) {

    }

    @Override
    public void unregisterEventListener(ContextChangedListener listener) {

    }
}
