package com.example.mlapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mlapplication.helper.ImageHelperActivity;
import com.example.mlapplication.image.FaceDetectionActivity;
import com.example.mlapplication.image.FlowerDetectionActivity;
import com.example.mlapplication.image.ImageClassificationActivity;
import com.example.mlapplication.image.ObjectDetectionActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onGotoImageActivity(View view){
        Intent intent=new Intent(this, ImageClassificationActivity.class);
        startActivity(intent);
    }

    public void onGotoFlowerDetectionActivity(View view){
        Intent intent=new Intent(this, FlowerDetectionActivity.class);
        startActivity(intent);
    }

    public void onGotoObjectDetectionActivity(View view){
        Intent intent=new Intent(this, ObjectDetectionActivity.class);
        startActivity(intent);
    }
    public void onGotoFaceDetectionActivity(View view){
        Intent intent=new Intent(this, FaceDetectionActivity.class);
        startActivity(intent);
    }
}