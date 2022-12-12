package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentGoogleAccountBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.ui.firebaseAuth.FirebaseAuthViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GoogleAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoogleAccountFragment extends Fragment {

    private FragmentGoogleAccountBinding fragmentGoogleAccountBinding;

    private View rootFragmentView;

    private FirebaseAuthViewModel firebaseAuthViewModel;

    private MaterialToolbar fragmentToolbar;

    private MaterialButton signOutButton, removeGoogleAccountButton;

    private ImageView googleAccountProfileIcon;

    private Drawable googleAccountProfileDrawable;

    private MaterialTextView googleAccountID, googleAccountDisplayName, googleAccountGivenName, googleAccountEmail;


    public GoogleAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment GoogleAccountFragment.
     */
    public static GoogleAccountFragment newInstance() {
        GoogleAccountFragment googleAccountFragment = new GoogleAccountFragment();
        Bundle fragmentBundle = new Bundle();
        googleAccountFragment.setArguments(fragmentBundle);
        return googleAccountFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater fragmentInflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentGoogleAccountBinding = FragmentGoogleAccountBinding.inflate(fragmentInflater, container, false);
        rootFragmentView = fragmentGoogleAccountBinding.getRoot();
        return rootFragmentView;
    }


    @Override
    public void onViewCreated(@NonNull View fragmentView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragmentView, savedInstanceState);

        // Set the navigation back button in the toolbar to navigate to the previous fragment on the MainActivity
        fragmentToolbar = fragmentGoogleAccountBinding.googleAccountToolbar;
        fragmentToolbar.setNavigationIcon(R.drawable.ic_arrow_back_icon);
        fragmentToolbar.setNavigationOnClickListener(navigateFragmentView -> navigateToParentFragment());


        googleAccountProfileIcon = fragmentGoogleAccountBinding.googleAccountProfileIcon;
        googleAccountID = fragmentGoogleAccountBinding.googleAccountID;
        googleAccountDisplayName = fragmentGoogleAccountBinding.googleAccountDisplayName;
        googleAccountGivenName = fragmentGoogleAccountBinding.googleAccountGivenName;
        googleAccountEmail = fragmentGoogleAccountBinding.googleAccountEmail;

        // Check if the user if currently signed in, then show their details
        checkGoogleAccountSignIn();

        // Get the buttons from the XML view
        signOutButton = fragmentGoogleAccountBinding.signOutButton;
        signOutButton.setOnClickListener(view -> {
            // Check if the user is signed in before signing them out
            if (checkGoogleAccountSignIn()){
                signOutGoogleAccount();
            } else {
                exitApplication();
            }
        });
        removeGoogleAccountButton = fragmentGoogleAccountBinding.removeGoogleAccountButton;
        removeGoogleAccountButton.setOnClickListener(v -> AuthUI.getInstance().delete(requireContext()).addOnCompleteListener(deleteUserAccountTask -> {
            Log.d("USER_ACCOUNT_DELETION_SUCCESSFUL", "User account has been successfully removed");
            Snackbar.make(fragmentView, "Your account has been successfully removed from the app", Snackbar.LENGTH_SHORT).show();
        }));

    }

    private boolean checkGoogleAccountSignIn() {
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(requireActivity().getApplicationContext());
        // Ensure that the Google Account is not null
        if (googleAccount != null) {
            setGoogleDetails(googleAccount);
            return true;
        } else {
            setGuestDetails();
            Snackbar.make(rootFragmentView, "You are currently not logged in, showing guest details", Snackbar.LENGTH_SHORT).show();
            Log.e("GOOGLE_ACCOUNT_ERROR", "Unable to retrieve Google Account details, using default values instead");
            return false;
        }
    }

    private void setGoogleDetails(GoogleSignInAccount googleAccount) {
        // Get the user's details from their Google account
        googleAccountID.setText(googleAccount.getId());
        googleAccountDisplayName.setText(googleAccount.getDisplayName());
        googleAccountGivenName.setText(googleAccount.getGivenName());
        googleAccountEmail.setText(googleAccount.getEmail());
        googleAccountProfileIcon.setImageDrawable(getGoogleAccountProfileDrawable(googleAccount));
    }

    private void setGuestDetails() {
        // Get the user's details from their Google account
        googleAccountID.setText(R.string.guestID);
        googleAccountDisplayName.setText(R.string.guestDisplayName);
        googleAccountGivenName.setText(R.string.guestGivenName);
        googleAccountEmail.setText(R.string.guestEmail);
        googleAccountProfileIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_account_circle_icon, requireActivity().getTheme()));
    }


    private Drawable getGoogleAccountProfileDrawable(GoogleSignInAccount googleAccount) {
        Uri googleAccountProfileUri = Objects.requireNonNull(googleAccount).getPhotoUrl();
        try {
            if (googleAccountProfileUri != null) {
                InputStream uriInputStream = requireActivity().getContentResolver().openInputStream(googleAccountProfileUri);
                googleAccountProfileDrawable = Drawable.createFromStream(uriInputStream, googleAccountProfileUri.toString());
            } else {
                googleAccountProfileDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_account_circle_icon, requireActivity().getTheme());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return googleAccountProfileDrawable;
    }

    private void signOutGoogleAccount() {
        // Sign out the user from the app itself using Firebase Auth
        AuthUI.getInstance().signOut(requireContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("USER_SIGN_OUT_SUCCESSFUL", "User has been successfully signed out");
            }
        });
    }

    private void exitApplication() {
        // Exit from the application altogether
        requireActivity().finishAndRemoveTask();
    }

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
            fragmentTransaction.replace(fragmentGoogleAccountBinding.rootFragmentLayout.getId(), parentFragment);
        }
        // Ensure that this fragment is not added to the backstack
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commit();
        // Make sure that the child fragment disappears when navigating back to the parent fragment
        if (currentFragment == null) {
            fragmentGoogleAccountBinding.rootFragmentLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentGoogleAccountBinding = null;
    }
}