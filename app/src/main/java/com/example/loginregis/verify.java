package com.example.loginregis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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
        final String content = String.format("{\"userID\":\"%s\",\"verify\":\"%s\"}", id, ver.getText().toString());
        final String verifyurl = "http://140.113.123.58:8000/users/verify/";

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 將資料寫入資料庫
                StringBuilder djangorespone = new StringBuilder();

                HttpURLConnection djangoconnect = null;
                try {
                    URL url = new URL(verifyurl);
                    djangoconnect = (HttpURLConnection)url.openConnection();
                    djangoconnect.setRequestMethod("POST");
                    djangoconnect.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    djangoconnect.setRequestProperty("Accept", "application/json");
                    djangoconnect.setDoInput(true);
                    djangoconnect.setDoOutput(true);

                    OutputStream os = djangoconnect.getOutputStream();
                    DataOutputStream writer = new DataOutputStream(os);
                    writer.write(content.getBytes(StandardCharsets.UTF_8));
                    writer.flush();
                    writer.close();
                    os.close();

                    InputStream inputStream = djangoconnect.getInputStream();
                    int status = djangoconnect.getResponseCode();
                    Log.d("djangoresponse", String.valueOf(status));

                    if(status == 200){
                        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                        BufferedReader in = new BufferedReader(reader);

                        String line;
                        while ((line = in.readLine()) != null)
                            djangorespone.append(line).append("\n");
                        in.close();

                        String response;
                        response = new JSONObject(djangorespone.toString()).getString("status");
                        Log.v("django reponse", response);

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
                } catch (Exception e) {
                    verify.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(verify.this, "驗證失敗請檢查網路連線", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("registfailed", e.getLocalizedMessage());
                    e.printStackTrace();
                }finally {
                    if (djangoconnect != null)
                        djangoconnect.disconnect();
                }

            }
        }).start();
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}