package com.example.loginregis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

// sharedPreference -- loginInfo
    // loggedIn: Boolean
    // userID: String
    // userPwd: String

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                        String token = task.getResult();

                        // Log and toast
                        Log.d("token", token);
                    }
                });

    }

    public void register(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, register.class);
        startActivity(intent);
    }

    public void login(View view) {
        Intent intent = new Intent();
//        Bundle bundle = new Bundle();
//        bundle.putString("token", token);
//        intent.putExtras(bundle);
        intent.setClass(MainActivity.this, login.class);
        startActivity(intent);
    }



}