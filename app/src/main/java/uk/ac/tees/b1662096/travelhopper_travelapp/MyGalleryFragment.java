package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentMyGalleryBinding;


public class MyGalleryFragment extends Fragment {

    private Context myGalleryFragmentContext;

    private FragmentMyGalleryBinding fragmentMyGalleryBinding;

    private static final int READ_EXTERNAL_STORAGE_CODE = 101;

    private static final String ARG_COLUMN_COUNT = "grid-column-count";
    private int gridColumnCount = 2;

    private FloatingActionButton addPhotoButton;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MyGalleryFragment() {
    }

    @SuppressWarnings("unused")
    public static MyGalleryFragment newInstance(int columnCount) {
        MyGalleryFragment fragment = new MyGalleryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gridColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        retrieveMediaFromStorage();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentMyGalleryBinding = FragmentMyGalleryBinding.inflate(inflater, container, false);
        View fragmentView = fragmentMyGalleryBinding.getRoot();

        myGalleryFragmentContext = fragmentView.getContext();

        addPhotoButton = fragmentMyGalleryBinding.addPhotoButton;

        // Once FAB button is clicked, launch an activity to allow for the user to choose a photo
        addPhotoButton.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(myGalleryFragmentContext, Manifest.permission.READ_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) myGalleryFragmentContext, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
            } else {
                Intent getPhotos = new Intent(Intent.ACTION_GET_CONTENT);
                getPhotos.setType("image/*");
                getPhotos.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                Intent photoChooser = Intent.createChooser(getPhotos, "Select Picture");
                if (getPhotos.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(photoChooser);
                }
            }
        });


        // Set the adapter
        if (fragmentView instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) fragmentView;
            if (gridColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(myGalleryFragmentContext));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(myGalleryFragmentContext, gridColumnCount));
            }
//            recyclerView.setAdapter(new MyGalleryRecyclerViewAdapter());
        }
        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        fragmentMyGalleryBinding.myGalleryRecyclerview.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentMyGalleryBinding = null;
    }


    private void retrieveMediaFromStorage() {

    }

}