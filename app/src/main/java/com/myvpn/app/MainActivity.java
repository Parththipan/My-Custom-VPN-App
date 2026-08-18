package com.myvpn.app;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private MaterialButton btnConnect;
    private TextView txtStatus, txtNotice;
    private boolean isConnected = false;
    private String activeConfig = "vless://f9806d26-485b-4eb1-b005-b03d65076bab@premium.dulacloud.store:443?encryption=none&type=tcp&headerType=none&security=tls&fp=chrome&sni=m.zoom.us#443DULA-DZOOM";

    // Remote Admin JSON URL (Fixed with your Gist URL)
    private final String ADMIN_CONFIG_URL = "https://gist.githubusercontent.com/Parththipan/012e0abd019c274652c1fccba849ade0/raw/ed070a4c7dff816820f2b53298cf8e317ee1de07/vpn_config.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConnect = findViewById(R.id.btnConnect);
        txtStatus = findViewById(R.id.txtStatus);
        txtNotice = findViewById(R.id.txtNotice);

        fetchAdminConfig();

        btnConnect.setOnClickListener(v -> {
            if (!isConnected) {
                txtStatus.setText("Status: Connected (Zoom Mode)");
                txtStatus.setTextColor(Color.parseColor("#22c55e"));
                btnConnect.setText("DISCONNECT");
                btnConnect.setBackgroundColor(Color.parseColor("#ef4444"));
                isConnected = true;
            } else {
                txtStatus.setText("Status: Disconnected");
                txtStatus.setTextColor(Color.parseColor("#94a3b8"));
                btnConnect.setText("CONNECT");
                btnConnect.setBackgroundColor(Color.parseColor("#3b82f6"));
                isConnected = false;
            }
        });
    }

    private void fetchAdminConfig() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(ADMIN_CONFIG_URL).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String data = response.body().string();
                        JSONObject json = new JSONObject(data);
                        if (json.has("server_config")) {
                            activeConfig = json.getString("server_config");
                        }
                        runOnUiThread(() -> {
                            txtStatus.setText("Status: Ready (Cloud Config Loaded)");
                            if (json.has("notice")) {
                                txtNotice.setText(json.getString("notice"));
                            }
                        });
                    } catch (Exception ignored) {}
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> txtStatus.setText("Status: Ready (Default Config)"));
            }
        });
    }
}
