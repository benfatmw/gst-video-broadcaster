package com.kalyzee.kontroller_services_api_implem.camera;


import com.kalyzee.kontroller_services_api.dtos.camera.MoveDirection;
import com.kalyzee.kontroller_services_api.dtos.camera.ZoomType;
import com.kalyzee.kontroller_services_api.exceptions.camera.MoveCameraFailureException;
import com.kalyzee.kontroller_services_api.exceptions.camera.MoveToPresetViewFailureException;
import com.kalyzee.kontroller_services_api.exceptions.camera.SetPresetViewFailureException;
import com.kalyzee.kontroller_services_api.exceptions.camera.StopMovingCameraFailureException;
import com.kalyzee.kontroller_services_api.exceptions.camera.StopZoomingFailureException;
import com.kalyzee.kontroller_services_api.exceptions.camera.ZoomFailureException;
import com.kalyzee.kontroller_services_api.interfaces.ContextChangedListener;
import com.kalyzee.kontroller_services_api.interfaces.camera.CameraManager;

public class CameraManagerImplem implements CameraManager {

    @Override
    public void move(MoveDirection direction) throws MoveCameraFailureException {

    }

    @Override
    public void stopMoving() throws StopMovingCameraFailureException {

    }

    @Override
    public void zoom(ZoomType type) throws ZoomFailureException {

    }

    @Override
    public void stopZooming() throws StopZoomingFailureException {

    }

    @Override
    public void setPresetView(int presetId) throws SetPresetViewFailureException {

    }

    @Override
    public void moveToPresetView(int presetId) throws MoveToPresetViewFailureException {

    }

    @Override
    public void registerContextChangedListener(ContextChangedListener listener) {

    }

    @Override
    public void unregisterContextChangedListener(ContextChangedListener listener) {

    }
}
