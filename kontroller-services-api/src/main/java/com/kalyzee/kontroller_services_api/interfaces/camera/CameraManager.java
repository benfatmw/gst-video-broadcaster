package com.kalyzee.kontroller_services_api.interfaces.camera;


import com.kalyzee.kontroller_services_api.dtos.camera.MoveDirection;
import com.kalyzee.kontroller_services_api.dtos.camera.ZoomType;
import com.kalyzee.kontroller_services_api.exceptions.camera.MoveCameraFailureException;
import com.kalyzee.kontroller_services_api.exceptions.camera.MoveToPresetViewFailureException;
import com.kalyzee.kontroller_services_api.exceptions.camera.SetPresetViewFailureException;
import com.kalyzee.kontroller_services_api.exceptions.camera.StopMovingCameraFailureException;
import com.kalyzee.kontroller_services_api.exceptions.camera.StopZoomingFailureException;
import com.kalyzee.kontroller_services_api.exceptions.camera.ZoomFailureException;
import com.kalyzee.kontroller_services_api.interfaces.ContextChangedListener;

public interface CameraManager {

    void move(MoveDirection direction) throws MoveCameraFailureException;

    void stopMoving() throws StopMovingCameraFailureException;

    void zoom(ZoomType type) throws ZoomFailureException;

    void stopZooming() throws StopZoomingFailureException;

    void setPresetView(int presetId) throws SetPresetViewFailureException;

    void moveToPresetView(int presetId) throws MoveToPresetViewFailureException;

    void registerContextChangedListener(ContextChangedListener listener);

    void unregisterContextChangedListener(ContextChangedListener listener);
}
