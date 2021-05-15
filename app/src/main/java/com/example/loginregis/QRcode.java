package com.example.loginregis;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        String encodeinfo = id + '\n' +dff.format(new Date());
        Hashtable<EncodeHintType, String> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BarcodeEncoder encoder = new BarcodeEncoder();

        try {
            final Bitmap bit = encoder.encodeBitmap(encodeinfo, BarcodeFormat.QR_CODE, 1000, 1000,hints);
            ivCode.setImageBitmap(bit);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void regenerate(View view){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String encodeinfo = id + '\n' +dff.format(new Date());
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BarcodeEncoder encoder = new BarcodeEncoder();

        try {
            final Bitmap bit = encoder.encodeBitmap(encodeinfo, BarcodeFormat.QR_CODE, 1000, 1000,hints);
            ivCode.setImageBitmap(bit);
        } catch (WriterException e) {
            e.printStackTrace();
        }


    }
}