package com.kalyzee.kontroller_services_api_implem.video;


import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.kalyzee.kontroller_services_api.exceptions.video.IceExchangeFailureException;
import com.kalyzee.kontroller_services_api.exceptions.video.SdpExchangeFailureException;
import com.kalyzee.kontroller_services_api.exceptions.video.StartWebrtcFeedbackSessionException;
import com.kalyzee.kontroller_services_api.exceptions.video.StopWebrtcFeedbackSessionException;
import com.kalyzee.panel_connection_manager.WebrtcSignallingClient;
import com.kalyzee.panel_connection_manager.mappers.video.IceCandidateContent;
import com.kalyzee.panel_connection_manager.mappers.video.SdpContent;
import com.kalyzee.panel_connection_manager.mappers.video.StartWebrtcFeedbackSessionContent;


import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;

public class KontrollerWebrtcSignallingClient extends WebrtcSignallingClient {

    private static final String TAG = "KastWebrtcSignalling";
    private static final String CREATE_CAMERA_CAPTURER = "Create a Camera capturer.";
    private static final String NULL_INPUT_PARAMETER = "Null input parameter.";
    private final static String FAILED_TO_START_WEBRTC_FEEDBACK_SESSION = "Failed to start Webrtc feedback session.";
    private final static String FAILED_TO_STOP_WEBRTC_FEEDBACK_SESSION = "Failed to stop Webrtc feedback session.";
    private final static String FAILED_TO_SET_REMOTE_SDP = "Failed to set remote sdp.";
    private final static String FAILED_TO_SET_REMOTE_ICE = "Failed to set remote ice.";

    private final static int VIDEO_RESOLUTION_WIDTH = 1024;
    private final static int VIDEO_RESOLUTION_HEIGHT = 720;
    private final static int FPS = 30;

    private final PeerConnectionFactory peerConnectionFactory;
    private final Context context;

    private PeerConnection localPeer;
    private VideoCapturer videoCapturerAndroid;

    public KontrollerWebrtcSignallingClient(Socket remoteSocket, PeerConnectionFactory peerConnectionFactory, Context context) {
        super(remoteSocket);
        this.peerConnectionFactory = peerConnectionFactory;
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void startWebrtcPreview(StartWebrtcFeedbackSessionContent content) throws StartWebrtcFeedbackSessionException {
        if (content == null) {
            throw new StartWebrtcFeedbackSessionException(NULL_INPUT_PARAMETER);
        }
        try {
            List<PeerConnection.IceServer> peerIceServers = new ArrayList<>();
            for (String turn : content.getTurns()) {
                String url = "turn:" + turn.substring(turn.indexOf("@") + 1, turn.length());
                String credentials = turn.substring(turn.indexOf("//") + 2, turn.indexOf("@"));
                String username = credentials.substring(0, credentials.lastIndexOf(":"));
                String password = credentials.substring(credentials.lastIndexOf(":") + 1, credentials.length());
                PeerConnection.IceServer peerIceServer = PeerConnection.IceServer.builder(url)
                        .setUsername(Uri.decode(username))
                        .setPassword(password)
                        .createIceServer();
                peerIceServers.add(peerIceServer);
            }

            for (String stun : content.getStuns()) {
                String url = stun.substring(stun.indexOf(":") + 1, stun.lastIndexOf(":"));
                PeerConnection.IceServer peerIceServer = PeerConnection.IceServer.builder("stun:" + url).createIceServer();
                peerIceServers.add(peerIceServer);
            }
            startPreview(peerIceServers);
        } catch (Exception e) {
            throw new StartWebrtcFeedbackSessionException(FAILED_TO_START_WEBRTC_FEEDBACK_SESSION, e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void stopWebrtcPreview() throws StopWebrtcFeedbackSessionException {
        try {
            if (videoCapturerAndroid != null) {
                videoCapturerAndroid.stopCapture();
            }
            if (localPeer != null) {
                localPeer.close();
            }
            localPeer = null;
        } catch (Exception e) {
            throw new StopWebrtcFeedbackSessionException(FAILED_TO_STOP_WEBRTC_FEEDBACK_SESSION, e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onRemoteSdpReceived(SdpContent sdp) throws SdpExchangeFailureException {
        try {
            localPeer.setRemoteDescription(new SdpObserverImplem("localSetRemote"),
                    new SessionDescription(SessionDescription.Type.ANSWER, sdp.getSdp()));
        } catch (Exception e) {
            throw new SdpExchangeFailureException(FAILED_TO_SET_REMOTE_SDP, e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onRemoteIceCandidateReceived(IceCandidateContent ice) throws IceExchangeFailureException {
        if (ice == null) {
            return;
        }
        try {
            localPeer.addIceCandidate(new IceCandidate(ice.getSdpMid(), ice.getSdpMLineIndex(), ice.getCandidate()));
        } catch (Exception e) {
            throw new IceExchangeFailureException(FAILED_TO_SET_REMOTE_ICE, e);
        }
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();
        /** Front facing camera not found, try something else */
        Log.i(TAG, CREATE_CAMERA_CAPTURER);
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        return null;
    }

    private void startPreview(List<PeerConnection.IceServer> peerIceServers) {

        VideoSource videoSource = null;
        VideoTrack localVideoTrack = null;
        AudioSource audioSource = null;
        AudioTrack localAudioTrack = null;

        PeerConnection.RTCConfiguration rtcConfig =
                new PeerConnection.RTCConfiguration(peerIceServers);
        /**
         * TCP candidates are only useful when connecting to a server that supports
         * ICE-TCP.
         */
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
        /** Use ECDSA encryption. */
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA;
        localPeer = peerConnectionFactory.createPeerConnection(rtcConfig, new PeerConnectionObserverImplem("localPeerCreation") {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                sendIceCandidate(new IceCandidateContent(iceCandidate.sdpMLineIndex,
                        iceCandidate.sdp,
                        iceCandidate.sdpMid));
            }
        });

        /** Now create a VideoCapturer instance. */
        videoCapturerAndroid = createCameraCapturer(new Camera1Enumerator(false));

        /** create an VideoSource instance */
        SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", null);
        videoSource = peerConnectionFactory.createVideoSource(videoCapturerAndroid.isScreencast());
        videoCapturerAndroid.initialize(surfaceTextureHelper, context, videoSource.getCapturerObserver());
        localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource);

        /** create an AudioSource instance */
        MediaConstraints audioConstraints = new MediaConstraints();
        audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);

        videoCapturerAndroid.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);

        /** Create local media stream and added the stream to the local peer */
        MediaStream stream = peerConnectionFactory.createLocalMediaStream("102");
        stream.addTrack(localAudioTrack);
        stream.addTrack(localVideoTrack);
        localPeer.addStream(stream);

        MediaConstraints sdpConstraints = new MediaConstraints();
        sdpConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        sdpConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        localPeer.createOffer(new SdpObserverImplem("localCreateOffer") {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);
                localPeer.setLocalDescription(new SdpObserverImplem("localSetLocalDesc"), sessionDescription);
                sendSdpOffer(new SdpContent(sessionDescription.type.canonicalForm(), sessionDescription.description));
            }
        }, sdpConstraints);
    }


}