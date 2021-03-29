package com.example.loginregis;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class register extends AppCompatActivity {
    EditText stuid  ;
    EditText name ;
    EditText email ;
    EditText phone ;
    EditText password ;
    Switch disp;
    ProgressBar spinner ;
    Handler handler = new Handler();


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        spinner = findViewById(R.id.progressBar7);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.editTextPhone);
        email = findViewById(R.id.mail);
        stuid = findViewById(R.id.editTextNumberPassword);
        stuid.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        password = findViewById(R.id.editTextTextPassword7);
        disp = findViewById((R.id.switch3));
    }


    public void display(View view){
        if(disp.getText().toString().equals("顯示")){
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            disp.setText("隱藏")  ;
        } else{
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            disp.setText("顯示")  ;
        }
    }
    public void finish(View view) {
        String[] data = {"\"userName\": ","\"phone\": ","\"email\": ","\"userID\": ","\"password\": "};
        data[0] = data[0] + '"' + name.getText().toString() + '"';
        data[1] = data[1] + '"' + phone.getText().toString() + '"';
        data[2] = data[2] + '"' + email.getText().toString() + '"';
        data[3] = data[3] + '"' + stuid.getText().toString() + '"';
        data[4] = data[4] + '"' + password.getText().toString() + '"';

        final String registerurl = "http://140.113.123.58:8000/users/register/";

        for (int i = 0;i<5;i++)
            Log.d(String.valueOf(i),data[i]);

        final StringBuilder content = new StringBuilder("{");

        for (int i = 0;i < 5; i++) {
            content.append(data[i]).append(", ");
        }
        content.setCharAt(content.lastIndexOf(", "),'}') ;

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 將資料寫入資料庫
                StringBuilder djangorespone = new StringBuilder();
                handler.post(new Runnable() {
                    public void run() {
                        spinner.setVisibility(View.VISIBLE);
                    }
                });
                HttpURLConnection djangoconnect = null;
                try {

                    URL url = new URL(registerurl);
                    djangoconnect = (HttpURLConnection)url.openConnection();
                    djangoconnect.setRequestMethod("POST");
                    djangoconnect.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    djangoconnect.setRequestProperty("Accept", "application/json");
                    djangoconnect.setDoInput(true);
                    djangoconnect.setDoOutput(true);

                    OutputStream os = djangoconnect.getOutputStream();
                    DataOutputStream writer = new DataOutputStream(os);
                    writer.write(content.toString().getBytes(StandardCharsets.UTF_8));
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
                        while ((line = in.readLine()) != null) {
                            djangorespone.append(line).append("\n");
                        }
                        in.close();

                        String response;
                        response = new JSONObject(djangorespone.toString()).getString("status");
                        Log.v("django reponse", response);

                        if(response.contains("repeat user")){
                            register.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(register.this, "已註冊過了!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else if(response.contains("ok")){
                            register.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(register.this, "註冊成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                            Intent intent = new Intent();
                            intent.setClass(register.this, takephoto.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("id",stuid.getText().toString());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }

                    }
                } catch (Exception e) {
                    register.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(register.this, "註冊失敗請檢查網路連線", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("registfailed", e.getLocalizedMessage());
                    e.printStackTrace();
                }finally {
                    if (djangoconnect != null)
                        djangoconnect.disconnect();
                    handler.post(new Runnable() {
                        public void run() {
                            spinner.setVisibility(View.INVISIBLE);
                        }
                    });
                }

            }
        }).start();


    }
}