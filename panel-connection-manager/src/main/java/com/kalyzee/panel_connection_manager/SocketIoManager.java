package com.kalyzee.panel_connection_manager;


import static com.kalyzee.panel_connection_manager.mappers.RequestCategory.ADMIN;
import static com.kalyzee.panel_connection_manager.mappers.RequestCategory.SESSION;
import static com.kalyzee.panel_connection_manager.mappers.admin.AdminAction.REGISTER;
import static com.kalyzee.panel_connection_manager.mappers.session.SessionAction.LOGIN;
import static com.kalyzee.panel_connection_manager.mappers.session.SessionAction.LOGOUT;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kalyzee.panel_connection_manager.exceptions.admin.RegisterFailureException;
import com.kalyzee.panel_connection_manager.exceptions.session.ConnectFailureException;
import com.kalyzee.panel_connection_manager.exceptions.session.LoginFailureException;
import com.kalyzee.panel_connection_manager.exceptions.session.NotLoggedInException;
import com.kalyzee.panel_connection_manager.executors.PanelRequestsExecutor;
import com.kalyzee.panel_connection_manager.mappers.RequestObject;
import com.kalyzee.panel_connection_manager.mappers.ResponseObject;
import com.kalyzee.panel_connection_manager.mappers.ResponseType;
import com.kalyzee.panel_connection_manager.mappers.admin.AdminAction;
import com.kalyzee.panel_connection_manager.mappers.admin.RegisterRequestContent;
import com.kalyzee.panel_connection_manager.mappers.session.LoginRequestContent;
import com.kalyzee.panel_connection_manager.mappers.session.LoginResponseContent;
import com.kalyzee.panel_connection_manager.mappers.session.SessionAction;

import org.json.JSONObject;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketIoManager {

    private static final String TAG = "SocketIoManager";
    private static final String CAMERA_MESSAGE = "camera_message";

    private static final String NULL_INPUT_PARAMETER = "Null input parameter.";
    private static final String FAILED_TO_CONNECT_TO_PANEL_ENDPOINT = "Failed to connect to Panel endpoint.";
    private static final String TIMEOUT_ELAPSED_NO_RESPONSE_FOR_THE_SENT_REQUEST = "Timeout elapsed. No response for the sent request.";
    private static final String FAILED_TO_EMIT_EVENT_TO_PANEL_ENDPOINT = "Failed to emit event to Panel endpoint.";
    private static final String CURRENT_THREAD_IS_INTERRUPTED = "The current thread is interrupted while it is waiting.";
    private static final String UNABLE_TO_HANDLE_PANEL_REQUESTS_CAMERA_NOT_LOGGED_IN = "Unable to handle Panel requests. The camera is not logged in.";
    private static final String CAMERA_REGISTRATION_SUCCESS = "The camera is registered in the Panel with success.";
    private static final String CAMERA_LOGIN_SUCCESS = "The camera is logged in with success.";
    private static final String CAMERA_REGISTRATION_FAILURE = "The camera failed to register to the Panel (Registration refused).";
    private static final String CAMERA_LOGIN_FAILURE = "The camera failed to log to the Panel (Login refused).";
    private static final String WRONG_CORRELATION_ID = "Wrong received correlation ID.";

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

    private boolean isLoggedIn;

    public SocketIoManager(Socket socket,
                           PanelRequestsExecutor cameraRequestsExecutor,
                           PanelRequestsExecutor networkRequestsExecutor,
                           PanelRequestsExecutor videoRequestsExecutor,
                           PanelRequestsExecutor systemRequestsExecutor) {
        this.socket = socket;
        this.cameraRequestsExecutor = cameraRequestsExecutor;
        this.networkRequestsExecutor = networkRequestsExecutor;
        this.videoRequestsExecutor = videoRequestsExecutor;
        this.systemRequestsExecutor = systemRequestsExecutor;
    }

    /**
     * #panel_requests_queue Producer
     */
    private Emitter.Listener onPanelRequest = new Emitter.Listener() {
        @Override
        public void call(final Object... objects) {
            Log.i(TAG, "Panel request received, socket id:" + socket.id());
            panelRequestsQueue.offer(objects[0]);
        }
    };

    private Emitter.Listener onPanelDisconnected = new Emitter.Listener() {
        @Override
        public void call(final Object... objects) {
            Log.i(TAG, "Socket " + socket.id() + " is disconnected.");
            isLoggedIn = false;
            socket.off(CAMERA_MESSAGE, onPanelRequest);
            if (panelRequestsConsumer != null) {
                panelRequestsConsumer.terminate();
                panelRequestsConsumer = null;
            }
        }
    };

    public void register(RegisterRequestContent content) {

        /** Sanity check */
        if (content == null) {
            throw new RegisterFailureException(NULL_INPUT_PARAMETER);
        }

        final BlockingQueue<Object> values = new LinkedBlockingQueue<>();

        /**
         * Connect to the server endpoint
         */
        connect();

        /**
         * Construct register_request object to be sent to the server then emit register event to the server
         * In case of error, catch the exception and wrap it in #RegisterFailureException
         */
        Gson gson = new Gson();
        final String correlation_id = UUID.randomUUID().toString();
        try {
            RequestObject<AdminAction, RegisterRequestContent> register_request = new RequestObject<AdminAction, RegisterRequestContent>(ADMIN, REGISTER, content, correlation_id, null);
            socket.on(CAMERA_MESSAGE, new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    values.offer(objects[0]);
                }
            });
            socket.emit(CAMERA_MESSAGE, new JSONObject(gson.toJson(register_request)));
        } catch (Exception e) {
            throw new RegisterFailureException(FAILED_TO_EMIT_EVENT_TO_PANEL_ENDPOINT, e);
        }

        /**
         * Wait for the Panel response.
         * In case the timeout is elapsed without a response from the Panel, throw #RegisterFailureException
         * In case of error, catch the exception and wrap it in #RegisterFailureException.
         */
        Object panel_msg = null;
        try {
            panel_msg = values.poll(TIMEOUT_S, TimeUnit.SECONDS);
            if (panel_msg == null) {
                throw new RegisterFailureException(TIMEOUT_ELAPSED_NO_RESPONSE_FOR_THE_SENT_REQUEST);
            }
        } catch (InterruptedException e) {
            throw new RegisterFailureException(CURRENT_THREAD_IS_INTERRUPTED, e);
        }

        ResponseObject register_resp = gson.fromJson((String) panel_msg.toString(), ResponseObject.class);
        /** Check the correlation ID */
        if (!register_resp.getCorrelationId().equals(correlation_id)) {
            throw new RegisterFailureException(WRONG_CORRELATION_ID);
        }
        if (register_resp.getType() == ResponseType.ERROR) {
            throw new RegisterFailureException(CAMERA_REGISTRATION_FAILURE);
        }
        Log.i(TAG, CAMERA_REGISTRATION_SUCCESS);
        /** Disconnect from the server endpoint */
        disconnect();

    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void login(LoginRequestContent content) {

        /** Sanity check */
        if (content == null) {
            throw new RegisterFailureException(NULL_INPUT_PARAMETER);
        }

        final BlockingQueue<Object> values = new LinkedBlockingQueue<>();

        /** Connect to the server endpoint */
        connect();

        /**
         * Construct login_request object to be sent to the server then emit login event to the server
         * In case of error, catch the exception and wrap it in #LoginFailureException
         */
        Gson gson = new Gson();
        final String correlation_id = UUID.randomUUID().toString();
        try {
            RequestObject<SessionAction, LoginRequestContent> login_request = new RequestObject<SessionAction, LoginRequestContent>(SESSION, LOGIN, content, correlation_id, null);
            socket.on(CAMERA_MESSAGE, new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    values.offer(objects[0]);
                }
            });
            /** Emit event to the server */
            socket.emit(CAMERA_MESSAGE, new JSONObject(gson.toJson(login_request)));
        } catch (Exception e) {
            throw new LoginFailureException(FAILED_TO_EMIT_EVENT_TO_PANEL_ENDPOINT, e);
        }

        /**
         * Wait for the Panel response.
         * In case the timeout is elapsed without a response from the Panel, throw #RegisterFailureException
         * In case of error, catch the exception and wrap it in #LoginFailureException.
         */
        Object panel_msg = null;
        try {
            panel_msg = values.poll(TIMEOUT_S, TimeUnit.SECONDS);
            if (panel_msg == null) {
                throw new LoginFailureException(TIMEOUT_ELAPSED_NO_RESPONSE_FOR_THE_SENT_REQUEST);
            }
        } catch (InterruptedException e) {
            throw new LoginFailureException(CURRENT_THREAD_IS_INTERRUPTED, e);
        }

        ResponseObject<LoginResponseContent> login_resp = gson.fromJson((String) panel_msg.toString(), new TypeToken<ResponseObject<LoginResponseContent>>() {
        }.getType());
        /** Check the correlation ID */
        if (!login_resp.getCorrelationId().equals(correlation_id)) {
            throw new LoginFailureException(WRONG_CORRELATION_ID);
        }
        if (login_resp.getType() == ResponseType.ERROR) {
            throw new LoginFailureException(CAMERA_LOGIN_FAILURE);
        }
        Log.i(TAG, CAMERA_LOGIN_SUCCESS);
        this.authToken = login_resp.getContent().getAuthToken();
        isLoggedIn = true;
        socket.on(CAMERA_MESSAGE, onPanelRequest);
        Log.i(TAG, "Camera logged in with success, socket id: " + socket.id());
        socket.on(Socket.EVENT_DISCONNECT, onPanelDisconnected);
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
        panelRequestsConsumer = new PanelRequestsConsumer(panelRequestsQueue, socket,
                cameraRequestsExecutor,
                networkRequestsExecutor,
                videoRequestsExecutor,
                systemRequestsExecutor);
        executorService.execute(panelRequestsConsumer);
        Log.i(TAG, "handlePanelRequests: PanelRequestsConsumer is instantiated and launched, socket id: " + socket.id());
    }

    public void logout() {

        final BlockingQueue<Object> values = new LinkedBlockingQueue<>();
        Gson gson = new Gson();

        /**
         * Construct logout_request object to be sent to the server then emit logout event to the server
         * In case of error, catch the exception and wrap it in #LogoutFailureException
         */
        try {
            final String correlation_id = UUID.randomUUID().toString();
            RequestObject<SessionAction, Object> logout_request = new RequestObject<SessionAction, Object>(SESSION, LOGOUT, null, correlation_id, this.authToken);
            socket.on(CAMERA_MESSAGE, new Emitter.Listener() {
                @Override
                public void call(Object... objects) {
                    values.offer(objects[0]);
                }
            });
            /** Emit event to the server */
            socket.emit(CAMERA_MESSAGE, new JSONObject(gson.toJson(logout_request)));
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
        Object panel_msg = null;
        try {
            panel_msg = values.poll(TIMEOUT_S, TimeUnit.SECONDS);
            if (panel_msg == null) {
                throw new ConnectFailureException(FAILED_TO_CONNECT_TO_PANEL_ENDPOINT);
            }
        } catch (InterruptedException e) {
            throw new ConnectFailureException(CURRENT_THREAD_IS_INTERRUPTED, e);
        }
    }

    private void disconnect() {
        socket.disconnect();
    }

}