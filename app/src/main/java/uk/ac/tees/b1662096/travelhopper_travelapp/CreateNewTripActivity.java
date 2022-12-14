package uk.ac.tees.b1662096.travelhopper_travelapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
import java.util.Random;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.ActivityCreateNewTripBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripEntity;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripListViewModel;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripListViewModelFactory;

public class CreateNewTripActivity extends AppCompatActivity {

    private ActivityCreateNewTripBinding activityCreateNewTripBinding;

    private View rootView;

    private TripListViewModel tripListViewModel;

    private TripListViewModelFactory tripListViewModelFactory;

    private static final int READ_EXTERNAL_STORAGE_CODE = 101;

    private MaterialToolbar activityToolbar;

    private FloatingActionButton createNewTripButton;

    private MaterialButton setTripAsFavouriteButton;

    private ImageView newTripImageView;

    private Uri newTripMediaUri;

    private boolean setTripAsFavourite = false;

    private String tripNameString, tripLocationString, tripStartDateString, tripEndDateString, tripDetailsString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this activity
        activityCreateNewTripBinding = ActivityCreateNewTripBinding.inflate(getLayoutInflater());
        rootView = activityCreateNewTripBinding.getRoot();
        setContentView(rootView);

        // Set the navigation back button in the toolbar to navigate to the previous fragment on the MainActivity
        activityToolbar = activityCreateNewTripBinding.createNewTripToolbar;
        activityToolbar.setNavigationIcon(R.drawable.ic_arrow_back_icon);
        activityToolbar.setNavigationOnClickListener(onClickView -> navigateToMainActivity());

        // Get the ViewModel based on the requirements of the parent fragment (MyTripsFragment)
        try {
            tripListViewModelFactory = ViewModelInjector.getTripListViewModelFactory(this);
            tripListViewModel = new ViewModelProvider(this, tripListViewModelFactory).get(TripListViewModel.class);
        } catch (Exception e) {
            Log.e("View Model Observer Exception", e.getLocalizedMessage());
        }

        // Get the TextFields from the view binding object
        TextInputLayout editTripName = activityCreateNewTripBinding.inputTripName;
        tripNameString = Objects.requireNonNull(editTripName.getEditText()).getText().toString();
        TextInputLayout editTripLocation = activityCreateNewTripBinding.inputTripLocation;
        tripLocationString = Objects.requireNonNull(editTripLocation.getEditText()).getText().toString();
        TextInputLayout editTripStartDate = activityCreateNewTripBinding.inputTripStartDate;
        tripStartDateString = Objects.requireNonNull(editTripStartDate.getEditText()).getText().toString();
        TextInputLayout editTripEndDate = activityCreateNewTripBinding.inputTripEndDate;
        tripEndDateString = Objects.requireNonNull(editTripEndDate.getEditText()).getText().toString();
        TextInputLayout editTripDetails = activityCreateNewTripBinding.inputTripDetails;
        tripDetailsString = Objects.requireNonNull(editTripDetails.getEditText()).getText().toString();

        // Get the imageView from the view binding object
        newTripImageView = activityCreateNewTripBinding.mediaFrameView;

        // Get the FloatingActionButton from the view binding object
        createNewTripButton = activityCreateNewTripBinding.createNewTripButton;

        // Get the FloatingActionButton from the view binding object
        setTripAsFavouriteButton = activityCreateNewTripBinding.addTripToFavouritesButton;

        // Initialise and show the DatePicker when either tripStartDate or tripEndDate fields are clicked
        editTripStartDate.setOnClickListener(view -> {
            MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select Start Date").build();
            materialDatePicker.show(getSupportFragmentManager(), materialDatePicker.getTag());
            SimpleDateFormat tripStartDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
            Long tripStartDateLong = materialDatePicker.getSelection();
            Objects.requireNonNull(editTripStartDate.getEditText()).setText(tripStartDateFormat.format(tripStartDateLong));
        });

        editTripEndDate.setOnClickListener(view -> {
            MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select End Date").build();
            materialDatePicker.show(getSupportFragmentManager(), materialDatePicker.getTag());
            SimpleDateFormat tripEndDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
            Long tripEndDateLong = materialDatePicker.getSelection();
            Objects.requireNonNull(editTripEndDate.getEditText()).setText(tripEndDateFormat.format(tripEndDateLong));
        });

        // If the button has been clicked, set tripIsFavourite to true
        setTripAsFavouriteButton.addOnCheckedChangeListener((favouriteButton, isChecked) -> {
            if (isChecked) {
                setTripAsFavourite = true;
                favouriteButton.getIcon();
                ContextCompat.getDrawable(this, R.drawable.ic_favourite_filled_icon);
            } else {
                setTripAsFavourite = false;
                favouriteButton.getIcon();
                ContextCompat.getDrawable(this, R.drawable.ic_favourite_outlined_icon);
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
                navigateToMainActivity();
            }
        });

    }

    private void addNewTrip(String editTripName, String editTripLocation, String tripStartDate, String tripEndDate, String editTripDetails, boolean tripIsFavourite) {
        try {
            tripListViewModel.insertTrip(new TripEntity(new Random().nextInt(), editTripName, editTripLocation, newTripMediaUri.getPath(), tripStartDate, tripEndDate, editTripDetails, tripIsFavourite));
            if (tripIsFavourite) {
                Snackbar.make(rootView, "New favourite trip has been added", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(rootView, "New trip has been added", Snackbar.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("VIEW_MODEL_EXCEPTION", e.getLocalizedMessage());
            e.printStackTrace();
        }
        navigateToMainActivity();
    }


    private void getMediaFromGallery() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
        } else {
            Intent getMediaFromGallery = new Intent(Intent.ACTION_GET_CONTENT);
            getMediaFromGallery.setType("image/*");
            final PackageManager packageManager = getPackageManager();
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
                            CursorLoader mediaCursorLoader = new CursorLoader(this, mediaDataUri, dataMediaColumns, null, null, orderMediaBy);
                            try (Cursor mediaCursor = mediaCursorLoader.loadInBackground()) {
                                if (mediaCursor != null) {
                                    mediaCursor.moveToFirst();
                                    int dataMediaIndex = mediaCursor.getColumnIndexOrThrow(dataMediaColumns[0]);
                                    mediaCursor.close();
                                    Uri mediaContentUri = ContentUris.withAppendedId(mediaDataUri, dataMediaIndex);
                                    // Add the Uri to the ImageView using Glide
                                    Glide.with(this).load(mediaContentUri).centerCrop().placeholder(ResourcesCompat.getDrawable(getResources(), R.drawable.unsplash_placeholder_medium, getTheme())).into(newTripImageView);
                                }
                            }
                        } else {
                            Snackbar.make(rootView, "Media has not been picked", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
            });


    private void navigateToMainActivity() {
        Intent navigateToMainActivity = new Intent(this, MainActivity.class);
        startActivity(navigateToMainActivity);
    }

}