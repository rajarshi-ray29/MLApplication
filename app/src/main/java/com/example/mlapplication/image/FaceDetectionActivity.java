package com.example.mlapplication.image;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.mlapplication.helper.BoxWithLabel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.ArrayList;
import java.util.List;

public class FaceDetectionActivity extends ImageClassificationActivity{

    private FaceDetector faceDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();
        faceDetector=FaceDetection.getClient(highAccuracyOpts);
    }

    @Override
    protected void runClassification(Bitmap bitmap) {
        Bitmap finalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        InputImage image = InputImage.fromBitmap(finalBitmap, 0);
        faceDetector.process(image)
                .addOnFailureListener(error -> {
                    error.printStackTrace();
                })
                .addOnSuccessListener(faces -> {
                    if (faces.isEmpty()) {
                        getTextViewOutput().setText("No faces detected");
                    } else {
                        getTextViewOutput().setText(String.format("%d faces detected", faces.size()));
                        List<BoxWithLabel> boxes = new ArrayList();
                        int i=1;
                        for (Face face : faces) {
                            boxes.add(new BoxWithLabel(String.valueOf(i), face.getBoundingBox()));
                            i++;
                        }
                        drawDetectionResult(finalBitmap, boxes);
                    }
                });
    }
}
