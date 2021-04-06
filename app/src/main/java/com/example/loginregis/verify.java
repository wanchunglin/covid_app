package com.example.loginregis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class verify extends AppCompatActivity {
    EditText ver ;
    Button f1;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        ver = findViewById(R.id.editTextNumberPassword3);
        f1 = findViewById(R.id.button5);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
    }


    public void verifying(View view)  {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 將資料寫入資料庫
                String response = null;
                String content = String.format("{\"userID\":\"%s\",\"verify\":\"%s\"}", id, ver.getText().toString());
                String verifyurl = "http://140.113.123.58:8000/users/verify/";
                djangocon connect = new djangocon();
                Map<String, String> property = new HashMap<>();
                property.put("Content-Type", "application/json; charset=UTF-8");
                property.put("Accept", "application/json");

                try {
                    response = connect.connection(verifyurl, "POST", property, content.toString(), null);
                } catch (IOException e) {
                    verify.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(verify.this, "驗證失敗請檢查網路連線", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                    return;
                }

                try {
                    assert response != null;
                    response = new JSONObject(response).getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(response.contains("fail")){
                    verify.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(verify.this, "驗證碼錯誤", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else if(response.contains("ok")){
                    verify.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(verify.this, "驗證成功!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent intent = new Intent();
                    intent.setClass(verify.this, login.class);
                    startActivity(intent);
                }
            }
        }).start();
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}