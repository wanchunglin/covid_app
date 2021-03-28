package com.example.loginregis;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;

public class login extends AppCompatActivity {

    EditText stuid ;
    EditText pas ;
    ImageView ivCode;
    Switch disp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        stuid = (EditText) findViewById(R.id.stid);
        stuid.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        pas = (EditText) findViewById(R.id.editTextPassword7);
        ivCode = (ImageView) findViewById(R.id.ivCode);
        disp = (Switch)  findViewById(R.id.switch1);
    }
    public void display(View view){
        if(disp.getText().toString().equals("顯示")){
            pas.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            disp.setText("隱藏")  ;
        } else{
            pas.setTransformationMethod(PasswordTransformationMethod.getInstance()); ;
            disp.setText("顯示")  ;
        }
    }
    public void fin(View view) throws InterruptedException {

    }

}