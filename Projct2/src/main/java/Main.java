package main.java;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length >= 1 && args[0].equals("-h")) {
            runReceiverMode();
        } else if (args.length >= 1 && args[0].equals("-s")) {
            runSenderMode();
        } else {
            System.exit(1);
        }
    }

    private static void runReceiverMode() throws IOException {
        RReceiveUDP receiver = new RReceiveUDP();
        configureReceiver(receiver);
        receiver.receiveFile();
        receiver.close();
    }

    private static void configureReceiver(RReceiveUDP receiver) {
        receiver.setMode(1);
        receiver.setModeParameter(512);
        receiver.setFilename("winter_novel.txt");
        receiver.setLocalPort(32456);
    }

    private static void runSenderMode() throws IOException {
        RSendUDP sender = new RSendUDP();
        configureSender(sender);
        sender.sendFile();
        sender.close();
    }

    private static void configureSender(RSendUDP sender) {
        sender.setMode(1);
        sender.setModeParameter(512);
        sender.setTimeout(10000);
        sender.setFilename("manuscript.txt");
        sender.setLocalPort(23456);
        sender.setReceiver(new InetSocketAddress("localhost", 32456));
    }
}
