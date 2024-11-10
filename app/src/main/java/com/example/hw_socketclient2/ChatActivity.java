package com.example.hw_socketclient2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    private Socket socket;
    private BufferedReader reader;
    private OutputStreamWriter outputSW;
    private TextView tvMessages, tvWelcomeMessage;
    private EditText etMessage;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvMessages = findViewById(R.id.tvMessages);
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);
        etMessage = findViewById(R.id.etMessage);
        Button btnSend = findViewById(R.id.btnSend);
        Button btnLeave = findViewById(R.id.btnLeave);

        name = getIntent().getStringExtra("name");
        String ip = getIntent().getStringExtra("ip");
        int port = getIntent().getIntExtra("port", 6100);

        if (name != null) {
            tvWelcomeMessage.setText("Hi, " + name);
        }

        new Thread(() -> {
            try {
                socket = new Socket(ip, port);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                outputSW = new OutputStreamWriter(socket.getOutputStream());

                outputSW.write(name + "\n");
                outputSW.flush();

                String tmp;
                while ((tmp = reader.readLine()) != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(tmp);
                        String senderName = jsonObj.getString("name");
                        String msg = jsonObj.getString("msg");

                        runOnUiThread(() -> {
                            if (!senderName.equals(name)) {
                                tvMessages.append(senderName + "[" + getCurrentTime() + "]: " + msg + "\n");
                            }
                        });
                    } catch (JSONException e) {
                        final String textMessage = tmp;
                        runOnUiThread(() -> tvMessages.append(textMessage + "\n"));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("SocketClient", "Error: " + e.getMessage());
            }
        }).start();

        btnSend.setOnClickListener(v -> sendMessage(etMessage.getText().toString().trim()));

        btnLeave.setOnClickListener(v -> {
            sendMessage(name + "[" + getCurrentTime() + "]: has left");
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
                finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void sendMessage(String message) {
        if (!message.isEmpty()) {
            runOnUiThread(() -> {
                tvMessages.append("Me[" + getCurrentTime() + "]: " + message + "\n");
                etMessage.setText("");
            });

            new Thread(() -> {
                try {
                    JSONObject json = new JSONObject();
                    json.put("name", name);
                    json.put("msg", message);

                    if (outputSW != null) {
                        outputSW.write(json.toString() + "\n");
                        outputSW.flush();
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    Log.e("SocketClient", "Error in sendMessage: " + e.getMessage());
                }
            }).start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }
}
