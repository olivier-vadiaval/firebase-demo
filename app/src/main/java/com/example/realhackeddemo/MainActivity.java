package com.example.realhackeddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.otaliastudios.cameraview.gesture.Gesture;
import com.otaliastudios.cameraview.gesture.GestureAction;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    CameraView camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = findViewById(R.id.camera);
        camera.setLifecycleOwner(this);
        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                super.onPictureTaken(result);
            }
        });

        camera.mapGesture(Gesture.LONG_TAP, GestureAction.TAKE_PICTURE);
        camera.addFrameProcessor(new FrameProcessor() {
            @Override
            public void process(@NonNull Frame frame) {
                extractImageData(frame);
            }
        });
    }

    private void extractImageData(Frame frame) {
            // Set up detector
            ObjectDetectorOptions options =
                    new ObjectDetectorOptions.Builder()
                            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
                            .enableMultipleObjects()
                            .enableClassification()
                            .build();

            ObjectDetector detector =
                    ObjectDetection.getClient(options);

            detector.process(getImage(frame))
                    .addOnSuccessListener(detectedObjects -> {
                        for (DetectedObject object : detectedObjects) {
                            if (object.getLabels().size() != 0) {
                                Toast.makeText(MainActivity.this, object.getLabels().get(0).getText(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "An error occurred", Toast.LENGTH_SHORT).show());
    }

    private InputImage getImage(Frame frame) {
        return InputImage.fromByteArray(
                frame.getData(),
                frame.getSize().getWidth(),
                frame.getSize().getHeight(),
                frame.getRotationToView(),
                InputImage.IMAGE_FORMAT_NV21
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.destroy();
    }
}