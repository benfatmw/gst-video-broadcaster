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

    public void move(MoveDirection direction) throws MoveCameraFailureException;

    public void stopMoving() throws StopMovingCameraFailureException;

    public void zoom(ZoomType type) throws ZoomFailureException;

    public void stopZooming() throws StopZoomingFailureException;

    public void setPresetView(int presetId) throws SetPresetViewFailureException;

    public void moveToPresetView(int presetId) throws MoveToPresetViewFailureException;

    public void registerContextChangedListener(ContextChangedListener listener);

    public void unregisterContextChangedListener(ContextChangedListener listener);
}
