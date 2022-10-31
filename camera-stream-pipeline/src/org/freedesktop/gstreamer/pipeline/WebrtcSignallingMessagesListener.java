package org.freedesktop.gstreamer.pipeline;

public interface WebrtcSignallingMessagesListener {
    void onIceCandidate(int sdpMLineIndex, String candidate);

    void onSdpCreated(String type, String sdp);

}
