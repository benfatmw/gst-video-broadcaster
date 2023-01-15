package com.kalyzee.visca_over_ip;

import static com.kalyzee.visca_over_ip.ViscaSpecification.SPEC_A;
import static com.kalyzee.visca_over_ip.ViscaSpecification.SPEC_B;

import android.util.Log;

import com.kalyzee.kontroller_services_api.dtos.camera.MoveDirection;
import com.kalyzee.kontroller_services_api.dtos.camera.ZoomType;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class ViscaCamera {

    private DatagramSocket socket;
    private InetAddress address;
    private int port;

    private static final byte[] PAN_TILT_UP_CMD = new byte[]{(byte) 0x81, (byte) 0x01, (byte) 0x06,
            (byte) 0x01, (byte) 0x07, (byte) 0x07, (byte) 0x03, (byte) 0x01, (byte) 0xFF};
    private static final byte[] PAN_TILT_DOWN_CMD = new byte[]{(byte) 0x81, (byte) 0x01, (byte) 0x06,
            (byte) 0x01, (byte) 0x07, (byte) 0x07, (byte) 0x03, (byte) 0x02, (byte) 0xFF};
    private static final byte[] PAN_TILT_LEFT_CMD = new byte[]{(byte) 0x81, (byte) 0x01, (byte) 0x06,
            (byte) 0x01, (byte) 0x07, (byte) 0x07, (byte) 0x01, (byte) 0x03, (byte) 0xFF};
    private static final byte[] PAN_TILT_RIGHT_CMD = new byte[]{(byte) 0x81, (byte) 0x01, (byte) 0x06,
            (byte) 0x01, (byte) 0x07, (byte) 0x07, (byte) 0x02, (byte) 0x03, (byte) 0xFF};
    private static final byte[] PAN_TILT_STOP_CMD = new byte[]{(byte) 0x81, (byte) 0x01, (byte) 0x06,
            (byte) 0x01, (byte) 0x07, (byte) 0x07, (byte) 0x03, (byte) 0x03, (byte) 0xFF};
    private static final byte[] ZOOM_ADD_CMD = new byte[]{(byte) 0x81, (byte) 0x01, (byte) 0x04,
            (byte) 0x07, (byte) 0x02, (byte) 0xFF};
    private static final byte[] ZOOM_DEC_CMD = new byte[]{(byte) 0x81, (byte) 0x01, (byte) 0x04,
            (byte) 0x07, (byte) 0x03, (byte) 0xFF};
    private static final byte[] ZOOM_STOP_CMD = new byte[]{(byte) 0x81, (byte) 0x01, (byte) 0x04,
            (byte) 0x07, (byte) 0x00, (byte) 0xFF};
    private static byte[] PRESET_SET_CMD = new byte[]{(byte) 0x81, (byte) 0x01, (byte) 0x04,
            (byte) 0x3F, (byte) 0x01, (byte) 0XFF, (byte) 0xFF};
    private static byte[] PRESET_CALL_CMD = new byte[]{(byte) 0x81, (byte) 0x01, (byte) 0x04,
            (byte) 0x3F, (byte) 0x02, (byte) 0XFF, (byte) 0xFF};

    public static ViscaSpecification currentSpec;

    public ViscaCamera(String address, int port) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket();
        socket.setSoTimeout(10000);
        this.address = InetAddress.getByName(address);
        this.port = port;
    }

    public void move(MoveDirection direction) throws IOException {
        switch (direction) {
            case UP:
                sendCommand(PAN_TILT_UP_CMD);
                break;
            case DOWN:
                sendCommand(PAN_TILT_DOWN_CMD);
                break;
            case LEFT:
                sendCommand(PAN_TILT_LEFT_CMD);
                break;
            case RIGHT:
                sendCommand(PAN_TILT_RIGHT_CMD);
                break;
        }
    }

    public void stopMoving() throws IOException {
        sendCommand(PAN_TILT_STOP_CMD);
    }

    public void zoom(ZoomType type) throws IOException {
        switch (type) {
            case ADD:
                sendCommand(ZOOM_ADD_CMD);
                break;
            case DEC:
                sendCommand(ZOOM_DEC_CMD);
                break;
        }
    }

    public void stopZooming() throws IOException {
        sendCommand(ZOOM_STOP_CMD);
    }

    public void setPresetView(int presetId) throws IOException {
        PRESET_SET_CMD[PRESET_SET_CMD.length - 2] = (byte) presetId;
        sendCommand(PRESET_SET_CMD);
    }

    public void moveToPresetView(int presetId) throws IOException {
        PRESET_CALL_CMD[PRESET_SET_CMD.length - 2] = (byte) presetId;
        sendCommand(PRESET_CALL_CMD);

    }

    public byte[] sendCommand(byte[] cmd) throws IOException {

        byte[] noReplyHeader = null;

        if (SPEC_A == currentSpec) {
            noReplyHeader = new byte[]{(byte) 0x82, (byte) 0x00};
        } else if (SPEC_B == currentSpec) {
            noReplyHeader = new byte[]{(byte) 0x01, (byte) 0x00,  (byte) 0x00};
            noReplyHeader[noReplyHeader.length-1] = (byte) cmd.length;
        } else {
            throw new IllegalArgumentException("Invalid visca specification.");
        }

        byte[] buf = new byte[noReplyHeader.length + cmd.length];
        System.arraycopy(noReplyHeader, 0, buf, 0, noReplyHeader.length);
        System.arraycopy(cmd, 0, buf, noReplyHeader.length, cmd.length);

        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        //socket.receive(packet);
        return packet.getData();
    }
}
