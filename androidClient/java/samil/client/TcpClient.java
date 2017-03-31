package samil.client;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by sam on 20.03.2017.
 */

public class TcpClient {
    public static final String SERVER_IP = "34.205.170.217";
    public static final int SERVER_PORT = 6789;
    public static final String TRYING_TO_CONNECT_MSG = "Trying to connect to ip " + SERVER_IP + ", port " + SERVER_PORT;
    private static ObjectOutputStream mBufferOut;
    private static ObjectInputStream mBufferIn;
    private static Socket socket;

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void sendMessage(String message) throws IOException {
        if (mBufferOut != null) {
            Log.d(this.getClass().getSimpleName(), "sending message");
            mBufferOut.writeObject(message);
            mBufferOut.flush();
        }
    }

    public String getMessageFromServer() throws IOException, ClassNotFoundException {
        return (String) mBufferIn.readObject();
    }

    public void connect() throws ConnectException {
        try {
            Log.d(this.getClass().getSimpleName(), TRYING_TO_CONNECT_MSG);
            Socket socket = new Socket(InetAddress.getByName(SERVER_IP), SERVER_PORT);
            try {
                //sends the message to the server
                mBufferOut = new ObjectOutputStream(socket.getOutputStream());
                //receives the message which the server sends back
                mBufferIn = new ObjectInputStream(socket.getInputStream());
            } catch (Exception e) {
                Log.e(this.getClass().getSimpleName(), "SSS: Error", e);
            }
        } catch (ConnectException e) {
            Log.e(this.getClass().getSimpleName(), "Cannot connect to server. " + e.toString());
            throw new ConnectException(e.toString());
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "CCC: Error", e);
        }
    }

}
