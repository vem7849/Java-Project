package main.java;

import edu.utulsa.unet.RSendUDPI;
import edu.utulsa.unet.UDPSocket;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Queue;

public class RSendUDP implements Closeable, RSendUDPI {

    private String fileName;
    private int localPort = 12987;
    private int mode = 0;
    private long modeParameter = 256;
    private InetSocketAddress receiver = new InetSocketAddress("localhost", localPort);
    private long timeout = 1000;

    public static byte[] formatPacket(String message, int sequenceNumber, int frameSize) {
        ByteBuffer buffer = ByteBuffer.allocate(frameSize);
        buffer.putInt(sequenceNumber);
        buffer.putInt(message.length());
        buffer.put(message.getBytes(), 0, Math.min(frameSize - Integer.BYTES * 2, message.length()));
        return buffer.array();
    }

    public void close() throws IOException {
    }

    public String getFilename() {
        return fileName;
    }

    public int getLocalPort() {
        return localPort;
    }

    public int getMode() {
        return mode;
    }

    public long getModeParameter() {
        return modeParameter;
    }

    public InetSocketAddress getReceiver() {
        return receiver;
    }

    public long getTimeout() {
        return timeout;
    }

    public boolean sendFile() {
        try (DatagramSocket socket = connect()) {
            Path filePath = Path.of(fileName);
            byte[] fileData = Files.readAllBytes(filePath);
            int frameSize = socket.getSendBufferSize();
            Queue<DatagramPacket> sentPackets = new ArrayDeque<>();
            int sequenceNumber = 0;

            while (true) {
                byte[] packetData = formatPacket(new String(fileData), sequenceNumber, frameSize);
                DatagramPacket packet = new DatagramPacket(packetData, packetData.length, receiver);
                sentPackets.add(packet);
                send(socket, packetData);

                if (sentPackets.size() > modeParameter || Files.readAllBytes(filePath).length <= sequenceNumber * (frameSize - Integer.BYTES * 2)) {
                    waitForACK(socket, sentPackets);
                }

                if (Files.readAllBytes(filePath).length <= sequenceNumber * (frameSize - Integer.BYTES * 2)) {
                    break;
                }

                sequenceNumber++;
            }
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setFilename(String filename) {
        this.fileName = filename;
    }

    public boolean setLocalPort(int port) {
        if (port < 0 || port > 65535) {
            return false;
        }
        this.localPort = port;
        this.receiver = new InetSocketAddress("localhost", localPort);
        return true;
    }

    public boolean setMode(int mode) {
        if (mode != 0 && mode != 1) {
            return false;
        }
        this.mode = mode;
        return true;
    }

    public boolean setModeParameter(long modeParameter) {
        if (modeParameter <= 0) {
            return false;
        }
        this.modeParameter = modeParameter;
        return true;
    }

    public boolean setReceiver(InetSocketAddress receiver) {
        this.receiver = receiver;
        return true;
    }

    public boolean setTimeout(long timeout) {
        if (timeout <= 0) {
            return false;
        }
        this.timeout = timeout;
        return true;
    }

    private DatagramSocket connect() throws IOException {
        return new UDPSocket(localPort);
    }

    private void send(DatagramSocket socket, byte[] data) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, data.length, receiver);
        socket.send(packet);
    }

    private void waitForACK(DatagramSocket socket, Queue<DatagramPacket> sentPackets) throws IOException, InterruptedException {
        byte[] buffer = new byte[3];
        DatagramPacket ackPacket = new DatagramPacket(buffer, buffer.length);
        socket.setSoTimeout((int) timeout);
        socket.receive(ackPacket);
        sentPackets.remove();
    }
}
