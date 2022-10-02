package com.kalyzee.panel_connection_manager;



import static com.kalyzee.panel_connection_manager.mappers.RequestCategory.SESSION;
import static com.kalyzee.panel_connection_manager.mappers.session.LoginErrorCode.UNAUTHORIZED;
import static com.kalyzee.panel_connection_manager.mappers.session.SessionAction.LOGIN;
import static com.kalyzee.panel_connection_manager.mappers.session.SessionAction.LOGOUT;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kalyzee.panel_connection_manager.exceptions.session.ConnectFailureException;
import com.kalyzee.panel_connection_manager.exceptions.session.LoginConnectionFailureException;
import com.kalyzee.panel_connection_manager.exceptions.session.LoginInvalidServerResponseException;
import com.kalyzee.panel_connection_manager.exceptions.session.LoginServerInternalErrorException;
import com.kalyzee.panel_connection_manager.exceptions.session.LoginUnauthorizedAccessException;
import com.kalyzee.panel_connection_manager.exceptions.session.NotLoggedInException;
import com.kalyzee.panel_connection_manager.executors.PanelRequestsExecutor;
import com.kalyzee.panel_connection_manager.mappers.ErrorResponseContent;
import com.kalyzee.panel_connection_manager.mappers.RequestObject;
import com.kalyzee.panel_connection_manager.mappers.ResponseObject;
import com.kalyzee.panel_connection_manager.mappers.ResponseType;
import com.kalyzee.panel_connection_manager.mappers.session.LoginErrorCode;
import com.kalyzee.panel_connection_manager.mappers.session.LoginRequestContent;
import com.kalyzee.panel_connection_manager.mappers.session.LoginResponseContent;
import com.kalyzee.panel_connection_manager.mappers.session.SessionAction;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Session {

    private static final String TAG = "Session";
    private static final String CAMERA_MESSAGE = "camera_message";

    private static final String NULL_INPUT_PARAMETER = "Null input parameter.";
    private static final String FAILED_TO_CONNECT_TO_PANEL_ENDPOINT = "Failed to connect to Panel endpoint.";
    private static final String TIMEOUT_ELAPSED_NO_RESPONSE_FOR_THE_SENT_REQUEST =
            "Timeout elapsed. No response for the sent request.";
    private static final String FAILED_TO_EMIT_EVENT_TO_PANEL_ENDPOINT = "Failed to emit event to Panel endpoint.";
    private static final String CURRENT_THREAD_IS_INTERRUPTED = "The current thread is interrupted while it is waiting.";
    private static final String UNABLE_TO_HANDLE_PANEL_REQUESTS_CAMERA_NOT_LOGGED_IN =
            "Unable to handle Panel requests. The camera is not logged in.";
    private static final String CAMERA_LOGIN_SUCCESS = "The camera is logged in with success. Socket id: ";
    private static final String CAMERA_LOGIN_FAILURE = "The camera failed to log to the Panel (Login refused).";
    private static final String CAMERA_SOCKET_DISCONNECTED = "The camera is disconnected. Socket id: ";
    private static final String PANEL_REQUEST_RECEIVED = "Panel request received, socket id: ";
    private static final String PANEL_REQUESTS_CONSUMER_LAUNCHED = "handlePanelRequests: " +
            "PanelRequestsConsumer is instantiated and launched, socket id: ";
    private static final String WRONG_CORRELATION_ID = "Wrong received correlation ID.";
    private static final String FAILED_TO_DESERIALIZE_PANEL_REQUEST =
            "Failed to deserialize Panel json request. Invalid JSON format.";
    private static final String LOGIN_UNAUTHORIZED = "Authentication is unauthorized. " +
            "Attempted login with wrong or obsolete credentials.";
    private static final String LOGIN_FAILED_DUE_TO_SERVER_INTERNAL_ERROR =
            "Login failed due to server internal error. Must retry later.";
    private static final String DEVICE_ALREADY_CONNECTED = "Attempt to connect already-connected socket.";
    private static final String DEVICE_ALREADY_DISCONNECTED = "Attempt to disconnect already-disconnected socket.";
    private static final String START_HANDLING_REMOTE_REQUESTS = "Start handling remote requests. socket id: ";

    private static final int TIMEOUT_S = 20;

    private final BlockingQueue<Object> panelRequestsQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private PanelRequestsConsumer panelRequestsConsumer;
    private String authToken;

    private Socket socket;
    private PanelRequestsExecutor cameraRequestsExecutor;
    private PanelRequestsExecutor networkRequestsExecutor;
    private PanelRequestsExecutor videoRequestsExecutor;
    private PanelRequestsExecutor systemRequestsExecutor;
    private PanelRequestsExecutor adminRequestsExecutor;

    private boolean isLoggedIn;

    public Session(Socket socket,
                   PanelRequestsExecutor cameraRequestsExecutor,
                   PanelRequestsExecutor networkRequestsExecutor,
                   PanelRequestsExecutor videoRequestsExecutor,
                   PanelRequestsExecutor systemRequestsExecutor,
                   PanelRequestsExecutor adminRequestsExecutor) {
        this.socket = socket;
        this.cameraRequestsExecutor = cameraRequestsExecutor;
        this.networkRequestsExecutor = networkRequestsExecutor;
        this.videoRequestsExecutor = videoRequestsExecutor;
        this.systemRequestsExecutor = systemRequestsExecutor;
        this.adminRequestsExecutor = adminRequestsExecutor;
    }

    /**
     * #panelRequestsQueue Producer
     */
    private Emitter.Listener onPanelRequest = new Emitter.Listener() {
        @Override
        public void call(final Object... objects) {
            Log.i(TAG, PANEL_REQUEST_RECEIVED + socket.id());
            panelRequestsQueue.offer(objects[0]);
        }
    };

    private Emitter.Listener onPanelDisconnected = new Emitter.Listener() {
        @Override
        public void call(final Object... objects) {
            Log.i(TAG, CAMERA_SOCKET_DISCONNECTED + socket.id());
            isLoggedIn = false;
            socket.off(CAMERA_MESSAGE, onPanelRequest);
            if (panelRequestsConsumer != null) {
                panelRequestsConsumer.terminate();
                panelRequestsConsumer = null;
            }
        }
    };

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void login(LoginRequestContent content) throws UnsupportedEncodingException, JsonProcessingException {

        /** Sanity check */
        if (content == null) {
            throw new NullPointerException(NULL_INPUT_PARAMETER);
        }

        final BlockingQueue<Object> values = new LinkedBlockingQueue<>();

        /** Connect to the server endpoint */
        connect();

        /**
         * Construct login_request object to be sent to the server then emit login event to the server
         * In case of error, catch the exception and wrap it in #LoginFailureException
         */
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        final String correlationId = UUID.randomUUID().toString();
        try {
            RequestObject<SessionAction, LoginRequestContent> loginRequest =
                    new RequestObject<SessionAction, LoginRequestContent>(SESSION, LOGIN, content, correlationId, null);
            socket.on(CAMERA_MESSAGE, new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    values.offer(objects[0]);
                }
            });
            /** Emit event to the server */
            socket.emit(CAMERA_MESSAGE, new JSONObject(objectMapper.writeValueAsString(loginRequest)));
        } catch (Exception e) {
            throw new LoginConnectionFailureException(FAILED_TO_EMIT_EVENT_TO_PANEL_ENDPOINT, e);
        }

        /**
         * Wait for the Panel response.
         * In case the timeout is elapsed without a response from the Panel, throw #LoginConnectionFailureException
         * In case of error, catch the exception and wrap it in #LoginFailureException.
         */
        Object panelMsg = null;
        try {
            panelMsg = values.poll(TIMEOUT_S, TimeUnit.SECONDS);
            if (panelMsg == null) {
                throw new LoginConnectionFailureException(TIMEOUT_ELAPSED_NO_RESPONSE_FOR_THE_SENT_REQUEST);
            }
        } catch (InterruptedException e) {
            throw new LoginConnectionFailureException(CURRENT_THREAD_IS_INTERRUPTED, e);
        }

        /** Get Panel request and deserialize it using gson */
        ResponseObject loginResp;
        try {
            loginResp = objectMapper.readValue(panelMsg.toString(), ResponseObject.class);
        } catch (Exception e) {
            throw new LoginInvalidServerResponseException(FAILED_TO_DESERIALIZE_PANEL_REQUEST, e);
        }

        /** Check the correlation ID */
        if (!loginResp.getCorrelationId().equals(correlationId)) {
            throw new LoginInvalidServerResponseException(WRONG_CORRELATION_ID);
        }

        /** Check the type (ERROR/SUCCESS) */
        if (loginResp.getType() == ResponseType.ERROR) {
            ErrorResponseContent loginErrorResp = objectMapper.readValue(objectMapper.writeValueAsString(loginResp.getContent()),
                    ErrorResponseContent.class);
            /** Map the error code into a custom exception */
            if (LoginErrorCode.value(loginErrorResp.getErrorCode()) == UNAUTHORIZED) {
                throw new LoginUnauthorizedAccessException(LOGIN_UNAUTHORIZED);
            } else {
                throw new LoginServerInternalErrorException(LOGIN_FAILED_DUE_TO_SERVER_INTERNAL_ERROR);
            }
        } else {
            LoginResponseContent loginRespContent = objectMapper.readValue(objectMapper.writeValueAsString(loginResp.getContent()),
                    LoginResponseContent.class);
            this.authToken = loginRespContent.getAuthToken();
            isLoggedIn = true;
            socket.on(CAMERA_MESSAGE, onPanelRequest);
            Log.i(TAG, CAMERA_LOGIN_SUCCESS + socket.id());
            socket.on(Socket.EVENT_DISCONNECT, onPanelDisconnected);
        }
    }

    /**
     * This method is called only if the camera is successfully logged in/authenticated to the Panel.
     * --> Start listening to Panel requests
     */
    public void handlePanelRequests() {

        /** Check if the camera is already logged */
        if (!isLoggedIn) {
            throw new NotLoggedInException(UNABLE_TO_HANDLE_PANEL_REQUESTS_CAMERA_NOT_LOGGED_IN);
        }
        Log.i(TAG, START_HANDLING_REMOTE_REQUESTS);
        panelRequestsConsumer = new PanelRequestsConsumer(panelRequestsQueue, socket,
                cameraRequestsExecutor,
                networkRequestsExecutor,
                videoRequestsExecutor,
                systemRequestsExecutor,
                adminRequestsExecutor);
        executorService.execute(panelRequestsConsumer);
        Log.i(TAG, PANEL_REQUESTS_CONSUMER_LAUNCHED + socket.id());
    }

    public void logout() {

        final BlockingQueue<Object> values = new LinkedBlockingQueue<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        /**
         * Construct logout_request object to be sent to the server then emit logout event to the server
         * In case of error, catch the exception and wrap it in #LogoutFailureException
         */
        try {
            final String correlationId = UUID.randomUUID().toString();
            RequestObject<SessionAction, Object> logoutRequest = new RequestObject<SessionAction, Object>(SESSION,
                    LOGOUT, null, correlationId, this.authToken);
            socket.on(CAMERA_MESSAGE, new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    values.offer(objects[0]);
                }
            });
            /** Emit event to the server */
            socket.emit(CAMERA_MESSAGE, new JSONObject(objectMapper.writeValueAsString(logoutRequest)));
            /**
             * Wait 2s before closing the socket so that the server doesn't miss the logout message
             */
            Thread.sleep(2000);
        } catch (Exception e) {
            Log.e(TAG, FAILED_TO_EMIT_EVENT_TO_PANEL_ENDPOINT);
        }

        /**
         * The camera is successfully logged out
         * --> Stop listening to Panel requests
         */
        executorService.shutdown();
        disconnect();
        socket.off(CAMERA_MESSAGE, onPanelRequest);
        isLoggedIn = false;
    }

    private void connect() {
        /** Check the socket state */
        if (socket.connected()) {
            Log.i(TAG, DEVICE_ALREADY_CONNECTED);
            return;
        }

        final BlockingQueue<Object> values = new LinkedBlockingQueue<>();
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                values.offer(objects);
            }
        });
        socket.connect();
        /**
         * Wait for connection establishment.
         * In case the timeout is elapsed without a response from the Panel, throw #ConnectFailureException
         * In case of error, catch the exception and wrap it in #ConnectFailureException.
         */
        Object remoteMsg = null;
        try {
            remoteMsg = values.poll(TIMEOUT_S, TimeUnit.SECONDS);
            if (remoteMsg == null) {
                throw new ConnectFailureException(FAILED_TO_CONNECT_TO_PANEL_ENDPOINT);
            }
        } catch (InterruptedException e) {
            throw new ConnectFailureException(CURRENT_THREAD_IS_INTERRUPTED, e);
        }
    }

    private void disconnect() {
        /** Check the socket state */
        if (!socket.connected()) {
            Log.i(TAG, DEVICE_ALREADY_DISCONNECTED);
            return;
        }
        socket.disconnect();
    }

}
