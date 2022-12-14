package uk.ac.tees.b1662096.travelhopper_travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.ActivityGoogleAccountBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.ui.firebaseAuth.FirebaseAuthViewModel;

public class GoogleAccountActivity extends AppCompatActivity {

    private ActivityGoogleAccountBinding activityGoogleAccountBinding;

    private View rootView;

    private FirebaseAuth firebaseAuth;

    private SignInClient oneTapClient;

    private GoogleSignInClient googleSignInClient;

    private FirebaseAuthViewModel firebaseAuthViewModel;

    private MaterialToolbar fragmentToolbar;

    private MaterialButton signOutButton, removeGoogleAccountButton;

    private ImageView googleAccountProfileIcon;

    private Drawable googleAccountProfileDrawable;

    private MaterialTextView firebaseUserId, firebaseUserDisplayName, firebaseUserEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        activityGoogleAccountBinding = ActivityGoogleAccountBinding.inflate(getLayoutInflater());
        rootView = activityGoogleAccountBinding.getRoot();


        firebaseAuth = FirebaseAuth.getInstance();

        // Set the navigation back button in the toolbar to navigate to the previous fragment on the MainActivity
        fragmentToolbar = activityGoogleAccountBinding.googleAccountToolbar;
        fragmentToolbar.setNavigationIcon(R.drawable.ic_arrow_back_icon);
        fragmentToolbar.setNavigationOnClickListener(navigateFragmentView -> navigateToMainActivity());


        googleAccountProfileIcon = activityGoogleAccountBinding.googleAccountProfileIcon;
        firebaseUserId = activityGoogleAccountBinding.googleAccountID;
        firebaseUserDisplayName = activityGoogleAccountBinding.googleAccountDisplayName;
        firebaseUserEmail = activityGoogleAccountBinding.googleAccountEmail;

        // Check if the user if currently signed in, then show their details
        checkGoogleAccountSignIn();

        // Get the buttons from the XML view
        signOutButton = activityGoogleAccountBinding.signOutButton;
        signOutButton.setOnClickListener(view -> {
            // Check if the user is signed in before signing them out
            if (checkGoogleAccountSignIn()) {
                signOutGoogleAccount();
            } else {
                exitApplication();
            }
        });
        removeGoogleAccountButton = activityGoogleAccountBinding.removeGoogleAccountButton;
        removeGoogleAccountButton.setOnClickListener(v -> AuthUI.getInstance().delete(this).addOnCompleteListener(deleteUserAccountTask -> {
            Log.d("USER_ACCOUNT_DELETION_SUCCESSFUL", "User account has been successfully removed");
            Snackbar.make(rootView, "Your account has been successfully removed from the app", Snackbar.LENGTH_SHORT).show();
        }));

        setContentView(rootView);
    }


    private boolean checkGoogleAccountSignIn() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        // Ensure that the Google Account is not null
        if (firebaseUser != null) {
            setGoogleDetails(firebaseUser);
            return true;
        } else {
            setGuestDetails();
            Snackbar.make(rootView, "You are currently not logged in, showing guest details", Snackbar.LENGTH_SHORT).show();
            Log.e("GOOGLE_ACCOUNT_ERROR", "Unable to retrieve Google Account details, using default values instead");
            return false;
        }
    }


    private void setGoogleDetails(FirebaseUser firebaseUser) {
        // Get the user's details from their Google account
        firebaseUserId.setText(firebaseUser.getUid());
        firebaseUserDisplayName.setText(firebaseUser.getDisplayName());
        firebaseUserEmail.setText(firebaseUser.getEmail());
        googleAccountProfileIcon.setImageDrawable(getFirebaseUserProfileDrawable(firebaseUser));
    }


    private Drawable getFirebaseUserProfileDrawable(FirebaseUser firebaseUser) {
        Uri googleAccountProfileUri = Objects.requireNonNull(firebaseUser).getPhotoUrl();
        try {
            if (googleAccountProfileUri != null) {
                InputStream uriInputStream = getContentResolver().openInputStream(googleAccountProfileUri);
                googleAccountProfileDrawable = Drawable.createFromStream(uriInputStream, googleAccountProfileUri.toString());
            } else {
                googleAccountProfileDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_account_circle_icon, getTheme());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return googleAccountProfileDrawable;
    }


    private void setGuestDetails() {
        // Get the user's details from their Google account
        firebaseUserId.setText(R.string.guestID);
        firebaseUserDisplayName.setText(R.string.guestDisplayName);
        firebaseUserEmail.setText(R.string.guestEmail);
        googleAccountProfileIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_account_circle_icon, getTheme()));
    }


    private void signOutGoogleAccount() {
        // Sign out the user from the app itself using Firebase Auth
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("USER_SIGN_OUT_SUCCESSFUL", "User has been successfully signed out");
            }
        });
    }

    private void navigateToMainActivity() {
        Intent navigateToMainActivity = new Intent(this, MainActivity.class);
        startActivity(navigateToMainActivity);
    }


    private void exitApplication() {
        // Exit from the application altogether
        finishAndRemoveTask();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityGoogleAccountBinding = null;
    }
}