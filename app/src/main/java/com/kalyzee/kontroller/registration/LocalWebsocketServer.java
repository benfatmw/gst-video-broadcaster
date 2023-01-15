package com.kalyzee.kontroller.registration;

import static com.kalyzee.kontroller.registration.mappers.MessageAction.GET_ID;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalyzee.kontroller.registration.mappers.ErrorResponseContent;
import com.kalyzee.kontroller.registration.mappers.GetDeviceIdResponseContent;
import com.kalyzee.kontroller.registration.mappers.RegisterRequestContent;
import com.kalyzee.kontroller.registration.mappers.MessageObject;
import com.kalyzee.kontroller.registration.mappers.RegisterResponseContent;
import com.kalyzee.panel_connection_manager.CredentialsManager;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;

import java.net.InetSocketAddress;

public class LocalWebsocketServer extends WebSocketServer {

    private static final String TAG = "LocalWebsocketServer";
    private static final String FAILED_TO_DESERIALIZE_FLASHER_TOOL_REQUEST =
            "Failed to deserialize flacher tool json request. Invalid JSON format.";
    private static final String DEVICE_IS_SUCCESSFULLY_REGISTERED = "Device is successfully registered.";
    private static final String FLASHER_TOOL_REQUEST_RECEIVED = "Flasher tool request received. Request details: ";
    private static final String FAILED_TO_PROCESS_THE_RECEIVED_MESSAGE =
            "Failed to process the received message.";
    private static final String DEVICE_RESPONSE_SENT = "Device response sent: ";

    private static final String LOCAL_SOCKET_OPENED = "Local websocket is successfully opened.";
    private static final String LOCAL_SOCKET_STARTED = "Local websocket is successfully started.";
    private static final String LOCAL_SOCKET_FAILED = "Local websocket error. Error cause: ";
    private static final String LOCAL_SOCKET_CLOSED = "Local websocket is closed";

    private final CredentialsManager credentialsManager;
    private final String ipAddress;
    private final String macAddress;


    public LocalWebsocketServer(String ipAddress,
                                String macAddress,
                                CredentialsManager credentialsManager) {
        super(new InetSocketAddress(ipAddress, 8000));
        this.credentialsManager = credentialsManager;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
    }

    @Override
    public void onClose(WebSocket socket, int arg1, String arg2, boolean arg3) {
        Log.e(TAG, LOCAL_SOCKET_CLOSED);
    }

    @Override
    public void onError(WebSocket socket, Exception errorCause) {
        Log.e(TAG, LOCAL_SOCKET_FAILED, errorCause);
    }

    @Override
    public void onStart() {
        Log.i(TAG, LOCAL_SOCKET_STARTED);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessage(WebSocket socket, String request) {
        String response = null;
        try {
            response = process(request);
            broadcast(response);
            Log.i(TAG, DEVICE_RESPONSE_SENT + response);
        } catch (Exception e) {
            Log.e(TAG, FAILED_TO_DESERIALIZE_FLASHER_TOOL_REQUEST + response, e);
        }
    }

    @Override
    public void onOpen(WebSocket socket, ClientHandshake arg1) {
        Log.i(TAG, LOCAL_SOCKET_OPENED);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String process(String request) throws JsonProcessingException, JSONException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String responseObject = null;
        MessageObject messageObject;

        Log.i(TAG, FLASHER_TOOL_REQUEST_RECEIVED + request);

        try {
            /** Get the flash tool request and deserialize it using jackson */
            messageObject = objectMapper.readValue(request, MessageObject.class);
            /** Dispatch the request to the appropriate executor based on the category field */
            switch (messageObject.getAction()) {
                case REGISTER:
                    RegisterRequestContent registerRequestParams = objectMapper.readValue(
                            objectMapper.writeValueAsString(messageObject.getParams()),
                            RegisterRequestContent.class);
                    registerCamera(registerRequestParams.getPanelUri(), registerRequestParams.getCertificate());
                    responseObject = objectMapper.writeValueAsString(new MessageObject<RegisterResponseContent>(
                            GET_ID, new RegisterResponseContent(DEVICE_IS_SUCCESSFULLY_REGISTERED)));
                    break;
                case GET_ID:
                    responseObject = objectMapper.writeValueAsString(new MessageObject<GetDeviceIdResponseContent>(
                            GET_ID, new GetDeviceIdResponseContent(macAddress)));
            }

        } catch (Exception e) {
            Log.e(TAG, FAILED_TO_PROCESS_THE_RECEIVED_MESSAGE, e);
            responseObject = objectMapper.writeValueAsString(new MessageObject<ErrorResponseContent>(
                    GET_ID, new ErrorResponseContent(FAILED_TO_PROCESS_THE_RECEIVED_MESSAGE
                    + ExceptionUtils.getStackTrace(e))));
        }
        return responseObject;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void registerCamera(String panelUri, String certificate) {
        credentialsManager.setDeviceRegistrationStatus(true);
        credentialsManager.setAllowFallback(false);
        credentialsManager.storeCredentials(panelUri, certificate);
    }
}
