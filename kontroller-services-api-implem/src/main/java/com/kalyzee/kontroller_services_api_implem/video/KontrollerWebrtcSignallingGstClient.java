package com.kalyzee.kontroller_services_api_implem.video;

import com.kalyzee.kontroller_services_api.exceptions.video.IceExchangeFailureException;
import com.kalyzee.kontroller_services_api.exceptions.video.SdpExchangeFailureException;
import com.kalyzee.kontroller_services_api.exceptions.video.StartWebrtcFeedbackSessionException;
import com.kalyzee.kontroller_services_api.exceptions.video.StopWebrtcFeedbackSessionException;
import com.kalyzee.panel_connection_manager.WebrtcSignallingClient;
import com.kalyzee.panel_connection_manager.mappers.video.IceCandidateContent;
import com.kalyzee.panel_connection_manager.mappers.video.SdpContent;
import com.kalyzee.panel_connection_manager.mappers.video.StartWebrtcFeedbackSessionContent;

import org.freedesktop.gstreamer.pipeline.CameraStreamPipeline;
import org.freedesktop.gstreamer.pipeline.WebrtcSignallingMessagesListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import io.socket.client.Socket;

public class KontrollerWebrtcSignallingGstClient extends WebrtcSignallingClient {

    private static final String TAG = "KontrollerWebrtcSignalling";
    private static final String NULL_INPUT_PARAMETER = "Null input parameter.";
    private final static String FAILED_TO_START_WEBRTC_FEEDBACK_SESSION = "Failed to start Webrtc feedback session.";
    private final static String FAILED_TO_STOP_WEBRTC_FEEDBACK_SESSION = "Failed to stop Webrtc feedback session.";
    private final static String FAILED_TO_SET_REMOTE_SDP = "Failed to set remote sdp.";
    private final static String FAILED_TO_SET_REMOTE_ICE = "Failed to set remote ice.";

    private final int id = (int) (new Date().getTime()/1000);
    private final CameraStreamPipeline cameraStreamPipeline;

    public KontrollerWebrtcSignallingGstClient(Socket remoteSocket, CameraStreamPipeline cameraStreamPipeline) {
        super(remoteSocket);
        this.cameraStreamPipeline = cameraStreamPipeline;
    }

    public void startWebrtcPreview(StartWebrtcFeedbackSessionContent content) throws StartWebrtcFeedbackSessionException {
        if (content == null) {
            throw new StartWebrtcFeedbackSessionException(NULL_INPUT_PARAMETER);
        }
        try {
            WebrtcSignallingMessagesListener webrtcSignallingMessagesListener = new WebrtcSignallingMessagesListener() {
                @Override
                public void onIceCandidate(int sdpMLineIndex, String candidate) {
                    sendIceCandidate(new IceCandidateContent(sdpMLineIndex, candidate, null));
                }

                @Override
                public void onSdpCreated(String type, String sdp) {
                    sendSdpOffer(new SdpContent(type, sdp));
                }
            };
            cameraStreamPipeline.startPreview(id, content.getStuns(), content.getTurns(),
                    webrtcSignallingMessagesListener);
        } catch (Exception e) {
            throw new StartWebrtcFeedbackSessionException(FAILED_TO_START_WEBRTC_FEEDBACK_SESSION, e);
        }
    }

    public void stopWebrtcPreview() throws StopWebrtcFeedbackSessionException {
        try {
            cameraStreamPipeline.stopPreview(id);
        } catch (Exception e) {
            throw new StopWebrtcFeedbackSessionException(FAILED_TO_STOP_WEBRTC_FEEDBACK_SESSION, e);
        }
    }

    public void onRemoteSdpReceived(SdpContent sdp) throws SdpExchangeFailureException {
        try {
            cameraStreamPipeline.setRemoteDescription(id, sdp.getType(), sdp.getSdp());
        } catch (Exception e) {
            throw new SdpExchangeFailureException(FAILED_TO_SET_REMOTE_SDP, e);
        }
    }

    public void onRemoteIceCandidateReceived(IceCandidateContent ice) throws IceExchangeFailureException {
        if (ice == null) {
            return;
        }
        try {
            cameraStreamPipeline.addIceCandidate(id, ice.getSdpMLineIndex(), ice.getCandidate());
        } catch (Exception e) {
            throw new IceExchangeFailureException(FAILED_TO_SET_REMOTE_ICE, e);
        }
    }
}
