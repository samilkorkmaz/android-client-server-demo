package samil.client;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.ConnectException;

import static samil.client.TcpClient.SERVER_IP;
import static samil.client.TcpClient.SERVER_PORT;
import static samil.client.TcpClient.TRYING_TO_CONNECT_MSG;

public class ClientActivity extends AppCompatActivity {
    TcpClient mTcpClient;
    TextView tvLog;
    TextView tvMessageFromClient;
    TextView tvMessageFromServer;
    Button btnConnect;
    Button btnDisconnect;
    Button btnSendMessageToServer;
    private static int btnClickCount = 0;
    private static final String DISCONNECT_MESSAGE = "CLIENT - END";
    private Animation anim = new AlphaAnimation(0.0f, 1.0f);
    private static final String CANNOT_CONNECT_TO_SERVER = "CANNOT_CONNECT_TO_SERVER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        tvLog = (TextView)findViewById(R.id.tvLog);
        tvMessageFromClient = (TextView)findViewById(R.id.tvMessageFromClient);
        tvMessageFromServer = (TextView)findViewById(R.id.tvMessageFromServer);
        btnConnect = (Button)findViewById(R.id.btnConnect);
        btnDisconnect = (Button)findViewById(R.id.btnDisconnect);
        btnSendMessageToServer = (Button)findViewById(R.id.btnSendMessageToServer);

        tvLog.setText(TRYING_TO_CONNECT_MSG);
        anim.setDuration(100); //You can manage the blinking time with this parameter
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        tvLog.startAnimation(anim);
        btnConnect.setEnabled(false);
        btnDisconnect.setEnabled(false);
        btnSendMessageToServer.setEnabled(false);
        new ConnectTask().execute("");
    }


    private void setIsConnected(boolean isConnected) {
        tvLog.clearAnimation();
        btnConnect.setEnabled(!isConnected);
        btnDisconnect.setEnabled(isConnected);
        btnSendMessageToServer.setEnabled(isConnected);
    }

    public void btnSendMessageToServerClick(View view) {
        //sends the message to the server
        if (mTcpClient != null) {
            Gson gson = new Gson();
            String json = gson.toJson(new MyData(++btnClickCount + ". msg", new double[]{1d, 2.5d, 3}, false));
            new SendMessageTask().execute(json);
            tvMessageFromClient.setText("Client: " + json);
        } else {
            tvMessageFromClient.setText("mTcpClient NULL");
        }
    }

    public void btnDisconnectClick(View view) {
        if (mTcpClient != null) {
            new SendMessageTask().execute(DISCONNECT_MESSAGE);
        } else {
            tvMessageFromClient.setText("mTcpClient NULL");
        }
    }

    public void btnConnectClick(View view) {
        tvLog.setText(TRYING_TO_CONNECT_MSG);
        tvLog.setAnimation(anim);
        btnConnect.setEnabled(false); //to prevent clicking it multiple times which would generate multiple tasks
        new ConnectTask().execute("");
    }

    public class ConnectTask extends AsyncTask<String, String, TcpClient> {

        @Override
        protected TcpClient doInBackground(String... message) {
            mTcpClient = new TcpClient();
            try {
                mTcpClient.connect();
                try {
                    mTcpClient.sendMessage("Hello from client.");
                    String msgFromServer = mTcpClient.getMessageFromServer();
                    publishProgress(msgFromServer);
                } catch (IOException e) {
                    Log.e(this.getClass().getSimpleName(), e.toString());
                } catch (ClassNotFoundException e) {
                    Log.e(this.getClass().getSimpleName(), e.toString());
                }
            } catch (ConnectException e) {
                publishProgress(CANNOT_CONNECT_TO_SERVER); //this method calls the onProgressUpdate which updates UI
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            updateUI(values[0]);
        }
    }

    public void updateUI(String msg) {
        Log.d(this.getClass().getSimpleName(), "message " + msg);
        //process server response here.
        if (msg.equals(CANNOT_CONNECT_TO_SERVER)) {
            tvLog.setText("FAILED to connect to " + SERVER_IP + ", port " + SERVER_PORT);
            setIsConnected(false);
        } if (msg.equals(DISCONNECT_MESSAGE))  {
            tvLog.clearAnimation();
            tvLog.setText("Disconnected from " + SERVER_IP + ", port " + SERVER_PORT);
            setIsConnected(false);
        }
        else {
            setIsConnected(true);
            tvLog.setText("Connected to " + SERVER_IP + ", port " + SERVER_PORT);
            tvMessageFromServer.setText("Server: " + msg);
        }
    }

    public class SendMessageTask extends AsyncTask<String, String, TcpClient> {

        @Override
        protected TcpClient doInBackground(String... message) {
            if (mTcpClient != null) {
                try {
                    String msgFromClient = message[0];
                    mTcpClient.sendMessage(msgFromClient);
                    if (msgFromClient.equals(DISCONNECT_MESSAGE)) {
                        publishProgress(msgFromClient);
                    } else {
                        try {
                            String msgFromServer = mTcpClient.getMessageFromServer();
                            publishProgress(msgFromServer);
                        } catch (IOException e) {
                            Log.e(this.getClass().getSimpleName(), e.toString());
                        } catch (ClassNotFoundException e) {
                            Log.e(this.getClass().getSimpleName(), e.toString());
                        }
                    }
                } catch (Exception e) {
                    Log.e(this.getClass().getSimpleName(), "sendMessage: Error", e);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            updateUI(values[0]);
        }
    }
}
