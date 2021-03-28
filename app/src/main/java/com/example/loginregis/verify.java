package com.example.loginregis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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


    public void fffff(View view) throws InterruptedException {
        final String v = ver.getText().toString();
        Log.v("DB", id+id+id);

        final boolean[] l = {false};
        final Object lock = new Object();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 將資料寫入資料庫
                synchronized (lock){
                    MysqlCon con = new MysqlCon();
                    l[0] = con.verifying(id,v);
                    lock.notify();
                }
                // 讀取更新後的資料
            }
        });
        thread.start();
        synchronized (lock){
            Log.v("OK","waiting");
            lock.wait();
            Log.v("OK","complete");
        }

        if(l[0]){
            Toast.makeText(this,"註冊成功！",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setClass(verify.this, login.class);
            startActivity(intent);
        }
        else
            Toast.makeText(this,"驗證碼錯誤",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}