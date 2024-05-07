package main.java;

import edu.utulsa.unet.RReceiveUDPI;
import edu.utulsa.unet.UDPSocket;


import java.io.Closeable;

import java.io.FileWriter;
import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import java.nio.ByteBuffer;

import java.util.LinkedList;


public class RReceiveUDP implements Closeable, RReceiveUDPI {
    private String fileName;
    private int localPort = 12987;
    private int mode = 0;
    private long modeParameter = 256;

    private static final int DEFAULT_TIMEOUT = 10_000;

    public static byte[] generateAck(int sequenceNumber) {
        return ByteBuffer.allocate(4).putInt(sequenceNumber).array();
    }

    public void close() throws IOException {}

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

    public static byte[] formatACK(int seq) {

        byte[] msg = "ACK".getBytes();
        ByteBuffer ackBuff = ByteBuffer.allocate(msg.length );
        ackBuff.put(msg);
        return ackBuff.array();

    }
    public boolean receiveFile() {
        try {
            DatagramSocket socket = new UDPSocket(localPort);
            String fileName = getFilename();
            FileWriter writer = new FileWriter(fileName);
            LinkedList<DatagramPacket> packets = new LinkedList<>();

            if (getMode() == 1) {
                // Sliding Window mode
                byte[] buffer = new byte[socket.getSendBufferSize()];
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

                while (true) {
                    socket.receive(receivedPacket);
                    packets.add(receivedPacket);
                    socket.send(new DatagramPacket(generateAck(packets.size()), 4, receivedPacket.getAddress(), receivedPacket.getPort()));
                    break;
                }
            } else {
                // Stop-and-Wait mode
                int sequenceNumber = 0;
                while (true) {
                    byte[] buffer = new byte[socket.getSendBufferSize()];
                    DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(receivedPacket);
                    packets.add(receivedPacket);
                    writer.write(new String(receivedPacket.getData(), 4, receivedPacket.getLength() - 4));
                    socket.send(new DatagramPacket(generateAck(sequenceNumber), 4, receivedPacket.getAddress(), receivedPacket.getPort()));
                    sequenceNumber++;
                    if (sequenceNumber == packets.size())
                        break;
                }
            }

            writer.close();
            return true;
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
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
        localPort = port;
        return true;
    }

    public boolean setMode(int mode) {
        if (mode < 0 || mode > 1) {
            return false;
        }
        this.mode = mode;
        return true;
    }

    public boolean setModeParameter(long parameter) {
        if (parameter <= 0) {
            return false;
        }
        modeParameter = parameter;
        return true;
    }

    private DatagramPacket receive(DatagramSocket socket, byte[] buffer) {
        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.setSoTimeout(DEFAULT_TIMEOUT);
            socket.receive(packet);
            return packet;
        } catch (IOException e) {
            return null;
        }
    }
}
