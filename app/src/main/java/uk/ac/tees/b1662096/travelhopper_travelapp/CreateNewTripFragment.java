package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.content.CursorLoader;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentCreateNewTripBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripEntity;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripListViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateNewTripFragment#getNewInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateNewTripFragment extends Fragment {

    public FragmentCreateNewTripBinding fragmentCreateNewTripBinding;

    private View rootFragmentView;

    private TripListViewModel tripListViewModel;

    private static final int READ_EXTERNAL_STORAGE_CODE = 101;

    private MaterialToolbar fragmentToolbar;

    private FloatingActionButton createNewTripButton;

    private MaterialButton setTripAsFavouriteButton;

    private ImageView newTripImageView;

    private Uri newTripMediaUri;

    private boolean setTripAsFavourite = false;

    private Long tripStartDate, tripEndDate;

    public CreateNewTripFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateNewTripFragment.
     */
    public static CreateNewTripFragment getNewInstance() {
        CreateNewTripFragment createNewTripFragment = new CreateNewTripFragment();
        Bundle fragmentBundle = new Bundle();
        createNewTripFragment.setArguments(fragmentBundle);
        return createNewTripFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater fragmentInflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentCreateNewTripBinding = FragmentCreateNewTripBinding.inflate(fragmentInflater, container, false);
        rootFragmentView = fragmentCreateNewTripBinding.getRoot();
        return rootFragmentView;
    }


    @Override
    public void onViewCreated(@NonNull View rootFragmentView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootFragmentView, savedInstanceState);

        // Set the navigation back button in the toolbar to navigate to the previous fragment on the MainActivity
        fragmentToolbar = fragmentCreateNewTripBinding.childFragmentToolbar;
        fragmentToolbar.setNavigationIcon(R.drawable.ic_arrow_back_icon);
        fragmentToolbar.setNavigationOnClickListener(navigateFragmentView -> navigateToParentFragment());

        // Get the ViewModel based on the requirements of the parent fragment (MyTripsFragment)
        tripListViewModel = new ViewModelProvider(requireParentFragment()).get(TripListViewModel.class);

        // Get the TextFields from the view binding object
        TextInputLayout editTripName = fragmentCreateNewTripBinding.inputTripName;
        String tripNameString = Objects.requireNonNull(editTripName.getEditText()).getText().toString();
        TextInputLayout editTripLocation = fragmentCreateNewTripBinding.inputTripLocation;
        String tripLocationString = Objects.requireNonNull(editTripLocation.getEditText()).getText().toString();
        TextInputLayout editTripStartDate = fragmentCreateNewTripBinding.inputTripStartDate;
        String tripStartDateString = Objects.requireNonNull(editTripStartDate.getEditText()).getText().toString();
        TextInputLayout editTripEndDate = fragmentCreateNewTripBinding.inputTripEndDate;
        String tripEndDateString = Objects.requireNonNull(editTripEndDate.getEditText()).getText().toString();
        TextInputLayout editTripDetails = fragmentCreateNewTripBinding.inputTripDetails;
        String tripDetailsString = Objects.requireNonNull(editTripDetails.getEditText()).getText().toString();

        // Get the imageView from the view binding object
        newTripImageView = fragmentCreateNewTripBinding.mediaFrameView;

        // Get the FloatingActionButton from the view binding object
        createNewTripButton = fragmentCreateNewTripBinding.createNewTripButton;

        // Get the FloatingActionButton from the view binding object
        setTripAsFavouriteButton = fragmentCreateNewTripBinding.addTripToFavouritesButton;

        // Initialise and show the DatePicker when either tripStartDate or tripEndDate fields are clicked
        editTripStartDate.setOnClickListener(view -> {
            MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select Start Date").build();
            materialDatePicker.show(getChildFragmentManager(), materialDatePicker.getTag());
            SimpleDateFormat tripStartDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
            tripStartDate = materialDatePicker.getSelection();
            Objects.requireNonNull(editTripStartDate.getEditText()).setText(tripStartDateFormat.format(tripStartDate));
        });

        editTripEndDate.setOnClickListener(view -> {
            MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select End Date").build();
            materialDatePicker.show(getChildFragmentManager(), materialDatePicker.getTag());
            SimpleDateFormat tripEndDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
            tripEndDate = materialDatePicker.getSelection();
            Objects.requireNonNull(editTripEndDate.getEditText()).setText(tripEndDateFormat.format(tripEndDate));
        });

        // If the button has been clicked, set tripIsFavourite to true
        setTripAsFavouriteButton.addOnCheckedChangeListener((favouriteButton, isChecked) -> {
            if (isChecked) {
                setTripAsFavourite = true;
                favouriteButton.getIcon();
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_favourite_filled_icon);
            } else {
                setTripAsFavourite = false;
                favouriteButton.getIcon();
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_favourite_outlined_icon);
            }
        });

        // Get media from the users gallery and add to the ImageView
        newTripImageView.setOnClickListener(view -> {
            getMediaFromGallery();
        });

        // Once button is clicked, call on the view model to insert a new trip
        createNewTripButton.setOnClickListener(insertTripToMyTrips -> {
            if (setTripAsFavouriteButton.isChecked()) {
                addNewTrip(tripNameString, tripLocationString, tripStartDateString, tripEndDateString, tripDetailsString, setTripAsFavourite);
                navigateToParentFragment();
            }
        });


    }

    private void addNewTrip(String editTripName, String editTripLocation, String tripStartDate, String tripEndDate, String editTripDetails, boolean tripIsFavourite) {
        try {
            tripListViewModel.insertTrip(new TripEntity(0, editTripName, editTripLocation, newTripMediaUri.getPath(), tripStartDate, tripEndDate, editTripDetails, tripIsFavourite));
            if (tripIsFavourite) {
                Snackbar.make(rootFragmentView, "New favourite trip has been added", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(rootFragmentView, "New trip has been added", Snackbar.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("VIEW_MODEL_EXCEPTION", e.getLocalizedMessage());
            e.printStackTrace();
        }
        navigateToParentFragment();
    }


    private void getMediaFromGallery() {
        if (ContextCompat.checkSelfPermission(requireActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
        } else {
            Intent getMediaFromGallery = new Intent(Intent.ACTION_GET_CONTENT);
            getMediaFromGallery.setType("image/*");
            final PackageManager packageManager = requireActivity().getPackageManager();
            if (getMediaFromGallery.resolveActivity(packageManager) != null) {
                mediaChooserResultLauncher.launch(getMediaFromGallery);
            }
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
                        if (intentData.getData() != null) {
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
                                    // Add the Uri to the ImageView using Glide
                                    Glide.with(this).load(mediaContentUri).centerCrop().placeholder(ResourcesCompat.getDrawable(requireActivity().getResources(), R.drawable.unsplash_placeholder_medium, requireActivity().getTheme())).into(newTripImageView);
                                }
                            }
                        } else {
                            Snackbar.make(rootFragmentView, "Media has not been picked", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
            });


    private void navigateToParentFragment() {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        // Get the current fragment (CreateNewTripFragment) called currentFragment by its tag
        CreateNewTripFragment currentFragment = (CreateNewTripFragment) getChildFragmentManager().findFragmentByTag("CREATE_NEW_TRIP_FRAGMENT");
        // Get the previous fragment (MyTripsFragment) called parentFragment by its tag
        MyTripsFragment parentFragment = (MyTripsFragment) getParentFragmentManager().findFragmentByTag("MY_TRIPS_FRAGMENT");
        // Set transition animations when the fragments are being switched
        fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_from_right_anim, R.anim.fragment_slide_to_left_anim);
        // Replace the current child fragment with the parent fragment (MyTripsFragment)
        if (parentFragment != null) {
            fragmentTransaction.replace(fragmentCreateNewTripBinding.rootFragmentLayout.getId(), parentFragment);
        }
        // Ensure that this fragment is not added to the backstack
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commit();
        // Make sure that the child fragment disappears when navigating back to the parent fragment
        if (currentFragment == null) {
            fragmentCreateNewTripBinding.createNewTripFragmentLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentCreateNewTripBinding = null;
    }

}