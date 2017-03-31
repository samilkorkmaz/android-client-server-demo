package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Reference: Intermediate java tutorial, episodes 39-46, Bucky, YouTube
 *
 * @author skorkmaz, 2017
 */
public class ServerInterface {

    public static final int SERVER_PORT = 6789;
    public static final String HOST_NAME_LIST_FILE = "hostNameList.txt";
    private List<String> hostNameList = new ArrayList<>();
    private static final String DISCONNECT_MESSAGE = "CLIENT - END";

    public ServerInterface() {
    }

    public void startRunning() {
        try {
            //read previously connected ip addresses from file:
            if (new File(HOST_NAME_LIST_FILE).exists()) {
                System.out.println("Reading from file " + HOST_NAME_LIST_FILE);
                hostNameList = FileUtils.readFromTextFile(HOST_NAME_LIST_FILE);
            }
            ServerLogic.createServer();
            while (true) {
                try {
                    showMessage("Waiting for someone to connect...\n");
                    String hostName = ServerLogic.waitForConnection();
                    showMessage("Now connected to " + hostName);
                    String date = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z").format(new Date());
                    hostNameList.add(hostName + ", " + date);
                    FileUtils.writeToTextFile(HOST_NAME_LIST_FILE, hostNameList);
                    ServerLogic.setupStreams();
                    showMessage("\nStreams are now setup!\n");
                    try {
                        whileChatting();                        
                    } catch (IOException e) {
                        System.out.println("IOException in whileChatting()");
                    }
                } catch (EOFException e) {
                    showMessage("\nServer ended the connection!");
                } finally {
                    //save hostname list to file:
                    System.out.println("Writing to file " + HOST_NAME_LIST_FILE);
                    closeCrap();
                }
            }
        } catch (IOException e) {
            System.out.println("IOException in outer while loop");
            closeCrap();
            System.out.println("Exiting.");
            //throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * During the chat conversations.
     */
    private void whileChatting() throws IOException {
        String message = "You are now connected!";
        sendMessage(message);
        do {
            try {
                message = ServerLogic.getMessageFromClient();
                showMessage("\n" + message);
                Gson gson = new Gson();
                try {
                    MyData myData = gson.fromJson(message, MyData.class);
                    System.out.println("Name: " + myData.getName() + ", heights: " + Arrays.toString(myData.getHeights())
                            + ", isDisplayHostNames: " + myData.isIsDisplayHostNames());
                    if (myData.isIsDisplayHostNames()) {
                        for (int i = 0; i < hostNameList.size(); i++) {
                            System.out.printf("%d. %s\n", (i + 1), hostNameList.get(i));
                        }
                    }
                } catch (JsonSyntaxException e) {
                    System.out.println("Message from client was not a JSON string.");
                }
                sendMessage("Client sent: " + message);
            } catch (ClassNotFoundException e) {
                showMessage("\nI could not figure out what the client send!");
            }
        } while (!message.equals(DISCONNECT_MESSAGE));
    }

    /**
     * Close streams and sockets after you are done chatting.
     */
    private void closeCrap() {
        showMessage("\nClosing connections...\n");
        ServerLogic.closeCrap();
    }

    /**
     * Send a message to client.
     */
    private void sendMessage(String message) {
        try {
            ServerLogic.sendMessageToClient(message);
            showMessage("\nSERVER - " + message);
        } catch (IOException e) {
            showMessage("\nERROR: DUDE I COULDN'T SEND THAT MESSAGE!");
        }
    }

    /**
     * Updates chatWindow.
     *
     * @param text
     */
    private void showMessage(final String text) {
        System.out.println("SERVER: " + text);
    }

}
