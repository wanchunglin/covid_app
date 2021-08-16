package com.nycu.biosciencepr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.R;

public class takephoto extends AppCompatActivity {
    Button take,confirm,rt;
    String id, currentPhotoPath;
    ImageView image;
    ProgressBar spinner;
    Handler handler = new Handler();
    PreviewView previewView;
    ImageAnalysis imageAnalysis;
    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    Camera camera;
    ImageCapture imageCapture;
    int rotationDegrees;
    Preview preview;
    ProcessCameraProvider cameraProvider;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takephoto);

        take = findViewById(R.id.button);
        confirm = findViewById(R.id.button3);
        rt = findViewById(R.id.button2);
        image = findViewById(R.id.imageView);
        previewView = findViewById(R.id.previewView);
        spinner = findViewById(R.id.indeterminateBar);
        spinner.bringToFront();

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {

                rotationDegrees = image.getImageInfo().getRotationDegrees();

                // insert your code here.
                image.close();
            }
        });
        cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void bindPreview( ProcessCameraProvider cameraProvider) {
        preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        imageCapture =
                new ImageCapture.Builder()
                        .build();
        Log.d("imagecaprotation", String.valueOf(previewView.getDisplay().getRotation()));
        camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageAnalysis, preview, imageCapture);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (!storageDir.exists()) storageDir.mkdirs();

//        File image = File.createTempFile(
//               imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );

        File image = new File(storageDir,imageFileName);
        if(image.exists())
            image.delete();

        image.createNewFile();

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

//    @SuppressLint("QueryPermissionsNeeded")
//    private void dispatchTakePictureIntent1() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//                Log.e("file error", "cannot create file");
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.example.NYCU.NYCU.lifeScience.fileprovider",
//                        photoFile);
//
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//            }
//        }
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            image = BitmapFactory.decodeFile(currentPhotoPath);
//            photo.setImageBitmap(image);
//        }
//    }

    public void take_pic(View view) throws IOException {
        File saveimage = createImageFile();
        ExifInterface exif= new ExifInterface(saveimage.getAbsolutePath());
        rotationDegrees = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        Log.d("rottttt", String.valueOf(rotationDegrees));
//        Log.d("path", saveimage.getAbsolutePath());
        imageCapture.setTargetRotation(CameraOrientation(rotationDegrees));
        Log.d("imcaprot", String.valueOf(imageCapture.getTargetRotation()));

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(saveimage).build();
        imageCapture.takePicture(outputFileOptions,ContextCompat.getMainExecutor(this) ,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                        // insert your code here.
                        Log.d("TAG", "onImageSaved: ");
                        Bitmap bitmap = previewView.getBitmap();
                        cameraProvider.unbindAll();
                        image.setImageBitmap(bitmap);
                        image.setVisibility(View.VISIBLE);
                        take.setVisibility(View.INVISIBLE);
                        rt.setVisibility(View.VISIBLE);
                        confirm.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        // insert your code here.
                        Log.d("WHY?", "Save error: ");
                        error.printStackTrace();
                    }
                }
        );
    }

    public void retake(View view){
        bindPreview(cameraProvider);
        take.setVisibility(View.VISIBLE);
        rt.setVisibility(View.INVISIBLE);
        image.setVisibility(View.INVISIBLE);
        confirm.setVisibility(View.INVISIBLE);
    }

    public int CameraOrientation(int orientation){
        switch (orientation) {
            case 90:
                return Surface.ROTATION_90;
            case 180:
                return Surface.ROTATION_180;
            case 270:
                return Surface.ROTATION_270;
            default:
                return Surface.ROTATION_0;
        }
    }

//    public int getCameraPhotoOrientation() {
//        int rotate = 0;
//        try {
//            ExifInterface exif  = null;
//            try {
//                exif = new ExifInterface(currentPhotoPath);
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//            int orientation = exif.getAttributeInt(
//                    ExifInterface.TAG_ORIENTATION, 0);
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    rotate = 180;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    rotate = 90;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    rotate = 270;
//                    break;
//                default:
//                    rotate = 0;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return rotate;
//    }

    public void OK(View view) throws IOException {
        spinner.setVisibility(View.VISIBLE);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 將資料寫入資料庫


                String response = null;
                String end = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                String actionUrl = "http://140.113.79.132:8000/userImages/addImage/";

                Map<String, String> property = new HashMap<>();
                property.put("Connection", "Keep-Alive");
                property.put("Charset", "UTF-8");
                property.put("Content-Type", "multipart/form-data;boundary=" + boundary);
                String content = twoHyphens + boundary + end +
                        String.format("Content-Disposition: form-data; name=\"imagefile\";filename=\"%s.jpg\"%s", id, end) + end;

                djangocon connect = new djangocon();
                try {
                    response = connect.connection(actionUrl,"POST",property,content,currentPhotoPath);
                } catch (IOException e) {
                    e.printStackTrace();takephoto.this.runOnUiThread(new Runnable() {
                        public void run() {
                            spinner.setVisibility(View.INVISIBLE);
                            Toast.makeText(takephoto.this, "上傳失敗請檢查網路連線", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                try {
                    assert response != null;
                    response = new JSONObject(response).getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (response.contains("ok")) {
                    Log.d("upload", "success");
                    takephoto.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(takephoto.this, "上傳成功!請檢查信箱進行驗證", Toast.LENGTH_LONG).show();
                        }
                    });
                    Intent intent = new Intent();
                    intent.setClass(takephoto.this, verify.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", id);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if(response.contains("addImage fail")){
                    Log.d("upload", "fail");
                    takephoto.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(takephoto.this, "請確保臉部在畫面中保持清晰", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                spinner.setVisibility(View.INVISIBLE);
            }
        });
        thread.start();

    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}