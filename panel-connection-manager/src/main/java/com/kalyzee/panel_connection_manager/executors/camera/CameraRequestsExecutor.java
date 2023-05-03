package com.kalyzee.panel_connection_manager.executors.camera;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.kalyzee.panel_connection_manager.mappers.ResponseType.ERROR;
import static com.kalyzee.panel_connection_manager.mappers.ResponseType.SUCCESS;

import com.kalyzee.kontroller_services_api.interfaces.ContextChangedListener;
import com.kalyzee.kontroller_services_api.interfaces.camera.CameraManager;
import com.kalyzee.panel_connection_manager.executors.PanelRequestsExecutor;
import com.kalyzee.panel_connection_manager.mappers.ErrorResponseContent;
import com.kalyzee.panel_connection_manager.mappers.ResponseObject;
import com.kalyzee.panel_connection_manager.mappers.camera.CameraAction;
import com.kalyzee.panel_connection_manager.mappers.camera.MoveRequestContent;
import com.kalyzee.panel_connection_manager.mappers.camera.PresetViewRequestContent;
import com.kalyzee.panel_connection_manager.mappers.camera.ZoomRequestContent;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class CameraRequestsExecutor implements PanelRequestsExecutor {

    private CameraManager cameraService;

    public CameraRequestsExecutor(CameraManager cameraService) {
        this.cameraService = cameraService;
    }

    @Override
    public JSONObject execute(String action, Object actionContent) throws JSONException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String gsonResponse;

        try {
            switch (CameraAction.value(action)) {
                case MOVE:
                    MoveRequestContent moveParams = objectMapper.readValue(objectMapper.writeValueAsString(actionContent),
                            MoveRequestContent.class);
                    cameraService.move(moveParams.getDirection());
                    break;
                case STOP_MOVING:
                    cameraService.stopMoving();
                    break;
                case ZOOM:
                    ZoomRequestContent zoomParams = objectMapper.readValue(objectMapper.writeValueAsString(actionContent),
                            ZoomRequestContent.class);
                    cameraService.zoom(zoomParams.getType());
                    break;
                case STOP_ZOOMING:
                    cameraService.stopZooming();
                    break;
                case SET_PRESET_VIEW:
                    PresetViewRequestContent setPresetParams = objectMapper.readValue(objectMapper.writeValueAsString(actionContent),
                            PresetViewRequestContent.class);
                    cameraService.setPresetView(setPresetParams.getPresetId());
                    break;
                case MOVE_TO_PRESET_VIEW:
                    PresetViewRequestContent moveToPresetParams = objectMapper.readValue(objectMapper.writeValueAsString(actionContent),
                            PresetViewRequestContent.class);
                    cameraService.moveToPresetView(moveToPresetParams.getPresetId());
                    break;
            }
            gsonResponse = objectMapper.writeValueAsString(new ResponseObject<Object>(SUCCESS, null, null, null));
        } catch (Exception e) {
            gsonResponse = objectMapper.writeValueAsString(new ResponseObject<ErrorResponseContent>(ERROR, null, null,
                    new ErrorResponseContent(ExceptionUtils.getStackTrace(e))));
        }
        return new JSONObject(gsonResponse);
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void registerEventListener(ContextChangedListener listener) {
        cameraService.registerContextChangedListener(listener);
    }

    @Override
    public void unregisterEventListener(ContextChangedListener listener) {
        cameraService.unregisterContextChangedListener(listener);
    }

}
