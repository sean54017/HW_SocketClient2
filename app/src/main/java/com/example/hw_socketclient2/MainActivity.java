package com.example.hw_socketclient2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etName, etIP, etPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etIP = findViewById(R.id.etIP);
        etPort = findViewById(R.id.etPort);
        Button btnConnect = findViewById(R.id.btnConnect);

        btnConnect.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String ip = etIP.getText().toString().trim();
            String port = etPort.getText().toString().trim();

            if (!name.isEmpty() && !ip.isEmpty() && !port.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("ip", ip);
                intent.putExtra("port", Integer.parseInt(port));
                startActivity(intent);
            }
        });
    }
}
