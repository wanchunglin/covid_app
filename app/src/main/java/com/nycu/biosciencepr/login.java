package com.nycu.biosciencepr;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.R;

public class login extends AppCompatActivity {

    EditText stuid;
    EditText pas;
    Button confirmLogin;
    String token = "";
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch disp;
    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        stuid = findViewById(R.id.stid);
        stuid.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        pas = findViewById(R.id.editTextPassword7);
        disp = findViewById(R.id.loginShowPwdSwitch);
        final FirebaseApp firebaseApp = FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("token failed", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();

                        // Log and toast
                        Log.d("token", token);
                    }
                });
        SharedPreferences pref = getSharedPreferences("loginInfo", MODE_PRIVATE);
        if (pref.getBoolean("loggedIn", false)){
            // the user is already logged in

            Log.v("login status", "the user is already logged in");
            stuid.setText(pref.getString("userID", ""));
            pas.setText(pref.getString("userPwd", ""));
            token = pref.getString("token","");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 將資料寫入資料庫
                    String loginurl = "http://140.113.79.132:8000/users/login/";

                    String content = String.format("{\"userID\":\"%s\",\"password\":\"%s\",\"token\":\"%s\"}", stuid.getText().toString(), pas.getText().toString(),token);
                    Log.d("payload", content);
                    djangocon connect = new djangocon();
                    String response = loginRequestToServer(loginurl, content, connect);

                    if (response != null){
                        try {
                            response = new JSONObject(response).getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        checkResponse(response);
                    }
                }
            }).start();
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent();
            intent.setClass(login.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void forget_password(View view){
        Intent intent = new Intent();
        intent.setClass(login.this, forget_password.class);
        startActivity(intent);
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
        final FirebaseApp firebaseApp = FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("token failed", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();

                        // Log and toast
                        Log.d("token", token);
                    }
                });
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 將資料寫入資料庫
                String loginurl = "http://140.113.79.132:8000/users/login/";
                String content = String.format("{\"userID\":\"%s\",\"password\":\"%s\",\"token\":\"%s\"}", stuid.getText().toString(), pas.getText().toString(),token);

                djangocon connect = new djangocon();
                String response = loginRequestToServer(loginurl, content, connect);

                if (response != null){
                    try {
                        response = new JSONObject(response).getString("status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    checkResponse(response);
                }
            }
        }).start();
    }

    private String loginRequestToServer(String loginurl, String content, djangocon connect){
        Map<String, String> property = new HashMap<>();
        property.put("Content-Type", "application/json; charset=UTF-8");
        property.put("Accept", "application/json");
        String response = null;
        // 將資料寫入資料庫
        try {
            response = connect.connection(loginurl, "POST", property, content, null);
        } catch (IOException e) {
            login.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(login.this, "登入失敗請檢查網路連線", Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
        }
        return response;
    }

    private void checkResponse(String response){
        if(response.contains("ok")){
            login.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(login.this, "成功!", Toast.LENGTH_SHORT).show();
                }
            });

            // 登入成功後，將使用者ID, PWD暫存，下次開啟App就不需再進行登入
            SharedPreferences pref = getSharedPreferences("loginInfo", MODE_PRIVATE);
            pref.edit().putBoolean("loggedIn", true).apply();
            pref.edit().putString("userID", stuid.getText().toString()).apply();
            pref.edit().putString("userPwd", pas.getText().toString()).apply();
            pref.edit().putString("token", token).apply();

            Intent intent = new Intent();
            intent.setClass(login.this, QRcode.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", stuid.getText().toString());
            intent.putExtras(bundle);
            startActivity(intent);
        }
        else if(response.contains("fail")){
            login.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(login.this, "學號或密碼有誤", Toast.LENGTH_SHORT).show();
                }
            });
        }

        else if(response.contains("not verify")){
            login.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(login.this, "尚未驗證完成喔~~", Toast.LENGTH_SHORT).show();
                }
            });
            Intent intent = new Intent();
            intent.setClass(login.this, verify.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", stuid.getText().toString());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}