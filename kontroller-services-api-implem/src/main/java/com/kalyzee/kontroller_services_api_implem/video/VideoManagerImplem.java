package com.kalyzee.kontroller_services_api_implem.video;

import android.content.Context;
import android.util.Log;

import com.kalyzee.kontroller_services_api.dtos.video.LiveProfile;
import com.kalyzee.kontroller_services_api.dtos.video.RecordSessionContext;
import com.kalyzee.kontroller_services_api.dtos.video.UploadProfile;
import com.kalyzee.kontroller_services_api.dtos.video.UploadSessionContext;
import com.kalyzee.kontroller_services_api.dtos.video.VideoContext;
import com.kalyzee.kontroller_services_api.dtos.video.VideoInformation;
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
import com.kalyzee.kontroller_services_api.interfaces.video.IRecordErrorListener;
import com.kalyzee.kontroller_services_api.interfaces.video.IVideoUploadStatusChangedListener;
import com.kalyzee.kontroller_services_api.interfaces.video.VideoManager;
import com.kalyzee.panel_connection_manager.WebrtcSignallingClient;
import com.kalyzee.panel_connection_manager.utils.SocketSSLBuilder;

import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.PeerConnectionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.socket.client.Socket;

public class VideoManagerImplem implements VideoManager {

    private static final String TAG = "VideoManager";
    private static final String START_RECORD = "Start recording.";
    private static final String UPLOAD_VIDEO_BY_ID = "Upload video by id.";
    private static final String START_LIVE = "Start live.";
    private static final String STOP_LIVE = "Stop live.";
    private static final String STOP_RECORD = "Stop recording.";
    private static final String REMOVE_RECORD_FILE_BY_ID = "Remove record file by id. Id:";
    private static final String GET_UPLOAD_SESSION_CONTEXT_BY_ID = "Get upload session context by id.";
    private static final String GET_RECORD_CONTEXT_BY_ID = "Get record context by id.";
    private static final String GET_VIDEO_CONTEXT = "Get video context.";
    private static final String SWITCH_SCENE = "Switch scene.";
    private static final String REGISTER_CONTEXT_CHANGED_LISTENER = "Register context changed listener.";
    private static final String UNREGISTER_CONTEXT_CHANGED_LISTENER = "Unregister context changed listener.";
    private static final String REGISTER_VIDEO_UPLOAD_STATUS_CHANGED_LISTENER = "Register video upload status changed listener.";
    private static final String UNREGISTER_VIDEO_UPLOAD_STATUS_CHANGED_LISTENER = "Unregister video upload status changed listener..";
    private static final String REGISTER_RECORD_ERROR_LISTENER = "Register record error listener.";
    private static final String UNREGISTER_RECORD_ERROR_LISTENER = "Unregister record error listener.";
    private static final String CREATE_WEBRTC_FEEDBACK_CONNECTION = "Create webrtc feedback connection.";

    private static final String FAILED_TO_CREATE_WEBRTC_FEEDBACK_CONNECTION = "Failed to create webrtc feedback connection.";

    private final PeerConnectionFactory peerConnectionFactory;
    private final Context context;
    private static List<ContextChangedListener<VideoContext>> videoContextListenersList = new ArrayList<>();
    private static List<IVideoUploadStatusChangedListener> videoUploadStatusChangedListenersList = new ArrayList<>();
    private static List<IRecordErrorListener> recordErrorListenersList = new ArrayList<>();

    private static HashMap<Socket, WebrtcSignallingClient> webrtcSignallingClientsMap = new HashMap<Socket, WebrtcSignallingClient>();

    public VideoManagerImplem(Context context) {
        this.context = context;
        this.peerConnectionFactory = createPeerConnectionFactory(context);
    }

    @Override
    public int startRecord() throws StartRecordFailureException {
        Log.i(TAG, START_RECORD);
        return 0;
    }

    @Override
    public int stopRecord(int sessionId) throws StopRecordFailureException {
        Log.i(TAG, STOP_RECORD);
        return 0;
    }

    @Override
    public void removeRecordFileById(int recordId) throws RemoveRecordFileByIdFailureException {
        Log.i(TAG, REMOVE_RECORD_FILE_BY_ID + recordId);
    }

    @Override
    public RecordSessionContext geRecordSessionContext(int sessionId) throws GetRecordSessionContextFailureException {
        Log.i(TAG, GET_RECORD_CONTEXT_BY_ID);
        return new RecordSessionContext(new VideoInformation("Stub",0,0,0), "IDLE");
    }

    @Override
    public int startLive(LiveProfile liveProfile) throws StartLiveFailureException {
        Log.i(TAG, START_LIVE);
        return 0;
    }

    @Override
    public void stopLive(int id) throws StopLiveFailureException {
        Log.i(TAG, STOP_LIVE);
    }

    @Override
    public int uploadVideoById(int videoId, UploadProfile uploadProfile) throws UploadVideoFailureException {
        Log.i(TAG, UPLOAD_VIDEO_BY_ID);
        return 0;
    }

    @Override
    public UploadSessionContext getUploadSessionContext(int sessionId) throws GetUploadSessionContextException {
        Log.i(TAG, GET_UPLOAD_SESSION_CONTEXT_BY_ID);
        return new UploadSessionContext(0, "Stub!");
    }

    @Override
    public VideoContext getVideoContext() throws GetVideoContextException {
        Log.i(TAG, GET_VIDEO_CONTEXT);
        return new VideoContext(false, 0, "IDLE", 0,0,0);
    }

    @Override
    public void switchScene(int id) throws SwitchSceneFailureException {
        Log.i(TAG, SWITCH_SCENE);
    }

    @Override
    public void createWebrtcFeedbackConnection(String uri) throws CreateWebrtcFeedbackConnectionException {
        Socket signallingSocket = null;
        try {
            Log.i(TAG, CREATE_WEBRTC_FEEDBACK_CONNECTION);
            signallingSocket = new SocketSSLBuilder().setURL(uri).build();
            webrtcSignallingClientsMap.put(signallingSocket, new KontrollerWebrtcSignallingClient(signallingSocket, peerConnectionFactory, context));
        } catch (Exception e) {
            /** Catch exception and wrap it in #CreateWebrtcFeedbackConnectionException. */
            throw new CreateWebrtcFeedbackConnectionException(FAILED_TO_CREATE_WEBRTC_FEEDBACK_CONNECTION, e);
        }
    }

    @Override
    public void registerContextChangedListener(ContextChangedListener listener) {
        Log.i(TAG, REGISTER_CONTEXT_CHANGED_LISTENER);
        videoContextListenersList.add((ContextChangedListener<VideoContext>)listener);
    }

    @Override
    public void unregisterContextChangedListener(ContextChangedListener listener) {
        Log.i(TAG, UNREGISTER_CONTEXT_CHANGED_LISTENER);
        videoContextListenersList.remove((ContextChangedListener<VideoContext>)listener);
    }

    @Override
    public void registerVideoUploadStatusChangedListener(IVideoUploadStatusChangedListener listener) {
        Log.i(TAG, REGISTER_VIDEO_UPLOAD_STATUS_CHANGED_LISTENER);
        videoUploadStatusChangedListenersList.add(listener);
    }

    @Override
    public void unregisterVideoUploadStatusChangedListener(IVideoUploadStatusChangedListener listener) {
        Log.i(TAG, UNREGISTER_VIDEO_UPLOAD_STATUS_CHANGED_LISTENER);
        videoUploadStatusChangedListenersList.remove(listener);
    }

    @Override
    public void registerRecordErrorListener(IRecordErrorListener listener) {
        Log.i(TAG, REGISTER_RECORD_ERROR_LISTENER);
        recordErrorListenersList.add(listener);
    }

    @Override
    public void unregisterRecordErrorListener(IRecordErrorListener listener) {
        Log.i(TAG, UNREGISTER_RECORD_ERROR_LISTENER);
        recordErrorListenersList.remove(listener);
    }

    private PeerConnectionFactory createPeerConnectionFactory(Context context) {
        PeerConnectionFactory peerConnectionFactory = null;
        /** Initialize PeerConnectionFactory globals. */
        PeerConnectionFactory.InitializationOptions initializationOptions =
                PeerConnectionFactory.InitializationOptions.builder(context)
                        .createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);
        /** Create a new PeerConnectionFactory instance - using Hardware encoder and decoder. */
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(new PeerConnectionFactory.Options())
                .setVideoEncoderFactory(new DefaultVideoEncoderFactory(null,
                        true, true))
                .setVideoDecoderFactory(new DefaultVideoDecoderFactory(null))
                .createPeerConnectionFactory();
        return peerConnectionFactory;
    }
}
