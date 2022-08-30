package com.kalyzee.panel_connection_manager;

import android.util.Log;

import com.google.gson.Gson;
import com.kalyzee.panel_connection_manager.executors.PanelRequestsExecutor;
import com.kalyzee.panel_connection_manager.mappers.RequestObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;

import io.socket.client.Socket;

public class PanelRequestsConsumer implements Runnable {

    private static final String TAG = "PanelRequestsConsumer";
    private static final Object POISON_PILL = new Object();
    private static final String CAMERA_MESSAGE = "camera_message";
    private static final String FAILED_TO_DESERIALIZE_PANEL_REQUEST = "Failed to deserialize Panel json request. Invalid JSON format.";
    private static final String FAILED_TO_SERIALIZE_CAMERA_RESPONSE = "Failed to serialize camera response.";
    private static final String UNSUPPORTED_REQUEST = "Unsupported request, ignore it!";
    private static final String CURRENT_THREAD_IS_INTERRUPTED = "The current thread is interrupted while it is waiting.";
    private static final String PANEL_REQUEST_RECEIVED = "Panel request received. Request details: ";
    private static final String CAMERA_RESPONSE_SENT = "Camera response sent. Response details: ";
    private static final String CORRELATION_ID = "correlation_id";

    private final BlockingQueue<Object> requestsQueue;

    private Socket socket;
    private PanelRequestsExecutor cameraRequestsExecutor;
    private PanelRequestsExecutor networkRequestsExecutor;
    private PanelRequestsExecutor videoRequestsExecutor;
    private PanelRequestsExecutor systemRequestsExecutor;

    private volatile boolean looping = true;

    public PanelRequestsConsumer(BlockingQueue<Object> requestsQueue, Socket socket,
                                 PanelRequestsExecutor cameraRequestsExecutor,
                                 PanelRequestsExecutor networkRequestsExecutor,
                                 PanelRequestsExecutor videoRequestsExecutor,
                                 PanelRequestsExecutor systemRequestsExecutor) {
        this.requestsQueue = requestsQueue;
        this.socket = socket;
        this.cameraRequestsExecutor = cameraRequestsExecutor;
        this.networkRequestsExecutor = networkRequestsExecutor;
        this.videoRequestsExecutor = videoRequestsExecutor;
        this.systemRequestsExecutor = systemRequestsExecutor;
    }

    @Override
    public void run() {
        /**
         * The Consumer object loops to retrieve requests from the queue.
         * It will break the loop and ended the thread
         * when it receives the marker object from the queue (POISON_PILL).
         */
        while (looping) {
            try {
                Object request = requestsQueue.take();
                if (request.equals(POISON_PILL)) {
                    Log.i(TAG, "Exiting consumer thread, end of data reached.");
                    break;
                }
                process(request);
            } catch (InterruptedException e) {
                Log.e(TAG, CURRENT_THREAD_IS_INTERRUPTED, e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                Log.e(TAG, UNSUPPORTED_REQUEST, e);
            }
        }
    }

    public void terminate() {
        Log.e(TAG, "Attempting to terminate/exit Panel requests consumer thread.");
        requestsQueue.offer(POISON_PILL);
        looping = false;
    }

    private void process(Object request) {

        Gson gson = new Gson();
        JSONObject response_object = null;
        RequestObject request_object = null;

        Log.i(TAG, PANEL_REQUEST_RECEIVED + (String) request.toString());

        /** Get Panel request and deserialize it using gson */
        try {
            request_object = gson.fromJson((String) request.toString(), RequestObject.class);
        } catch (Exception e) {
            Log.e(TAG, FAILED_TO_DESERIALIZE_PANEL_REQUEST, e);
            return;
        }

        try {
            /** Dispatch the request to the appropriate executor based on the category field */
            switch (request_object.getCategory()) {
                case VIDEO:
                    response_object = videoRequestsExecutor.execute(request_object.getAction().toString(), request_object.getContent());
                    break;
                case NETWORK:
                    response_object = networkRequestsExecutor.execute(request_object.getAction().toString(), request_object.getContent());
                    break;
                case CAMERA:
                    response_object = cameraRequestsExecutor.execute(request_object.getAction().toString(), request_object.getContent());
                    break;
                case SYSTEM:
                    response_object = systemRequestsExecutor.execute(request_object.getAction().toString(), request_object.getContent());
                    break;
                default:
                    Log.e(TAG, UNSUPPORTED_REQUEST);
                    return;
            }
            /** Add the correlation id to #response_object */
            response_object.put(CORRELATION_ID, request_object.getCorrelationId());
            /** Send response to the panel */
            socket.emit(CAMERA_MESSAGE, response_object);
            Log.i(TAG, CAMERA_RESPONSE_SENT + response_object.toString() + ", socket id:" + socket.id());
        } catch (JSONException e) {
            Log.e(TAG, FAILED_TO_SERIALIZE_CAMERA_RESPONSE, e);
        }

    }

}
