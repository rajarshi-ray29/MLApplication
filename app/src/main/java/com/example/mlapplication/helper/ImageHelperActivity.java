package com.example.mlapplication.helper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mlapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabelerOptionsBase;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ImageHelperActivity extends AppCompatActivity {

    private int REQUEST_PICK_IMAGE=1000;
    private int REQUEST_CAPTURE_IMAGE=1001;

    private ImageView inputImageView;
    private TextView textViewOutput;

    private File photofile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_helper);

        inputImageView=findViewById(R.id.imageViewOutput);
        textViewOutput=findViewById(R.id.textViewOutput);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }


    }
    public void onPickImage(View view) {
//        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
//
//        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
//        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, REQUEST_PICK_IMAGE);
        }
    }

    public void onStartCamera(View view){
        photofile=createPhotoFile();
        Uri fileUri= FileProvider.getUriForFile(this,"com.example.fileprovider",photofile);

        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);

        startActivityForResult(intent,REQUEST_CAPTURE_IMAGE);
    }

    private File createPhotoFile(){
        File photoFileDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"ML_IMAGE_HELPER");
        if(!photoFileDir.exists()){
            photoFileDir.mkdirs();
        }
        String name=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file=new File(photoFileDir.getPath()+File.separator+name);
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==REQUEST_PICK_IMAGE){
                Uri uri=data.getData();
                Bitmap bitmap=loadFromUri(uri);
                inputImageView.setImageBitmap(bitmap);
                runClassification(bitmap);
            }
            else if(requestCode==REQUEST_CAPTURE_IMAGE){
                Log.d("TAG","received callback from camera");
                Bitmap bitmap= BitmapFactory.decodeFile(photofile.getAbsolutePath());
                inputImageView.setImageBitmap(bitmap);
                runClassification(bitmap);
            }
        }
    }

    protected Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;

        try {
            if (Build.VERSION.SDK_INT > 27) {
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    protected void runClassification(Bitmap bitmap){

    }

    public TextView getTextViewOutput() {
        return textViewOutput;
    }

    public ImageView getInputImageView() {
        return inputImageView;
    }

//    protected void drawDetectionResult(List<BoxWithLabel> boxes, Bitmap bitmap){
//        Bitmap outputBitmap=bitmap.copy(Bitmap.Config.ARGB_8888,true);
//        Canvas canvas=new Canvas(bitmap);
//
//    }
    protected void drawDetectionResult(
            Bitmap bitmap,
            List<BoxWithLabel> detectionResults
    ) {
        Bitmap outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(outputBitmap);
        Paint pen = new Paint();
        pen.setTextAlign(Paint.Align.LEFT);

        for (BoxWithLabel box : detectionResults) {
            // draw bounding box
            pen.setColor(Color.RED);
            pen.setStrokeWidth(8F);
            pen.setStyle(Paint.Style.STROKE);
            canvas.drawRect(box.rect, pen);

            Rect tagSize = new Rect(0, 0, 0, 0);

            // calculate the right font size
            pen.setStyle(Paint.Style.FILL_AND_STROKE);
            pen.setColor(Color.YELLOW);
            pen.setStrokeWidth(2F);

            pen.setTextSize(96F);
            pen.getTextBounds(box.text, 0, box.text.length(), tagSize);
            float fontSize = pen.getTextSize() * box.rect.width() / tagSize.width();

            // adjust the font size so texts are inside the bounding box
            if (fontSize < pen.getTextSize()) {
                pen.setTextSize(fontSize);
            }

            float margin = (box.rect.width() - tagSize.width()) / 2.0F;
            if (margin < 0F) margin = 0F;
            canvas.drawText(
                    box.text, box.rect.left + margin,
                    box.rect.top + tagSize.height(), pen
            );
        }
        getInputImageView().setImageBitmap(outputBitmap);
    }
}