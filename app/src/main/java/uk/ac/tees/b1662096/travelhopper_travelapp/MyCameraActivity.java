package uk.ac.tees.b1662096.travelhopper_travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.FallbackStrategy;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.VideoRecordEvent;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.ActivityMyCameraBinding;

public class MyCameraActivity extends AppCompatActivity {

    private ActivityMyCameraBinding myCameraActivityBinding;

    private View rootView;

    private static final String[] CAMERA_ACTIVITY_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int CAMERA_ACTIVITY_PERMISSION_REQUEST_CODE = 10;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private PreviewView previewView;

    private ImageCapture imageCapture;

    private VideoCapture<Recorder> videoCapture;

    private Recorder recorder;

    private Recording recording;

    private FloatingActionButton imageCaptureButton, videoCaptureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up View Binding for the Root View
        myCameraActivityBinding = ActivityMyCameraBinding.inflate(getLayoutInflater());
        rootView = myCameraActivityBinding.getRoot();
        setContentView(rootView);

        // Check if device has an available camera
        if (cameraIsAvailable()) {
            Log.i("VIDEO_RECORD_TAG", "Camera is detected");
            // Set up the CameraProvider and check for permissions granted
            cameraProviderFuture = ProcessCameraProvider.getInstance(this);
            cameraProviderFuture.addListener(() -> {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    if (cameraPermissionGranted()) {
                        startCamera(cameraProvider);
                    } else {
                        requestMyCameraPermissions();
                    }
                } catch (ExecutionException | InterruptedException e) {
                    Log.e("ExecutionException/InterruptedException", "CameraX Binding Failed", e);
                }
            }, ContextCompat.getMainExecutor(this));

        } else {
            Log.i("VIDEO_RECORD_TAG", "Error - Unable to detect camera");
        }

        // Set up the previewView for CameraX through view binding
        previewView = myCameraActivityBinding.previewViewFinder;

        // Set up Photo and Video Buttons
        imageCaptureButton = myCameraActivityBinding.imageCaptureButton;
        imageCaptureButton.setOnClickListener(view -> {
            // Check for permissions once the imageCaptureButton was clicked
            if (readExternalStoragePermissionGranted() && writeExternalStoragePermissionGranted()) {
                capturePhoto();
            } else {
                requestMyCameraPermissions();
            }
        });
        videoCaptureButton = myCameraActivityBinding.videoCaptureButton;
        videoCaptureButton.setOnClickListener(view -> {
            // Check for permissions once the videoCaptureButton was clicked
            if (readExternalStoragePermissionGranted() && writeExternalStoragePermissionGranted() && recordAudioPermissionGranted()) {
                captureVideo();
            } else {
                requestMyCameraPermissions();
            }
        });

        // Set up the back Button
        FloatingActionButton backButton = myCameraActivityBinding.backButton;
        Intent navigateToHome = new Intent(this, MainActivity.class);
        backButton.setOnClickListener(view -> startActivity(navigateToHome));
    }

    private boolean cameraIsAvailable() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    // Before the main Camera starts, check if permissions are granted by the user
    private boolean cameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, CAMERA_ACTIVITY_PERMISSIONS[2]) == PackageManager.PERMISSION_GRANTED;
    }

    // Before taking a photo, check if READ and WRITE permissions are granted by the user
    private boolean readExternalStoragePermissionGranted() {
        return ContextCompat.checkSelfPermission(this, CAMERA_ACTIVITY_PERMISSIONS[3]) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean writeExternalStoragePermissionGranted() {
        return ContextCompat.checkSelfPermission(this, CAMERA_ACTIVITY_PERMISSIONS[2]) == PackageManager.PERMISSION_GRANTED;
    }

    // Before recording a video, check if the RECORD_AUDIO permission was granted by the user
    private boolean recordAudioPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, CAMERA_ACTIVITY_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED;
    }

    // If all permissions were not already granted by the user, ask the user for these permissions at runtime
    private void requestMyCameraPermissions() {
        ActivityCompat.requestPermissions(this, CAMERA_ACTIVITY_PERMISSIONS, CAMERA_ACTIVITY_PERMISSION_REQUEST_CODE);
    }

    // Start the main Camera functionality
    private void startCamera(ProcessCameraProvider cameraProvider) {

        // Set up the ImageCapture use case
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        // Set up the VideoCapture use case
        // Build Recorder as a part of the VideoCapture
        QualitySelector videoQualitySelector = QualitySelector.fromOrderedList(Arrays.asList(Quality.UHD, Quality.FHD, Quality.HD, Quality.SD), FallbackStrategy.lowerQualityOrHigherThan(Quality.SD));
        recorder = new Recorder.Builder().setExecutor(ContextCompat.getMainExecutor(this)).setQualitySelector(videoQualitySelector).build();

        // Create the VideoCapture with the Recorder object
        videoCapture = VideoCapture.withOutput(recorder);

        // Set up the CameraSelector use case
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // Set up the Preview use case
        Preview preview = new Preview.Builder()
                .build();

        // Set up the scaleType for the PreviewView
        previewView.setScaleType(PreviewView.ScaleType.FILL_START);

        // Set up the previewView SurfaceProvider
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        try {
            // Unbind use cases before rebinding to the lifecycle
            cameraProvider.unbindAll();
            // Bind all use cases to the lifecycle
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, videoCapture);
        } catch (IllegalArgumentException ie) {
            Log.e("IllegalArgumentException", "Camera use case binding failed", ie);
        }

    }

    // Capture and save photo to phone storage
    private void capturePhoto() {

        // Check if the imageCapture object is not pointing to null
        if (imageCapture != null) {

            // Create the file name string for the photo file config object - photoContentValues
            String photoFileName = "TravelHopper_Photo-" + new SimpleDateFormat("M", Locale.ENGLISH).format(System.currentTimeMillis()) + ".jpg";
            ContentValues photoContentValues = new ContentValues();
            // Use MediaStore to set the photo file for the content resolver to handle
            photoContentValues.put(MediaStore.Images.Media.TITLE, photoFileName);
            photoContentValues.put(MediaStore.Images.Media.DISPLAY_NAME, photoFileName);
            photoContentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            photoContentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                photoContentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + "TravelHopper");
            }

            // Taking the photoContentValues object created, build the output options for the file
            ImageCapture.OutputFileOptions photoOutputFileOptions =
                    new ImageCapture.OutputFileOptions.Builder(getContentResolver(),
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            photoContentValues).build();
            // Capture the photo with the output options and record the capture from callback to output a new photo file
            imageCapture.takePicture(photoOutputFileOptions, ContextCompat.getMainExecutor(this),
                    new ImageCapture.OnImageSavedCallback() {
                        // If the photo has successfully saved, return a success message/Toast
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            Snackbar.make(rootView, "Photo has been saved successfully", Snackbar.LENGTH_SHORT).show();
                        }

                        // If the photo has not successfully saved, return an error message/Toast
                        @Override
                        public void onError(@NonNull ImageCaptureException error) {
                            Snackbar.make(rootView, "Error while saving photo: " + error.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
            );
            // Clear the contents of the photo once the photo file has been successfully saved
            photoContentValues.clear();
        }
    }

    private void captureVideo() {

        // Check if the videoCapture object is not pointing to null
        if (videoCapture != null) {

            // Create the file name string for the video file config object - videoContentValues
            String videoFileName = "TravelHopper_Video-" + new SimpleDateFormat("M", Locale.ENGLISH).format(System.currentTimeMillis()) + ".mp4";
            ContentValues videoContentValues = new ContentValues();
            // Use MediaStore to set the video file for the content resolver to handle
            videoContentValues.put(MediaStore.MediaColumns.TITLE, videoFileName);
            videoContentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, videoFileName);
            videoContentValues.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
            videoContentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                videoContentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Video/" + "TravelHopper");
            }

            // Taking the videoOutputFileOptions object created, build the output options for the file
            MediaStoreOutputOptions videoOutputFileOptions = new MediaStoreOutputOptions.Builder(getContentResolver(),
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI).setContentValues(videoContentValues).build();
            // Retrieve the output stream from the capture and start the recording with the output options for the file
            try {
                recording = videoCapture.getOutput()
                        .prepareRecording(this, videoOutputFileOptions)
                        .withAudioEnabled()
                        .start(ContextCompat.getMainExecutor(this), videoRecordEvent -> {
                            if (videoRecordEvent instanceof VideoRecordEvent.Start) {
                                Snackbar.make(rootView, "Video is now recording", Snackbar.LENGTH_SHORT).show();
                                // Change the icon of the record button when recording starts and the button is clicked
                                videoCaptureButton.setOnClickListener(view -> videoCaptureButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_stop_recording_icon, getApplicationContext().getTheme())));
                            } else if (videoRecordEvent instanceof VideoRecordEvent.Finalize) {
                                // Change the icon of the record button when recording stops and the button is clicked
                                videoCaptureButton.setOnClickListener(view -> {
                                    videoCaptureButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_record_icon, getApplicationContext().getTheme()));
                                    // Check if the video has been successfully captured
                                    VideoRecordEvent.Finalize finalizeEvent =
                                            (VideoRecordEvent.Finalize) videoRecordEvent;
                                    int error = finalizeEvent.getError();
                                    if (error == VideoRecordEvent.Finalize.ERROR_NONE) {
                                        Snackbar.make(rootView, "Video has been captured successfully", Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        recording.close();
                                        recording = null;
                                        Snackbar.make(rootView, "Error while capturing video: " + "${error}", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
            } catch (SecurityException se) {
                se.printStackTrace();
            }
        }
    }

}

