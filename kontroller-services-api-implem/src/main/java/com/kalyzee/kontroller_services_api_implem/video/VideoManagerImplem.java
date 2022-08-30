package com.kalyzee.kontroller_services_api_implem.video;

import com.kalyzee.kontroller_services_api.dtos.video.LiveProfile;
import com.kalyzee.kontroller_services_api.dtos.video.UploadProfile;
import com.kalyzee.kontroller_services_api.dtos.video.VideoContext;
import com.kalyzee.kontroller_services_api.exceptions.video.CreateWebrtcFeedbackConnectionException;
import com.kalyzee.kontroller_services_api.exceptions.video.GetVideoContextException;
import com.kalyzee.kontroller_services_api.exceptions.video.RemoveRecordFileByIdFailureException;
import com.kalyzee.kontroller_services_api.exceptions.video.StartLiveFailureException;
import com.kalyzee.kontroller_services_api.exceptions.video.StartRecordFailureException;
import com.kalyzee.kontroller_services_api.exceptions.video.StartVodFailureException;
import com.kalyzee.kontroller_services_api.exceptions.video.StopLiveFailureException;
import com.kalyzee.kontroller_services_api.exceptions.video.StopRecordFailureException;
import com.kalyzee.kontroller_services_api.exceptions.video.StopVodFailureException;
import com.kalyzee.kontroller_services_api.exceptions.video.SwitchSceneFailureException;
import com.kalyzee.kontroller_services_api.interfaces.ContextChangedListener;
import com.kalyzee.kontroller_services_api.interfaces.video.VideoManager;

public class VideoManagerImplem implements VideoManager {

    @Override
    public void startRecord(String title) throws StartRecordFailureException {

    }

    @Override
    public int stopRecord() throws StopRecordFailureException {
        return 0;
    }

    @Override
    public void removeRecordFileById(int recordId) throws RemoveRecordFileByIdFailureException {

    }

    @Override
    public void startLive(LiveProfile liveProfile) throws StartLiveFailureException {

    }

    @Override
    public void stopLive() throws StopLiveFailureException {

    }

    @Override
    public void startVod(int videoId, UploadProfile uploadProfile) throws StartVodFailureException {

    }

    @Override
    public void stopVod(int videoId) throws StopVodFailureException {

    }

    @Override
    public VideoContext getVideoContext() throws GetVideoContextException {
        return null;
    }

    @Override
    public void switchScene(int id) throws SwitchSceneFailureException {

    }

    @Override
    public void createWebrtcFeedbackConnection(String uri) throws CreateWebrtcFeedbackConnectionException {

    }

    @Override
    public void registerContextChangedListener(ContextChangedListener listener) {

    }

    @Override
    public void unregisterContextChangedListener(ContextChangedListener listener) {

    }
}
