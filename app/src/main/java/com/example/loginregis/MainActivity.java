package com.example.loginregis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

// sharedPreference -- loginInfo
    // loggedIn: Boolean
    // userID: String
    // userPwd: String

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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