package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author skorkmaz
 */
public class ServerLogic {

    private static final int SERVER_PORT = 6789;
    private static ObjectOutputStream output;
    private static ObjectInputStream input;
    private static ServerSocket server;
    private static Socket connection;
    
    private ServerLogic() {        
    }

    public static void createServer() throws IOException {
        server = new ServerSocket(SERVER_PORT, 100);
    }

    /**
     * Wait for connection, then display connection information.
     */
    public static String waitForConnection() throws IOException {
        connection = server.accept();
        return connection.getInetAddress().getHostName();
    }

    /**
     * Get stream to send and receive data.
     */
    public static void setupStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
    }

    public static String getMessageFromClient() throws IOException, ClassNotFoundException {
        return (String) input.readObject();
    }

    /**
     * Send a message to client.
     */
    public static void sendMessageToClient(String message) throws IOException {
        output.writeObject("SERVER - " + message);
        output.flush();
    }

    /**
     * Close streams and sockets after you are done chatting.
     */
    public static void closeCrap() {
        try {
            output.close();
            input.close();
            connection.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
