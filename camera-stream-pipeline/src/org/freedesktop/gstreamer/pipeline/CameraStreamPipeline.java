package org.freedesktop.gstreamer.pipeline;

import android.content.Context;
import android.util.Log;

import com.kalyzee.kontroller_services_api.dtos.video.LiveProfile;

import org.freedesktop.gstreamer.GStreamer;

import java.util.HashMap;

public class CameraStreamPipeline {
    public static final String TAG = "RtspToWebrtc";

    private native void nativeInit();     // Initialize native code, build pipeline, etc

    private native void nativeFinalize(); // Destroy pipeline and shutdown native code

    private native void nativePlay();     // Set pipeline to PLAYING

    private native void nativePause();    // Set pipeline to PAUSED

    private static native boolean nativeClassInit(); // Initialize native class: cache Method IDs for callbacks

    private native long nativeStartPreview(int id, String[] stuns, String[] turns);

    private native void nativeStopPreview(int id);

    private native void nativeSetRemoteDescription(int id, String type, String sdp);

    private native void nativeAddIceCandidate(int id, int sdpMLineIndex, String candidate);

    private native void nativeStartStream(int id, LiveProfile liveProfile);

    private native void nativeStopStream(int id);

    private long nativeCustomData;      // Native code will use this to keep private data

    private static HashMap<Integer, WebrtcSignallingMessagesListener> webrtcSignallingMessagesListenersMap
            = new HashMap<Integer, WebrtcSignallingMessagesListener>();

    /**
     * Called when the activity is first created.
     */
    public CameraStreamPipeline(Context context) {
        /** Initialize GStreamer and warn if it fails */
        try {
            GStreamer.init(context);
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Gstreamer", e);
            return;
        }
        nativeInit();
    }

    public long startPreview(int id, String[] stuns, String[] turns,
                             WebrtcSignallingMessagesListener webrtcSignallingMessagesListener) {
        webrtcSignallingMessagesListenersMap.put(id, webrtcSignallingMessagesListener);
        return nativeStartPreview(id, stuns, turns);
    }

    public void stopPreview(int id) {
        nativeStopPreview(id);
        webrtcSignallingMessagesListenersMap.remove(id);
    }

    public void setRemoteDescription(int id, String type, String sdp) {
        nativeSetRemoteDescription(id, type, sdp);
    }

    public void addIceCandidate(int id, int sdpMLineIndex, String candidate) {
        nativeAddIceCandidate(id, sdpMLineIndex, candidate);
    }

    public void startStream(int id, LiveProfile liveProfile) {
        nativeStartStream(id, liveProfile);
    }

    public void stopStream(int id) {
        nativeStopStream(id);
    }

    /**
     * Called from native code. Native code calls this once it has created its pipeline and
     * the main loop is running, so it is ready to accept commands.
     */
    private void onGStreamerInitialized() {
        Log.i("GStreamer", "Gst initialized.");
        nativePlay();
    }

    private void onIceCandidate(int id, int sdpMLineIndex, String candidate) {
        WebrtcSignallingMessagesListener webrtcSignallingMessagesListener =
                webrtcSignallingMessagesListenersMap.get(id);
        webrtcSignallingMessagesListener.onIceCandidate(sdpMLineIndex, candidate);
    }

    private void onSdpCreated(int id, String type, String sdp) {
        WebrtcSignallingMessagesListener webrtcSignallingMessagesListener =
                webrtcSignallingMessagesListenersMap.get(id);
        webrtcSignallingMessagesListener.onSdpCreated(type, sdp);
    }

    static {
        System.loadLibrary("gstreamer_android");
        System.loadLibrary("camera_stream_pipeline");
        if (!nativeClassInit())
            Log.e(TAG, "nativeClassInit() failed!!!");
    }

}
