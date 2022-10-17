package uk.ac.tees.b1662096.travelhopper_travelapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.ActivityMainBinding;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding mainActivityBinding;

    private ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View bindingView = mainActivityBinding.getRoot();
        setContentView(bindingView);

        PreviewView previewView = findViewById(R.id.previewViewFinder);

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();

                imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();

                CameraSelector cameraSelector = new CameraSelector.Builder().build();

                Camera camera = cameraProvider.bindToLifecycle((this), cameraSelector, preview, imageCapture);

                preview.setSurfaceProvider(previewView.getSurfaceProvider());

            } catch (InterruptedException | ExecutionException e) {

            }
        }, ContextCompat.getMainExecutor(this));

        FloatingActionButton imageCaptureButton = findViewById(R.id.image_capture_button);
        FloatingActionButton videoCaptureButton = findViewById(R.id.video_capture_button);
        imageCaptureButton.setOnClickListener(this);
        videoCaptureButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_capture_button:

                break;
            case R.id.video_capture_button:

                break;
        }
    }
}