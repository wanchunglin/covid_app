package com.nycu.biosciencepr;

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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.R;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class register extends AppCompatActivity {
    EditText stuid;
    EditText name;
    EditText email;
    EditText phone;
    EditText password;
    Switch disp;
    ProgressBar spinner;
    Handler handler = new Handler();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        spinner = findViewById(R.id.registerProgressBar);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.editTextPhone);
        email = findViewById(R.id.mail);
        stuid = findViewById(R.id.editTextNumberPassword);
        stuid.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        password = findViewById(R.id.editTextTextPassword7);
        disp = findViewById((R.id.regisShowPwdSwitch));
    }


    public void display(View view) {
        if (disp.getText().toString().equals("顯示")) {
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            disp.setText("隱藏");
        } else {
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            disp.setText("顯示");
        }
    }

    public void finish(View view) {
        String[] data = {"\"userName\": ", "\"phone\": ", "\"email\": ", "\"userID\": ", "\"password\": "};
        data[0] = data[0] + '"' + name.getText().toString() + '"';
        data[1] = data[1] + '"' + phone.getText().toString() + '"';
        data[2] = data[2] + '"' + email.getText().toString() + '"';
        data[3] = data[3] + '"' + stuid.getText().toString() + '"';
        data[4] = data[4] + '"' + password.getText().toString() + '"';

        final String registerurl = "http://140.113.79.132:8000/users/register/";

        for (int i = 0; i < 5; i++)
            Log.d(String.valueOf(i), data[i]);

        final StringBuilder content = new StringBuilder("{");

        for (int i = 0; i < 5; i++) {
            content.append(data[i]).append(", ");
        }
        content.setCharAt(content.lastIndexOf(", "), '}');

        new Thread(new Runnable() {
            @Override
            public void run() {

                handler.post(new Runnable() {
                    public void run() {
                        spinner.setVisibility(View.VISIBLE);
                    }
                });
                String response = null;
                djangocon connect = new djangocon();

                Map<String, String> property = new HashMap<>();
                property.put("Content-Type", "application/json; charset=UTF-8");
                property.put("Accept", "application/json");
                // 將資料寫入資料庫
                try {
                    response = connect.connection(registerurl, "POST", property, content.toString(), null);
                } catch (IOException e) {
                    register.this.runOnUiThread(new Runnable() {
                        public void run() {
                            spinner.setVisibility(View.INVISIBLE);
                            Toast.makeText(register.this, "註冊失敗請檢查網路連線", Toast.LENGTH_SHORT).show();
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

                if (response.contains("repeat user")) {
                    register.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(register.this, "已註冊請拍照!!", Toast.LENGTH_LONG).show();
                        }
                    });
                    Intent intent = new Intent();
                    intent.setClass(register.this, takephoto.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", stuid.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (response.contains("ok")) {
                    register.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(register.this, "註冊成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent intent = new Intent();
                    intent.setClass(register.this, takephoto.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", stuid.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else if (response.contains("repeat email")) {
                    register.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(register.this, "信箱已經使用", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if (response.contains("email fail")) {
                    register.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(register.this, "請確認信箱是否存在", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                spinner.setVisibility(View.INVISIBLE);
            }
        }).start();
    }
}