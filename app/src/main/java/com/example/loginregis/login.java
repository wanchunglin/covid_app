package com.example.loginregis;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONObject;

import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;

public class login extends AppCompatActivity {

    EditText stuid ;
    EditText pas ;
    ImageView ivCode;
    Switch disp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        stuid = (EditText) findViewById(R.id.stid);
        stuid.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        pas = (EditText) findViewById(R.id.editTextPassword7);
        ivCode = (ImageView) findViewById(R.id.ivCode);
        disp = (Switch)  findViewById(R.id.switch1);
    }
    public void display(View view){
        if(disp.getText().toString().equals("顯示")){
            pas.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            disp.setText("隱藏")  ;
        } else{
            pas.setTransformationMethod(PasswordTransformationMethod.getInstance()); ;
            disp.setText("顯示")  ;
        }
    }
    public void fin(View view) {
        final String content = String.format("{\"userID\":\"%s\",\"password\":\"%s\"}", stuid.getText().toString(), pas.getText().toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 將資料寫入資料庫
                String loginurl = "http://140.113.123.58:8000/users/login/";
                StringBuilder djangorespone = new StringBuilder();

                HttpURLConnection djangoconnect = null;
                try {
                    URL url = new URL(loginurl);
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
                            login.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(login.this, "學號或密碼有誤", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else if(response.contains("ok")){
                            SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            dff.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                            final String encodeinfo = stuid.getText().toString() + dff.format(new Date());
                            final Hashtable hints = new Hashtable();
                            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                            BarcodeEncoder encoder = new BarcodeEncoder();
                            final Bitmap bit = encoder.encodeBitmap(encodeinfo, BarcodeFormat.QR_CODE, 1000, 1000,hints);
                            login.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    ivCode.setImageBitmap(bit);
                                    Toast.makeText(login.this, "成功!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    login.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(login.this, "登入失敗請檢查網路連線", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("loginfailed", e.getLocalizedMessage());
                    e.printStackTrace();
                }finally {
                    if (djangoconnect != null)
                        djangoconnect.disconnect();
                }

            }
        }).start();
    }

}