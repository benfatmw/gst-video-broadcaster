package com.kalyzee.visca_over_ip;

import android.util.Log;

import com.kalyzee.kontroller_services_api.dtos.camera.MoveDirection;
import com.kalyzee.kontroller_services_api.dtos.camera.ZoomType;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

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

    private static byte[] NO_REPLY_HEADER = new byte[]{(byte) 0x82, (byte) 0x00};

    public ViscaCamera(String address, int port) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket();
        socket.setSoTimeout(10000);
        this.address = InetAddress.getByName(address);
        this.port = port;
    }

    public void move(MoveDirection direction) throws IOException {
        byte[] packet = null;
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
        sendCommand(packet);
    }

    public void stopMoving() throws IOException {
        sendCommand(PAN_TILT_STOP_CMD);
    }

    public void zoom(ZoomType type) throws IOException {
        byte[] packet = null;
        switch (type) {
            case ADD:
                sendCommand(ZOOM_ADD_CMD);
                break;
            case DEC:
                sendCommand(ZOOM_DEC_CMD);
                break;
        }
        sendCommand(packet);
    }

    public void stopZooming() throws IOException {
        sendCommand(ZOOM_STOP_CMD);
    }

    public void setPresetView(int presetId) throws IOException {
        PRESET_SET_CMD[PRESET_SET_CMD.length-2]= (byte) presetId;
        sendCommand(PRESET_SET_CMD);
    }

    public void moveToPresetView(int presetId) throws IOException {
        PRESET_CALL_CMD[PRESET_SET_CMD.length-2]= (byte) presetId;
        sendCommand(PRESET_CALL_CMD);

    }

    /**
     * According to the documentation:
     * |------packet (3-16 bytes)---------|
     * header     message      terminator
     * (1 byte)  (1-14 bytes)  (1 byte)
     * | X | X . . . . .  . . . . . X | X |
     * header:                  terminator:
     * 1 s2 s1 s0 0 r2 r1 r0     0xff
     * with r,s = recipient, sender msb first
     * for broadcast the header is 0x88!
     * we use -1 as recipient to send a broadcast!
     */
    public byte[] sendCommand(byte[] cmd) throws IOException {

        byte[] buf = new byte[NO_REPLY_HEADER.length + cmd.length];
        System.arraycopy(NO_REPLY_HEADER, 0, buf, 0, NO_REPLY_HEADER.length);
        System.arraycopy(cmd, 0, buf, NO_REPLY_HEADER.length, cmd.length);

        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        //socket.receive(packet);
        return packet.getData();
    }
}
