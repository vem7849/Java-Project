import java.io.*;
import java.net.*;

/**
 * A simple server application that listens for incoming connections
 * on port 8000 and responds to incoming messages with a simple greeting.
 */
public class Main {
    /**
     * The main method of the server application.
     * It creates a ServerSocket and listens for incoming connections indefinitely.
     * Upon receiving a connection, it delegates the handling to the handleConnection method.
     *
     * @param args command-line arguments (not used)
     * @throws IOException if an I/O error occurs while waiting for a connection
     */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8000);
        System.out.println("Server started. Listening for incoming connections...");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("Incoming connection from " + socket.getInetAddress());

            // Handle the incoming connection
            handleConnection(socket);
        }
    }

    /**
     * Handles an incoming connection by reading messages from the client
     * and responding with a simple greeting.
     *
     * @param socket the Socket representing the connection to the client
     * @throws IOException if an I/O error occurs while reading from or writing to the socket
     */
    private static void handleConnection(Socket socket) throws IOException {
        // Read from the socket
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            // Processing received data
            System.out.println("Received: " + line);
        }

        // Write to the socket
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        writer.println("Hello from the server!");
    }
}
