package com.nycu.biosciencepr;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.R;

// sharedPreference -- loginInfo
    // loggedIn: Boolean
    // userID: String
    // userPwd: String

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = "default_notification_channel_id";
            String channelName = "default_notification_channel_name";
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }
//        final FirebaseApp firebaseApp = FirebaseApp.initializeApp(this);
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w("token failed", "Fetching FCM registration token failed", task.getException());
//                            return;
//                        }
//
//                        // Get new FCM registration token
//                        String token = task.getResult();
//
//                        // Log and toast
//                        Log.d("token", token);
//                    }
//                });

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