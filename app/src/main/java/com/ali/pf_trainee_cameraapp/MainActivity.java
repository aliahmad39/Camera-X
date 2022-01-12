package com.ali.pf_trainee_cameraapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.ImageCaptureConfig;
import androidx.camera.core.impl.PreviewConfig;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private Executor executor = Executors.newSingleThreadExecutor();

    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

    PreviewView mPreviewView;
    Preview preview;
    ImageView captureImage , ivSwitch , proImage;
    File file;
    ImageButton ivFlash;

    Singleton singleton = Singleton.getInstance();

    ArrayList<Uri> list = singleton.list;
    ProcessCameraProvider cameraProvider;
    Camera camera;
    CameraSelector cameraSelector;
    ImageCapture imageCapture;
    ImageAnalysis imageAnalysis;
    boolean isFlashOn = false;

    public  ArrayList<String> saveedList = singleton.savedList;
    RecyclerViewAdapter adapter = new RecyclerViewAdapter();
   // CameraSelector lensFacing = CameraSelector.DEFAULT_BACK_CAMERA;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
        getSavedData();
        checkPermissions();
        initializeClickListener();
        captureImage();


    }

    private void initializeClickListener() {
        proImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
         startActivity(new Intent(MainActivity.this , GalleryActivity.class));
            }
        });

        ivFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashSwap();
            }
        });

        ivSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraSwap();
            }
        });
    }

    private void checkPermissions() {
        if (allPermissionsGranted()) {
            startCamera(); //start camera if permission has been granted by user
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void bindViews() {
        mPreviewView = findViewById(R.id.pre_camera);
        captureImage = findViewById(R.id.captureImg);
        proImage = findViewById(R.id.profile_image);
        ivFlash = findViewById(R.id.iv_flash);
        ivSwitch = findViewById(R.id.iv_switch_camera);
    }

    private void getSavedData() {
        ArrayList<String> list1 = getArrayList("myGalleryList1");
        if(list1 != null){
            if(!list1.isEmpty()){
                for (String string : list1) {
                    Uri uri = Uri.parse(string);
                    if(!list.contains(uri)) {
                        list.add(uri);
                    }
                }
                setProfileImage(list.get(list.size()-1));
            }
        }
    }

    private void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                 cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @SuppressLint("RestrictedApi")
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

       preview = new Preview.Builder()
                .build();

            if(cameraSelector == null){
                cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();
            }else {
                if (cameraSelector.getLensFacing().equals(CameraSelector.LENS_FACING_FRONT)) {
                    cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build();

                } else {
                    cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                            .build();

                }
            }

        setPreview();

    }
    private void captureImage() {
        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           //     Toast.makeText(MainActivity.this, "capture click", Toast.LENGTH_SHORT).show();
                Calendar calendar = Calendar.getInstance();
                String randomNo = calendar.getTimeInMillis()+".jpg";


             // SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
              //   File file1 = new File(getBatchDirectoryName(), randomNo);
                file = null;

                // file = createImageFile();
                //   File file = makeFile("Download", filename);
                file = makeFile("Download",  randomNo);


                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
//                Toast.makeText(MainActivity.this, "image saved", Toast.LENGTH_SHORT).show();
//                Uri uri2 = getFileUri("Download", mDateFormat.toString());
//                if(uri2 != null){
//                    list.add(uri2);
//                    startActivity(new Intent(MainActivity.this , GalleryActivity.class));
//                }
                imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                        //    Toast.makeText(MainActivity.this, "image saved 2", Toast.LENGTH_SHORT).show();
                        Uri uri2 = getFileUri("Download", randomNo);
                        if(uri2 != null){
                            setProfileImage(uri2);
                            list.add(uri2);
//                        Uri uri2 = outputFileResults.getSavedUri();
//                        if(uri2 != null){
//                            setProfileImage(uri2);
//                            list.add(uri2);

//                            String string = uri2.toString();
//                            saveedList.add(string);
//                            saveArrayList(saveedList ,"myGalleryList1" );
                        }
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        //  Toast.makeText(MainActivity.this, "error :"+error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("errorCapture", error.getLocalizedMessage());
                        error.printStackTrace();
                    }
                });
            }
        });
    }

    public void setProfileImage(Uri uri2) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        proImage.setImageURI(uri2);
                    }
                });

            }
        });
        thread.start();

    }

    private void setPreview() {
        imageAnalysis = new ImageAnalysis.Builder()
                  .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();

        //Vendor-Extensions (The CameraX extensions dependency in build.gradle)
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);

        // Query if extension is available (optional).
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .build();
        preview.setSurfaceProvider(mPreviewView.createSurfaceProvider());
        camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis, imageCapture);
    }



    public String getBatchDirectoryName() {

        String app_folder_path = "";
        app_folder_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/images";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {

        }

        return app_folder_path;
    }

    private boolean allPermissionsGranted() {

        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_";

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );
        Toast.makeText(this, "location :"+image.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    public File makeFile(String destination, String filename) {
        String root = Environment.getExternalStorageDirectory().toString();
        //  String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
//        if (!isStoragePermissionGranted())
//            return null;

        File myDir = new File(root, destination);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File file = new File(myDir, filename);


        //  By using this line you will be able to see saved images in the gallery view.
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//                Uri.parse("file://" + Environment.getExternalStorageDirectory())));
//        new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file) );

        //      Toast.makeText(this, file.getPath() + "", Toast.LENGTH_SHORT).show();


        return file;
    }

    public Uri getFileUri(String destination, String filename) {
    //  final File file = makeFile(destination, filename);
    // final File file1 = new File(String.valueOf(file));

        Uri uri = FileProvider.getUriForFile(MainActivity.this, "com.ali.pf_trainee_cameraapp.provider", file);
        return uri;
    }


    private void flashSwap() {
        if(!isFlashOn){
            camera.getCameraControl().enableTorch(true);
            ivFlash.setImageResource( R.drawable.ic_flash_on );
            isFlashOn = true;
        }
      else{
            camera.getCameraControl().enableTorch(false);
            ivFlash.setImageResource( R.drawable.ic_flash_off );
            isFlashOn = false;
        }
    }



        @SuppressLint("RestrictedApi")
    void cameraSwap(){
        cameraProvider.unbindAll();
        startCamera();


    }


    public void saveArrayList(ArrayList<String> list, String key  ){
        SharedPreferences sp = getSharedPreferences("PfCameraX" , MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
        // editor.commit();
    }

    @Override
    protected void onPause() {
      //  Toast.makeText(this, "on pause", Toast.LENGTH_SHORT).show();
        for(Uri uri : list){
         String string = uri.toString();
         if(!saveedList.contains(string)){
             saveedList.add(string);
         }
        }
        if(!saveedList.isEmpty())
            saveArrayList(saveedList ,"myGalleryList1" );
        super.onPause();
    }

    public  ArrayList<String> getArrayList(String key )  {
        SharedPreferences sp =getSharedPreferences("PfCameraX" , MODE_PRIVATE);
        // SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = sp.getString(key, null);

        Type type = new TypeToken<ArrayList<String>>() {}.getType();

        return gson.fromJson(json, type);
    }

    @Override
    protected void onResume() {
        if(!list.isEmpty()){
            setProfileImage(list.get(list.size()-1));
        }else{
            proImage.setImageDrawable(getDrawable(R.drawable.ic_photo));
        }

        super.onResume();
    }
}