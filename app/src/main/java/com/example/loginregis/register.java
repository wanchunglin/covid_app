package com.example.loginregis;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class register extends AppCompatActivity {
    EditText stuid  ;
    EditText name ;
    EditText email ;
    EditText phone ;
    EditText password ;
    Switch disp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        name.setText("萬長霖");
        phone = findViewById(R.id.editTextPhone);
        phone.setText("0975363637");
        email = findViewById(R.id.mail);
        email.setText("wanchunglin.eed06@g2.nctu.edu.tw");
        stuid = findViewById(R.id.editTextNumberPassword);
        stuid.setText("0610807");
        stuid.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        password = findViewById(R.id.editTextTextPassword7);
        password.setText("SAM310428");
        disp = findViewById((R.id.switch3));
    }


    public void display(View view){
        if(disp.getText().toString().equals("顯示")){
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            disp.setText("隱藏")  ;
        } else{
            password.setTransformationMethod(PasswordTransformationMethod.getInstance()); ;
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
                String djangorespone = "";

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
                    writer.write(content.toString().getBytes("UTF-8"));
                    writer.flush();
                    writer.close();
                    os.close();

                    InputStream inputStream = djangoconnect.getInputStream();
                    int status = djangoconnect.getResponseCode();
                    Log.d("djangoresponse", String.valueOf(status));



                    if(status == 200){
                        InputStreamReader reader = new InputStreamReader(inputStream,"utf-8");
                        BufferedReader in = new BufferedReader(reader);

                        String line="";
                        while ((line = in.readLine()) != null) {
                            djangorespone += (line+"\n");
                        }
                        in.close();

                        String response = null;
                        response = new JSONObject(djangorespone).getString("status");
                        Log.v("django reponse", response);

                        if(response.contains("repeat user")){
                            register.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(register.this, "已經註冊過囉", Toast.LENGTH_SHORT).show();
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
                }




            }
        }).start();


    }
}