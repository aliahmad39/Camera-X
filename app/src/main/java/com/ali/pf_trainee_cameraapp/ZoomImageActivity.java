package com.ali.pf_trainee_cameraapp;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class ZoomImageActivity extends AppCompatActivity {
ImageView zoomImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);

        zoomImage = findViewById(R.id.zoomImage1);

        String string = getIntent().getStringExtra("uriData");

            zoomImage.setImageURI(Uri.parse(string));



    }
}