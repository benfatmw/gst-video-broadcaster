package com.kalyzee.panel_connection_manager;


import static com.kalyzee.panel_connection_manager.mappers.ResponseType.ERROR;
import static com.kalyzee.panel_connection_manager.mappers.ResponseType.SUCCESS;
import static com.kalyzee.panel_connection_manager.mappers.video.WebrtcSignallingAction.ICE_CANDIDATE;
import static com.kalyzee.panel_connection_manager.mappers.video.WebrtcSignallingAction.SDP;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalyzee.kontroller_services_api.exceptions.video.IceExchangeFailureException;
import com.kalyzee.kontroller_services_api.exceptions.video.SdpExchangeFailureException;
import com.kalyzee.kontroller_services_api.exceptions.video.StartWebrtcFeedbackSessionException;
import com.kalyzee.kontroller_services_api.exceptions.video.StopWebrtcFeedbackSessionException;
import com.kalyzee.panel_connection_manager.exceptions.session.ConnectFailureException;
import com.kalyzee.panel_connection_manager.mappers.ErrorResponseContent;
import com.kalyzee.panel_connection_manager.mappers.RequestObject;
import com.kalyzee.panel_connection_manager.mappers.ResponseObject;
import com.kalyzee.panel_connection_manager.mappers.video.IceCandidateContent;
import com.kalyzee.panel_connection_manager.mappers.video.SdpContent;
import com.kalyzee.panel_connection_manager.mappers.video.StartWebrtcFeedbackSessionContent;
import com.kalyzee.panel_connection_manager.mappers.video.WebrtcSignallingAction;
import com.kalyzee.panel_connection_manager.mappers.video.WebrtcSignallingMessage;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public abstract class WebrtcSignallingClient {

    private static final String TAG = "WebrtcSignallingClient";

    private static final String WEBRTC_MESSAGE = "webrtc_message";

    private static final int TIMEOUT_S = 20;
    private static final String CORRELATION_ID = "correlation_id";

    private static final String CURRENT_THREAD_IS_INTERRUPTED = "The current thread is interrupted while it is waiting.";
    private static final String FAILED_TO_CONNECT_TO_SIGNALLING_SERVER_ENDPOINT = "Failed to connect to signalling server endpoint.";
    private static final String WEBRTC_SIGNALLING_SERVER_MESSAGE_RECEIVED = "Webrtc signalling server message received: ";
    private static final String FAILED_TO_DESERIALIZE_WEBRTC_SIGNALLING_SERVER_MESSAGE = "Failed to deserialize Webrtc signalling server message.";
    private static final String UNSUPPORTED_WEBRTC_SIGNALLING_REQUEST = "Unsupported Webrtc signalling request.";
    private static final String CAMERA_RESPONSE_SENT = "Camera response to webrtc signalling server: ";
    private static final String CAMERA_MESSAGE_SENT = "Camera message sent to webrtc signalling server: ";
    private static final String FAILED_TO_SEND_SDP_OFFER_TO_SIGNALLING_SERVER = "Failed to send SDP offer to signalling server.";
    private static final String FAILED_TO_SEND_ICE_CANDIDATE_TO_SIGNALLING_SERVER = "Failed to send Ice candidate to signalling server.";
    private static final String FAILED_TO_SERIALIZE_WEBRTC_SIGNALLING_CAMERA_MESSAGE = "Failed to serialize webrtc signalling camera message.";

    private Socket remoteSocket;

    public WebrtcSignallingClient(Socket socket) {
        this.remoteSocket = socket;
        connect();
    }

    private Emitter.Listener onSignallingServerRequest = new Emitter.Listener() {
        @Override
        public void call(final Object... objects) {
            Log.i(TAG, WEBRTC_SIGNALLING_SERVER_MESSAGE_RECEIVED + (String) objects[0].toString()
                    + " Socket id: " + remoteSocket.id());
            process(objects[0]);
        }
    };

    private Emitter.Listener onSignallingServerDisconnected = new Emitter.Listener() {
        @Override
        public void call(final Object... objects) {
            Log.i(TAG, "Socket " + remoteSocket.id() + " is disconnected.");
            try {
                stopWebrtcPreview();
            } catch (Exception e) {
                Log.e(TAG, "Failed to stop webrtc feedback session.", e);
            }
            remoteSocket.off(WEBRTC_MESSAGE, onSignallingServerRequest);
        }
    };

    private void connect() {
        final BlockingQueue<Object> values = new LinkedBlockingQueue<>();
        remoteSocket.on(WEBRTC_MESSAGE, onSignallingServerRequest);
        remoteSocket.on(Socket.EVENT_DISCONNECT, onSignallingServerDisconnected);
        remoteSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                values.offer(objects);
            }
        });
        remoteSocket.connect();
        /**
         * Wait for connection establishment.
         * In case the timeout is elapsed without a response from the Panel, throw #ConnectFailureException
         * In case of error, catch the exception and wrap it in #ConnectFailureException.
         */
        Object panelMsg = null;
        try {
            panelMsg = values.poll(TIMEOUT_S, TimeUnit.SECONDS);
            if (panelMsg == null) {
                throw new ConnectFailureException(FAILED_TO_CONNECT_TO_SIGNALLING_SERVER_ENDPOINT);
            }
        } catch (InterruptedException e) {
            throw new ConnectFailureException(CURRENT_THREAD_IS_INTERRUPTED, e);
        }
        Log.i(TAG, "Camera connected to Webrtc signalling server with success, socket id: " + remoteSocket.id());
    }

    private void disconnect() {
        remoteSocket.off(WEBRTC_MESSAGE, onSignallingServerRequest);
        remoteSocket.disconnect();
    }

    private void process(Object request) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JSONObject responseObject = null;
        WebrtcSignallingMessage signallingMessage = null;

        /** Get Webrtc signalling server message and deserialize it using gson */
        try {
            signallingMessage = objectMapper.readValue((String) request.toString(), WebrtcSignallingMessage.class);
        } catch (Exception e) {
            Log.e(TAG, FAILED_TO_DESERIALIZE_WEBRTC_SIGNALLING_SERVER_MESSAGE, e);
            return;
        }

        try {
            responseObject = execute(signallingMessage);
            if (responseObject != null) {
                /** Add the correlation id to #response_object */
                responseObject.put(CORRELATION_ID, signallingMessage.getCorrelationId());
                /** Send response to the panel */
                remoteSocket.emit(WEBRTC_MESSAGE, responseObject);
                Log.i(TAG, CAMERA_RESPONSE_SENT + responseObject.toString() + ", socket id: "
                        + remoteSocket.id());
            }
        } catch (JSONException | JsonProcessingException e) {
            Log.e(TAG, FAILED_TO_SERIALIZE_WEBRTC_SIGNALLING_CAMERA_MESSAGE, e);
        }

    }

    public JSONObject execute(WebrtcSignallingMessage signalling_msg) throws JSONException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String gsonResponse = null;
        try {
            switch (signalling_msg.getAction()) {
                case START:
                    /** Start Webrtc negotiation */
                    StartWebrtcFeedbackSessionContent iceServers =
                            objectMapper.readValue((String) signalling_msg.getContent().toString(),
                                    StartWebrtcFeedbackSessionContent.class);
                    startWebrtcPreview(iceServers);
                    break;
                case STOP:
                    /** Stop Webrtc feedback session */
                    stopWebrtcPreview();
                    break;
                case SDP:
                    onRemoteSdpReceived(objectMapper.readValue((String) signalling_msg.getContent().toString(),
                            SdpContent.class));
                    return null;
                case ICE_CANDIDATE:
                    IceCandidateContent ice = objectMapper.readValue((String) signalling_msg.getContent().toString(),
                            IceCandidateContent.class);
                    onRemoteIceCandidateReceived(ice);
                    return null;
                default:
                    Log.e(TAG, UNSUPPORTED_WEBRTC_SIGNALLING_REQUEST);
                    return null;
            }
            gsonResponse = objectMapper.writeValueAsString(new ResponseObject<Object>(SUCCESS, null,
                    null, null));
        } catch (Exception e) {
            Log.e(TAG, FAILED_TO_SERIALIZE_WEBRTC_SIGNALLING_CAMERA_MESSAGE, e);
            gsonResponse = objectMapper.writeValueAsString(new ResponseObject<ErrorResponseContent>(ERROR, null,
                    null, new ErrorResponseContent(ExceptionUtils.getStackTrace(e))));
        }
        return new JSONObject(gsonResponse);
    }

    protected void sendSdpOffer(SdpContent sdp) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            RequestObject<WebrtcSignallingAction, SdpContent> sdpOffer = new RequestObject<WebrtcSignallingAction, SdpContent>(null,
                    SDP, sdp, null, null);
            JSONObject responseObject = new JSONObject(objectMapper.writeValueAsString(sdpOffer));
            remoteSocket.emit(WEBRTC_MESSAGE, responseObject);
            Log.i(TAG, CAMERA_MESSAGE_SENT + responseObject.toString() + ", socket id: "
                    + remoteSocket.id());
        } catch (Exception e) {
            Log.e(TAG, FAILED_TO_SEND_SDP_OFFER_TO_SIGNALLING_SERVER + e);
        }
    }

    protected void sendIceCandidate(IceCandidateContent ice) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            RequestObject<WebrtcSignallingAction, IceCandidateContent> iceCandidate = new RequestObject<WebrtcSignallingAction, IceCandidateContent>(null,
                    ICE_CANDIDATE, ice, null, null);
            JSONObject responseObject = new JSONObject(objectMapper.writeValueAsString(iceCandidate));
            remoteSocket.emit(WEBRTC_MESSAGE, responseObject);
            Log.i(TAG, CAMERA_MESSAGE_SENT + responseObject.toString() + ", socket id: "
                    + remoteSocket.id());
        } catch (Exception e) {
            Log.e(TAG, FAILED_TO_SEND_ICE_CANDIDATE_TO_SIGNALLING_SERVER + e);
        }
    }

    public abstract void startWebrtcPreview(StartWebrtcFeedbackSessionContent iceServers) throws StartWebrtcFeedbackSessionException;

    public abstract void stopWebrtcPreview() throws StopWebrtcFeedbackSessionException;

    public abstract void onRemoteSdpReceived(SdpContent sdp) throws SdpExchangeFailureException;

    public abstract void onRemoteIceCandidateReceived(IceCandidateContent ice) throws IceExchangeFailureException;
}
