package uk.ac.tees.b1662096.travelhopper_travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
//import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
//import androidx.camera.video.FallbackStrategy;
//import androidx.camera.video.Quality;
//import androidx.camera.video.QualitySelector;
//import androidx.camera.video.Recorder;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
//import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.ActivityCameraBinding;

public class CameraActivity extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private PreviewView previewView;

    private ImageCapture imageCapture;

//    private VideoCapture videoCapture;
//
//    private Recorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up View Binding for the Root View
        ActivityCameraBinding cameraActivityBinding = ActivityCameraBinding.inflate(getLayoutInflater());
        View bindingView = cameraActivityBinding.getRoot();
        setContentView(bindingView);

//        if (allPermissionsGranted()) {
//            startCamera();
//        } else {
//            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
//        }

        previewView = cameraActivityBinding.previewViewFinder;

        // Set up Camera Process
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCamera(cameraProvider);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }, getCameraExecutor());

        // Set up Photo and Video Buttons
        FloatingActionButton imageCaptureButton = cameraActivityBinding.imageCaptureButton;
        imageCaptureButton.setOnClickListener(view -> capturePhoto());
//        FloatingActionButton videoCaptureButton = cameraActivityBinding.videoCaptureButton;
//        videoCaptureButton.setOnClickListener(view -> captureVideo());

        FloatingActionButton backButton = cameraActivityBinding.backButton;
        Intent navigateToHome = new Intent(this, MainActivity.class);
        backButton.setOnClickListener(view -> startActivity(navigateToHome));

    }

    public Executor getCameraExecutor() {
        return ContextCompat.getMainExecutor(this);
    }


    // Start the main Camera functionality
    private void startCamera(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        Preview preview = new Preview.Builder()
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Build ImageCapture
        imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();

//        // Build Recorder as a part of the VideoCapture
//        QualitySelector videoQualitySelector = QualitySelector.fromOrderedList(Arrays.asList(Quality.UHD, Quality.FHD, Quality.HD, Quality.SD), FallbackStrategy.lowerQualityOrHigherThan(Quality.SD));
//        recorder = new Recorder.Builder().setExecutor(getCameraExecutor()).setQualitySelector(videoQualitySelector).build();
//
//        videoCapture = VideoCapture.;
//
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    // Capture and save photo to phone storage
    private void capturePhoto() {
        File photoDir = new File(Environment.getExternalStorageDirectory().getPath());
        if (!photoDir.exists()) {
            photoDir.mkdir();
        }

        Date photoDate = new Date();
        String timeStamp = String.valueOf(photoDate.getTime());
        String photoFilePath = photoDir.getAbsolutePath() + "/" + timeStamp + ".jpg";

        File photoFile = new File(photoFilePath);

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(outputFileOptions, getCameraExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Toast.makeText(CameraActivity.this, "Photo has been saved successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        Toast.makeText(CameraActivity.this, "Error saving photo: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
//    private void captureVideo() {
//
//    }

}

