package uk.ac.tees.b1662096.travelhopper_travelapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentMyGalleryBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.MediaCardViewBinding;


public class MyGalleryFragment extends Fragment {

    private FragmentMyGalleryBinding fragmentMyGalleryBinding;

    private MediaCardViewBinding bindingMediaCardView;

    private View rootFragmentView;

    private static final int READ_EXTERNAL_STORAGE_CODE = 101;

    private ArrayList<Uri> mediaArrayList;

    private int imageWidthPixels;

    private int imageHeightPixels;

    private RecyclerView myGalleryRecyclerView;

    private MyGalleryRecyclerViewAdapter myGalleryRecyclerViewAdapter;

    private static final String ARG_COLUMN_COUNT = "grid-column-count";
    private int gridColumnCount = 1;


    private FloatingActionButton addMediaButton;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MyGalleryFragment() {
    }

    @SuppressWarnings("unused")
    public static MyGalleryFragment getNewInstance(int columnCount) {
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
        // Manage navigation callback when back button is pressed
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Ensure that the parent fragment (MyGalleryFragment) loads into the NavHostFragment while using the bottom navigation menu
                NavHostFragment.findNavController(MyGalleryFragment.this).navigateUp();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater fragmentInflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the root view from the layout of this fragment and then return it
        fragmentMyGalleryBinding = FragmentMyGalleryBinding.inflate(fragmentInflater, container, false);
        rootFragmentView = fragmentMyGalleryBinding.getRoot();

        return rootFragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View fragmentView, @Nullable Bundle savedInstanceState) {

        mediaArrayList = new ArrayList<>();

        imageWidthPixels = 640;
        imageHeightPixels = 427;

        // Get the FloatingActionButton from the view binding object
        addMediaButton = fragmentMyGalleryBinding.addMediaButton;

        // Once FAB button is clicked, launch an activity to allow for the user to choose multiple photos or videos
        // Also check for permissions on button click before launching the ActivityResultLauncher
        addMediaButton.setOnClickListener(view -> {
            getMediaFromGallery();
        });

        // Setup the adapter with the correct layout
        myGalleryRecyclerView = fragmentMyGalleryBinding.myGalleryRecyclerView;
    }


    private void getMediaFromGallery() {
        // Check for permissions before sending the intent to access photos from the user's gallery
        if (ContextCompat.checkSelfPermission(requireActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            Intent getMediaFromGallery = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Allow for multiple images and video thumbnails to be picked
            getMediaFromGallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            // Set the MIME data type of the content that is being retrieved by the intent
            final PackageManager packageManager = requireActivity().getPackageManager();
            if (getMediaFromGallery.resolveActivity(packageManager) != null) {
                mediaChooserResultLauncher.launch(getMediaFromGallery);
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
        }
    }

    // Resolve the result of the intent
    ActivityResultLauncher<Intent> mediaChooserResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            activityResult -> {
                // Check if the result from the content picker intent is OK
                if (activityResult.getResultCode() == Activity.RESULT_OK) {
                    // Get the data as an intent to pass
                    Intent intentData = activityResult.getData();
                    // Check if the Intent is null
                    if (intentData != null) {
                        // Check if the clip data contained in the intent is null
                        if (intentData.getClipData() != null) {
                            ClipData retrievedClipData = intentData.getClipData();
                            // Loop through the clip data and get each item
                            for (int i = 0; i < retrievedClipData.getItemCount(); i++) {
                                ClipData.Item clipDataItem = retrievedClipData.getItemAt(i);
                                // Get the Uri from the clip data item
                                Uri clipDataUri = clipDataItem.getUri();
                                String[] clipDataMediaColumns = {MediaStore.Images.Media.DATA};
                                String orderMediaBy = MediaStore.Images.Media._ID;
                                CursorLoader mediaCursorLoader = new CursorLoader(requireContext(), clipDataUri, clipDataMediaColumns, null, null, orderMediaBy);
                                try (Cursor mediaCursor = mediaCursorLoader.loadInBackground()) {
                                    if (mediaCursor != null) {
                                        mediaCursor.moveToFirst();
                                        int clipDataMediaIndex = mediaCursor.getColumnIndexOrThrow(clipDataMediaColumns[0]);
                                        mediaCursor.close();
                                        Uri mediaContentUri = ContentUris.withAppendedId(clipDataUri, clipDataMediaIndex);
                                        mediaArrayList.add(mediaContentUri);
                                        Log.d("IMAGES_ADDED", "Images have been added to the RecyclerView");
                                        // Set the RecyclerView for the adapter
                                        myGalleryRecyclerViewAdapter = new MyGalleryRecyclerViewAdapter(requireActivity().getApplicationContext(), mediaArrayList);
                                        // Set the GridLayoutManager for the RecyclerView
                                        myGalleryRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), gridColumnCount, GridLayoutManager.VERTICAL, false));
                                        myGalleryRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                        myGalleryRecyclerView.setHasFixedSize(true);
                                        myGalleryRecyclerView.setAdapter(myGalleryRecyclerViewAdapter);
                                    }
                                }
                            }
                        } else if (intentData.getData() != null) {
                            String[] dataMediaColumns = {MediaStore.Images.Media.DATA};
                            String orderMediaBy = MediaStore.Images.Media._ID;
                            Uri mediaDataUri = intentData.getData();
                            CursorLoader mediaCursorLoader = new CursorLoader(requireContext(), mediaDataUri, dataMediaColumns, null, null, orderMediaBy);
                            try (Cursor mediaCursor = mediaCursorLoader.loadInBackground()) {
                                if (mediaCursor != null) {
                                    mediaCursor.moveToFirst();
                                    int dataMediaIndex = mediaCursor.getColumnIndexOrThrow(dataMediaColumns[0]);
                                    mediaCursor.close();
                                    Uri mediaContentUri = ContentUris.withAppendedId(mediaDataUri, dataMediaIndex);
                                    mediaArrayList.add(mediaContentUri);
                                    Log.d("IMAGE_ADDED", "Image has been added to the RecyclerView");
                                    // Set up the adapter for the RecyclerView
                                    myGalleryRecyclerViewAdapter = new MyGalleryRecyclerViewAdapter(requireActivity().getApplicationContext(), mediaArrayList);
                                    // Set the GridLayoutManager for the RecyclerView
                                    myGalleryRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), gridColumnCount, GridLayoutManager.VERTICAL, false));
                                    myGalleryRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                    myGalleryRecyclerView.setHasFixedSize(true);
                                    myGalleryRecyclerView.setAdapter(myGalleryRecyclerViewAdapter);
                                }
                            }
                        }
                    }
                } else {
                    Log.e("INTENT_DATA_IS_NULL", "Intent data received from activity is null");
                }
            });



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentMyGalleryBinding = null;
    }


    private class MyGalleryRecyclerViewAdapter extends RecyclerView.Adapter<MyGalleryRecyclerViewAdapter.MyGalleryViewHolder> {

        Context adapterContext;
        ArrayList<Uri> mediaArrayList;


        public MyGalleryRecyclerViewAdapter(Context adapterContext, ArrayList<Uri> mediaArrayList) {
            this.adapterContext = adapterContext;
            this.mediaArrayList = mediaArrayList;
        }

        private class MyGalleryViewHolder extends RecyclerView.ViewHolder {

            final ImageView imageView;

            public MyGalleryViewHolder(MediaCardViewBinding mediaCardViewBinding) {
                super(mediaCardViewBinding.getRoot());
                imageView = mediaCardViewBinding.mediaImageView;
            }
        }

        @NonNull
        @Override
        public MyGalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            bindingMediaCardView = MediaCardViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new MyGalleryViewHolder(bindingMediaCardView);
        }

        @Override
        public void onBindViewHolder(final MyGalleryViewHolder myGalleryViewHolder, int position) {
            // Use Glide to load the thumbnails of each image into the viewHolder
            RequestBuilder<Drawable> thumbnailRequest = Glide.with(myGalleryViewHolder.itemView.getContext()).load(mediaArrayList.get(position));

            // Use Glide to load the Uri's into the binding view by using the context of the ViewHolder items
            Glide.with(myGalleryViewHolder.itemView.getContext())
                    .load(mediaArrayList.get(position))
                    .thumbnail(thumbnailRequest)
                    .centerCrop()
                    .placeholder(R.drawable.unsplash_placeholder_small)
                    .error(R.drawable.ic_error_icon)
                    .into(bindingMediaCardView.mediaImageView);


            // Check that the item in the Binding View is clickable
            bindingMediaCardView.mediaCardView.setOnClickListener(view -> Snackbar.make(rootFragmentView, "Image has been clicked", Snackbar.LENGTH_SHORT).show());
//        // Set a fade-in animation for each item in the RecyclerView
//        setRecyclerViewAnimation(myGalleryViewHolder.mediaImageView, position);
        }

        @Override
        public int getItemCount() {
            return mediaArrayList.size();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public void updateGalleryList(ArrayList<Uri> updatedMediaArrayList) {
            // Clear the old Uris in the list and then initialise the new Uris
            mediaArrayList.clear();
            mediaArrayList = updatedMediaArrayList;
            notifyDataSetChanged();
//            notifyItemChanged(mediaPathArrayList.size());
        }
    }

//    class RecyclerViewPreloadProvider implements ListPreloader.PreloadModelProvider {
//
//        @NonNull
//        @Override
//        public List getPreloadItems(int position) {
//            Uri mediaUri = mediaArrayList.get(position);
//            if (mediaUri == null) {
//                return Collections.emptyList();
//            }
//            return Collections.singletonList(mediaUri);
//        }
//
//        @Nullable
//        @Override
//        public RequestBuilder<?> getPreloadRequestBuilder(@NonNull Object mediaUri) {
//            return Glide.with(MyGalleryFragment.this).load(mediaUri).override(imageWidthPixels, imageHeightPixels);
//        }
//    }
}

