package com.nycu.biosciencepr;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;

import com.R;

public class QRcode extends AppCompatActivity {
    ImageView ivCode;
    Button refresh;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_rcode);
        ivCode = findViewById(R.id.ivCode);
        refresh = findViewById(R.id.button8);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        generate_qrcode();

    }

    public void generate_qrcode(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String encodeinfo = id + '\n' +dff.format(new Date());
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BarcodeEncoder encoder = new BarcodeEncoder();

        try {
            Bitmap bit = encoder.encodeBitmap(encodeinfo, BarcodeFormat.QR_CODE, 250, 250,hints);
            bit = Bitmap.createBitmap(bit,25,25,bit.getWidth()-50,bit.getHeight()-50);
            ivCode.setImageBitmap(bit);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void regenerate(View view ){
        generate_qrcode();
    }

    public void log_out(View view){
        SharedPreferences pref = getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("loggedIn",false);
        editor.putString("userID","");
        editor.putString("userPwd","");
        editor.apply();

        Intent intent = new Intent();
        intent.setClass(QRcode.this, login.class);
        startActivity(intent);
    }
}