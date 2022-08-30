package com.kalyzee.kontroller_services_api.interfaces.video;

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

public interface VideoManager {

    public void startRecord(String title) throws StartRecordFailureException;

    public int stopRecord() throws StopRecordFailureException;

    public void removeRecordFileById(int recordId) throws RemoveRecordFileByIdFailureException;

    public void startLive(LiveProfile liveProfile) throws StartLiveFailureException;

    public void stopLive() throws StopLiveFailureException;

    public void startVod(int videoId, UploadProfile uploadProfile) throws StartVodFailureException;

    public void stopVod(int videoId) throws StopVodFailureException;

    public VideoContext getVideoContext() throws GetVideoContextException;

    public void switchScene(int id) throws SwitchSceneFailureException;

    public void createWebrtcFeedbackConnection(String uri) throws CreateWebrtcFeedbackConnectionException;

    public void registerContextChangedListener(ContextChangedListener listener);

    public void unregisterContextChangedListener(ContextChangedListener listener);
}
