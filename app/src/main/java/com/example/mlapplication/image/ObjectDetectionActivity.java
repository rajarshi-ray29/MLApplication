package com.example.mlapplication.image;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.mlapplication.helper.BoxWithLabel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;

import java.util.ArrayList;
import java.util.List;

public class ObjectDetectionActivity extends ImageClassificationActivity{

    private ObjectDetector objectDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ObjectDetectorOptions options =
                new ObjectDetectorOptions.Builder()
                        .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
                        .enableMultipleObjects()
                        .enableClassification()  // Optional
                        .build();
        objectDetector = ObjectDetection.getClient(options);
    }

    @Override
    protected void runClassification(Bitmap bitmap) {
        InputImage image=InputImage.fromBitmap(bitmap,0);
        objectDetector.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<DetectedObject>>() {
                    @Override
                    public void onSuccess(List<DetectedObject> detectedObjects) {
                        if(!detectedObjects.isEmpty()){
                            StringBuilder builder=new StringBuilder();
                            List<BoxWithLabel> boxes=new ArrayList<>();
                            for(DetectedObject object:detectedObjects){
                                if(!object.getLabels().isEmpty()){
                                    String label=object.getLabels().get(0).getText();
                                    builder.append(label).append(": ").append(object.getLabels().get(0).getConfidence()).append("\n");
                                    boxes.add(new BoxWithLabel(label,object.getBoundingBox()));
                                    Log.d("ObjectDetection","Object Detected: "+label);
                                }
                            }
                            getTextViewOutput().setText(builder.toString());
                            drawDetectionResult(bitmap,boxes);
                        }else{
                            getTextViewOutput().setText("Could not detect");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}
