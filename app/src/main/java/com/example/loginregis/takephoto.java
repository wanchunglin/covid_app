package com.example.loginregis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class takephoto extends AppCompatActivity {
    Button take;
    ImageView photo;
    Button confirm;
    Button t;
    Bitmap image;
    String id;
    String currentPhotoPath;
    ProgressBar spinner;
    Handler handler = new Handler();
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takephoto);

        take = findViewById(R.id.button4);
        photo = findViewById(R.id.imageView7);
        confirm = findViewById(R.id.button6);
        t = findViewById(R.id.button7);
        spinner = findViewById(R.id.indeterminateBar);
        spinner.setVisibility(View.INVISIBLE);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File storageDir = new File(getFilesDir().getAbsolutePath()+"/Pictures/");
        if (!storageDir.exists()) storageDir.mkdirs();

        boolean b = storageDir.exists();
//        File image = new File(storageDir,imageFileName+".jpg" );
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

//        if(!image.exists())
//            image.createNewFile();
        boolean a = image.exists();

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent1() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 0);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("file error", "cannot create file");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.loginregis.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            image = BitmapFactory.decodeFile(currentPhotoPath);
            photo.setImageBitmap(image);
        }
    }

    public void take_pic(View view) {
        dispatchTakePictureIntent1();
        confirm.setVisibility(View.VISIBLE);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) t.getLayoutParams();
        take.setLayoutParams(params);
        take.setText("重拍");
    }

    public void OK(View view) {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 將資料寫入資料庫

                handler.post(new Runnable() {
                    public void run() {
                        spinner.setVisibility(View.VISIBLE);
                    }
                });
                String end = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                StringBuilder djangorespone = new StringBuilder();
                String actionUrl = "http://140.113.123.58:8000/userImages/addImage/";

                try {
                    URL url = new URL(actionUrl);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    /* 允許Input、Output，不使用Cache */
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setUseCaches(false);
                    /* 設定傳送的method=POST */
                    con.setRequestMethod("POST");
                    /* setRequestProperty */
                    con.setRequestProperty("Connection", "Keep-Alive");
                    con.setRequestProperty("Charset", "UTF-8");
                    con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    /* 設定DataOutputStream */
                    DataOutputStream ds = new DataOutputStream(con.getOutputStream());
                    ds.writeBytes(twoHyphens + boundary + end);
                    ds.writeBytes(String.format("Content-Disposition: form-data; name=\"imagefile\";filename=\"%s.jpg\"%s", id, end));
                    ds.writeBytes(end);
                    /* 取得檔案的FileInputStream */
                    FileInputStream fStream = new FileInputStream(currentPhotoPath);
                    /* 設定每次寫入1024bytes */
                    int bufferSize = 1024;
                    byte[] buffer = new byte[bufferSize];
                    int length;
                    /* 從檔案讀取資料至緩衝區 */
                    while ((length = fStream.read(buffer)) != -1) {
                        /* 將資料寫入DataOutputStream中 */
                        ds.write(buffer, 0, length);
                    }
                    ds.writeBytes(end);
                    ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
                    /* close streams */
                    fStream.close();
                    ds.flush();
                    /* 取得Response內容 */
                    InputStream is = con.getInputStream();
                    int status = con.getResponseCode();
                    Log.d("djangoresponse", String.valueOf(status));

                    if (status == 200) {
                        InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                        BufferedReader in = new BufferedReader(reader);

                        String line;
                        while ((line = in.readLine()) != null) {
                            djangorespone.append(line).append("\n");
                        }
                        in.close();

                        String response;
                        response = new JSONObject(djangorespone.toString()).getString("status");
                        Log.v("django reponse", response);

                        if (response.contains("ok")) {
                            Log.d("upload", "success");
                            takephoto.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(takephoto.this, "上傳成功", Toast.LENGTH_SHORT).show();
                                    spinner.setVisibility(View.INVISIBLE);
                                }
                            });
                            Intent intent = new Intent();
                            intent.setClass(takephoto.this, verify.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("id", id);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                    /* 關閉DataOutputStream */
                    ds.close();
                    con.disconnect();
                } catch (Exception e) {
                    takephoto.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(takephoto.this, "上傳失敗請檢查網路連線", Toast.LENGTH_SHORT).show();
                            spinner.setVisibility(View.INVISIBLE);
                        }
                    });
                    Log.e("upload", "failed " + e.getLocalizedMessage());
                } finally {
                    handler.post(new Runnable() {
                        public void run() {
                            spinner.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
        thread.start();

    }

}