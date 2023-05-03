package com.kalyzee.kontroller_services_api.interfaces.video;


import com.kalyzee.kontroller_services_api.dtos.video.LiveProfile;
import com.kalyzee.kontroller_services_api.dtos.video.RecordSessionContext;
import com.kalyzee.kontroller_services_api.dtos.video.UploadProfile;
import com.kalyzee.kontroller_services_api.dtos.video.UploadSessionContext;
import com.kalyzee.kontroller_services_api.dtos.video.VideoContext;
import com.kalyzee.kontroller_services_api.exceptions.video.CreateWebrtcFeedbackConnectionException;
import com.kalyzee.kontroller_services_api.exceptions.video.GetRecordSessionContextFailureException;
import com.kalyzee.kontroller_services_api.exceptions.video.GetUploadSessionContextException;
import com.kalyzee.kontroller_services_api.exceptions.video.GetVideoContextException;
import com.kalyzee.kontroller_services_api.exceptions.video.RemoveRecordFileByIdFailureException;
import com.kalyzee.kontroller_services_api.exceptions.video.StartLiveFailureException;
import com.kalyzee.kontroller_services_api.exceptions.video.StartRecordFailureException;
import com.kalyzee.kontroller_services_api.exceptions.video.StopLiveFailureException;
import com.kalyzee.kontroller_services_api.exceptions.video.StopRecordFailureException;
import com.kalyzee.kontroller_services_api.exceptions.video.SwitchSceneFailureException;
import com.kalyzee.kontroller_services_api.exceptions.video.UploadVideoFailureException;
import com.kalyzee.kontroller_services_api.interfaces.ContextChangedListener;

public interface VideoManager {

    int startRecord() throws StartRecordFailureException;

    int stopRecord(int sessionId) throws StopRecordFailureException;

    void removeRecordFileById(int recordId) throws RemoveRecordFileByIdFailureException;

    RecordSessionContext geRecordSessionContext(int sessionId) throws GetRecordSessionContextFailureException;

    int startLive(LiveProfile liveProfile) throws StartLiveFailureException;

    void stopLive(int id) throws StopLiveFailureException;

    int uploadVideoById(int videoId, UploadProfile uploadProfile) throws UploadVideoFailureException;

    UploadSessionContext getUploadSessionContext(int sessionId) throws GetUploadSessionContextException;

    VideoContext getVideoContext() throws GetVideoContextException;

    void switchScene(int id) throws SwitchSceneFailureException;

    void createWebrtcFeedbackConnection(String uri) throws CreateWebrtcFeedbackConnectionException;

    void registerContextChangedListener(ContextChangedListener listener);

    void unregisterContextChangedListener(ContextChangedListener listener);

    void registerVideoUploadStatusChangedListener(IVideoUploadStatusChangedListener listener);

    void unregisterVideoUploadStatusChangedListener(IVideoUploadStatusChangedListener listener);

    void registerRecordErrorListener(IRecordErrorListener listener);

    void unregisterRecordErrorListener(IRecordErrorListener listener);

    void cleanup();
}
