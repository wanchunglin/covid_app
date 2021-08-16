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

public class resetpassword extends AppCompatActivity {
    EditText pass1;
    EditText pass2;
    Button reset;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);
        pass1 = (EditText) findViewById(R.id.Password1);
        pass2 = (EditText) findViewById(R.id.Password2);
        reset = (Button) findViewById(R.id.button7);

        Bundle bundle = getIntent().getExtras();
        email = bundle.getString("email");
    }

    public void changePassword(View view){
        if(pass1.getText().toString().equals(pass2.getText().toString())){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 將資料寫入資料庫
                    String response = null;
                    String content = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, pass1.getText().toString());
                    String verifyurl = "http://140.113.79.132:8000/users/changepassword/";
                    djangocon connect = new djangocon();
                    Map<String, String> property = new HashMap<>();
                    property.put("Content-Type", "application/json; charset=UTF-8");
                    property.put("Accept", "application/json");

                    try {
                        response = connect.connection(verifyurl, "POST", property, content, null);
                    } catch (IOException e) {
                        resetpassword.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(resetpassword.this, "網路連線失敗", Toast.LENGTH_SHORT).show();
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

                    if(response.contains("ok")){
                        resetpassword.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(resetpassword.this, "密碼重置成功!", Toast.LENGTH_LONG).show();
                            }
                        });
                        Intent intent = new Intent();
                        intent.setClass(resetpassword.this, login.class);
                        startActivity(intent);
                    }
                }
            }).start();
        }
        else{
            Toast.makeText(resetpassword.this, "兩次密碼不一致", Toast.LENGTH_SHORT).show();
        }
    }
}