package com.nycu.biosciencepr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.R;

public class forget_password extends AppCompatActivity {
    EditText email,code;
    Button sendcode, givepassword;
    String stremail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        code = findViewById(R.id.editTextNumberPassword2);
        email = findViewById(R.id.editTextTextEmailAddress);
        sendcode = findViewById(R.id.button9);
        givepassword = findViewById(R.id.button6);
        givepassword.setClickable(false);
    }
    public void requestverify(View view){
        stremail = email.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 將資料寫入資料庫
                String forgeturl = "http://140.113.79.132:8000/users/forgetpassword/";
                String content = String.format("{\"email\":\"%s\"}", stremail);
                String response = null;
                djangocon connect = new djangocon();

                Map<String, String> property = new HashMap<>();
                property.put("Content-Type", "application/json; charset=UTF-8");
                property.put("Accept", "application/json");

                try {
                    response = connect.connection(forgeturl, "POST", property, content, null);
                    response = new JSONObject(response).getString("status");
                } catch (IOException | JSONException e) {
                    forget_password.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(forget_password.this, "請確認網路連線", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                    return;
                }
                if(response.contains("success") ){

                    forget_password.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            givepassword.setClickable(true);
                            Toast.makeText(forget_password.this, "請至信箱確認驗證碼", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    forget_password.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(forget_password.this, "信箱錯誤", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }).start();

    }

    public void checkpass(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 將資料寫入資料庫
                String forgeturl = "http://140.113.79.132:8000/users/verify/";
                String content = String.format("{\"email\":\"%s\",\"verify\":\"%s\"}", email.getText().toString(),code.getText().toString());
                String response = null;
                djangocon connect = new djangocon();

                Map<String, String> property = new HashMap<>();
                property.put("Content-Type", "application/json; charset=UTF-8");
                property.put("Accept", "application/json");

                try {
                    response = connect.connection(forgeturl, "POST", property, content, null);
                    response = new JSONObject(response).getString("status");
                } catch (IOException | JSONException e) {
                    forget_password.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(forget_password.this, "請檢查是否有網路連線", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                    return;
                }
                if(response.contains("ok")){
                    forget_password.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(forget_password.this, "驗證成功!", Toast.LENGTH_LONG).show();
                        }
                    });
                    Bundle bundle = new Bundle();
                    bundle.putString("email", stremail);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(forget_password.this, resetpassword.class);
                    startActivity(intent);
                }
                else {
                    forget_password.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(forget_password.this, "驗證碼有誤", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }).start();

    }
}