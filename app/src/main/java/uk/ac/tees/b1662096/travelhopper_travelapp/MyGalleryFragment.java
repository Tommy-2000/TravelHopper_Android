package uk.ac.tees.b1662096.travelhopper_travelapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;


import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentMyGalleryBinding;


public class MyGalleryFragment extends Fragment {

    private FragmentMyGalleryBinding fragmentMyGalleryBinding;

    private View rootFragmentView;

    private static final int READ_EXTERNAL_STORAGE_CODE = 101;

    private final ArrayList<Bitmap> mediaArrayList = new ArrayList<>();

    private MyGalleryRecyclerViewAdapter myGalleryRecyclerViewAdapter;

    private static final String ARG_COLUMN_COUNT = "grid-column-count";
    private int gridColumnCount = 2;


    private FloatingActionButton addMediaButton;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MyGalleryFragment() {
    }

    @SuppressWarnings("unused")
    public static MyGalleryFragment getInstance(int columnCount) {
        MyGalleryFragment myGalleryFragment = new MyGalleryFragment();
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putInt(ARG_COLUMN_COUNT, columnCount);
        myGalleryFragment.setArguments(fragmentBundle);
        return myGalleryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gridColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater fragmentInflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the root view from the layout of this fragment and then return it
        fragmentMyGalleryBinding = FragmentMyGalleryBinding.inflate(fragmentInflater, container, false);
        rootFragmentView = fragmentMyGalleryBinding.getRoot();

        // Get the FloatingActionButton from the view binding object
        addMediaButton = fragmentMyGalleryBinding.addMediaButton;

        // Once FAB button is clicked, launch an activity to allow for the user to choose multiple photos or videos
        addMediaButton.setOnClickListener(view -> getMediaFromGallery());

        // Setup the adapter with the correct layout
        RecyclerView myGalleryRecyclerView = fragmentMyGalleryBinding.myGalleryRecyclerView;
        myGalleryRecyclerView.setLayoutManager(new GridLayoutManager(rootFragmentView.getContext(), gridColumnCount));
        myGalleryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myGalleryRecyclerView.setAdapter(new MyGalleryRecyclerViewAdapter(mediaArrayList));

        return rootFragmentView;
    }


    private void getMediaFromGallery() {
        if (ContextCompat.checkSelfPermission(requireActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
        } else {
            Intent getMediaFromGallery = new Intent(Intent.ACTION_GET_CONTENT);
            getMediaFromGallery.setType("media/*");
            // Check if the media from the mime type is an image or video
            if (getMediaFromGallery.getType().equals("image/*")) {
                isMediaImage(getMediaFromGallery.getData().getPath());
            } else if (getMediaFromGallery.getType().equals("video/*")) {
                isMediaVideo(getMediaFromGallery.getData().getPath());
            }
            final PackageManager packageManager = requireActivity().getPackageManager();
            if (getMediaFromGallery.resolveActivity(packageManager) != null) {
                mediaChooserResultLauncher.launch(getMediaFromGallery);
            }
        }
    }

    // Check if the media picked from the user is an image
    private static boolean isMediaImage(String uriMediaPath) {
        String imageMimeType = URLConnection.guessContentTypeFromName(uriMediaPath);
        return imageMimeType != null && imageMimeType.startsWith("image");
    }

    // Check if the media picked from the user is a video
    private static boolean isMediaVideo(String uriMediaPath) {
        String videoMimeType = URLConnection.guessContentTypeFromName(uriMediaPath);
        return videoMimeType != null && videoMimeType.startsWith("video");
    }

    // Resolve the result of the intent
    @SuppressLint("NotifyDataSetChanged")
    ActivityResultLauncher<Intent> mediaChooserResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            activityResult -> {
                // Check if the result from the content picker intent is OK
                if (activityResult.getResultCode() == Activity.RESULT_OK) {
                    // Get the data as an intent to pass
                    Intent retrievedMediaData = activityResult.getData();
                    assert retrievedMediaData != null;
                    // Check if intent's data is not null
                    if (retrievedMediaData.getData() != null) {
                        // Check if the media in the intent is an image
                        if (isMediaImage(retrievedMediaData.getData().getPath())) {
                            Uri imageUriData = retrievedMediaData.getData();
                            try {
                                // Convert the Uri from the intent into a Bitmap, then add the bitmap to the arrayList
                                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.requireContext().getContentResolver(), imageUriData);
                                mediaArrayList.add(imageBitmap);
                                myGalleryRecyclerViewAdapter.notifyDataSetChanged();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // Check if the media in the intent is a video
                        } else if (isMediaVideo(retrievedMediaData.getData().getPath())) {
                            // Convert the Uri from the intent into a Video Thumbnail, then add this to the arrayList
                            Bitmap videoThumbnailBitmap = ThumbnailUtils.createVideoThumbnail(retrievedMediaData.getData().getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                            mediaArrayList.add(videoThumbnailBitmap);
                            myGalleryRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    Snackbar.make(rootFragmentView, "Media has not been picked", Snackbar.LENGTH_SHORT).show();
                }
            });

    @Override
    public void onViewCreated(@NonNull View fragmentView, @Nullable Bundle savedInstanceState) {
        fragmentMyGalleryBinding.myGalleryRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentMyGalleryBinding = null;
    }

}