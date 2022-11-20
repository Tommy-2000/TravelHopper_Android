package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentMyGalleryBinding;


public class MyGalleryFragment extends Fragment {

    private FragmentMyGalleryBinding fragmentMyGalleryBinding;

    private View rootFragmentView;

    private static final int READ_EXTERNAL_STORAGE_CODE = 101;

    private final ArrayList<Uri> galleryMediaList = new ArrayList<>();

    private static final String ARG_COLUMN_COUNT = "grid-column-count";
    private int gridColumnCount = 2;

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

        FloatingActionButton addMediaButton = fragmentMyGalleryBinding.addMediaButton;

        // Once FAB button is clicked, launch an activity to allow for the user to choose multiple photos or videos
        addMediaButton.setOnClickListener(view -> getMediaFromExternalStorage());

        // Setup the adapter with the correct layout
        RecyclerView myGalleryRecyclerView = fragmentMyGalleryBinding.myGalleryRecyclerView;
        myGalleryRecyclerView.setLayoutManager(new GridLayoutManager(rootFragmentView.getContext(), gridColumnCount));
        myGalleryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myGalleryRecyclerView.setAdapter(new MyGalleryRecyclerViewAdapter(galleryMediaList));

        return rootFragmentView;
    }

    private void getMediaFromExternalStorage() {
        if (ContextCompat.checkSelfPermission(requireActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
        } else {
            Intent getMediaFromGallery = new Intent(Intent.ACTION_GET_CONTENT);
            getMediaFromGallery.setType("image/*");
            String[] mediaMimeTypes = {"image/*", "video/*"};
            getMediaFromGallery.putExtra(Intent.EXTRA_MIME_TYPES, mediaMimeTypes);
            getMediaFromGallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            if (getMediaFromGallery.resolveActivity(requireActivity().getPackageManager()) != null) {
                mediaChooserResultLauncher.launch(getMediaFromGallery);
            }
        }
    }

    ActivityResultLauncher<Intent> mediaChooserResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            activityResult -> {
                if (activityResult.getResultCode() == Activity.RESULT_OK) {
                    Intent retrievedMediaData = activityResult.getData();
                    assert retrievedMediaData != null;
                    if (retrievedMediaData.getClipData() != null) {
                        int clipDataCount = retrievedMediaData.getClipData().getItemCount();
                        for (int i = 0; i < clipDataCount; i++) {
                            galleryMediaList.add(retrievedMediaData.getClipData().getItemAt(i).getUri());
                        }
                    } else if (retrievedMediaData.getData() != null) {
                        Uri mediaUriData = retrievedMediaData.getData();
                        galleryMediaList.add(mediaUriData);
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