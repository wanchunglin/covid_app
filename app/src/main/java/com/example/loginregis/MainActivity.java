package com.example.loginregis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref = getSharedPreferences("loginInfo", MODE_PRIVATE);

        if (pref.getBoolean("loggedIn", false)){
            // the user is already logged in
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, login.class);
            startActivity(intent);
        }
    }

    public void register(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, register.class);
        startActivity(intent);
    }

    public void login(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, login.class);
        startActivity(intent);
    }
}