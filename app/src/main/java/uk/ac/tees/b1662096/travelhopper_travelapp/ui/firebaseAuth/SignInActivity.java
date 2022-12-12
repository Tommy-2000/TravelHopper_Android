package uk.ac.tees.b1662096.travelhopper_travelapp.ui.firebaseAuth;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import uk.ac.tees.b1662096.travelhopper_travelapp.BuildConfig;

import android.util.Log;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.List;

import uk.ac.tees.b1662096.travelhopper_travelapp.MainActivity;
import uk.ac.tees.b1662096.travelhopper_travelapp.data.model.TravelHopperUser;
import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding activitySignInBinding;

    private View rootView;

    private FirebaseAuthViewModel firebaseAuthViewModel;


    private FirebaseAuth firebaseAuth;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    private boolean showOneTapUI = true;

    private String TRAVELHOPPER_USER;

    private String FIREBASE_USER;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        rootView = activitySignInBinding.getRoot();
        setContentView(rootView);

        firebaseAuthViewModel = new ViewModelProvider(this).get(FirebaseAuthViewModel.class);

        // Get the Google Sign In Client to be used with the Google Sign In button with predefined sign in options
        getOneTapSignInClient();

        // Get an instance of FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        // IMPORTANT! - Make sure to first test the application with Firebase emulators before using in production!
        firebaseAuth.useEmulator("127.0.0.1", 9099);

        // Open the One Tap UI Sign In
        signInWithOneTap();

        CircularProgressIndicator loadingProgressBar = activitySignInBinding.circularProgressBar;

        MaterialButton signInButton = activitySignInBinding.signInButton;
        signInButton.setOnClickListener(view -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            createEmailPasswordSignInIntent();
        });


        MaterialButton continueAsGuestButton = activitySignInBinding.continueAsGuest;
        continueAsGuestButton.setOnClickListener(view -> {
            navigateToMainActivity();
        });
    }

    private SignInClient getOneTapSignInClient() {
        // Set up the One Tap UI for Google Accounts
        return Identity.getSignInClient(this);
    }

    private void signInWithOneTap() {
        oneTapClient = getOneTapSignInClient();
        // Set up the request to sign with using One Tap UI
        signInRequest = BeginSignInRequest
                .builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .setAutoSelectEnabled(false)
                .build();

        // Only if showOneTapUI is true, begin the sign in process
        if (showOneTapUI) {
            // Begin sign in process using One Tap UI
            oneTapClient.beginSignIn(signInRequest).addOnSuccessListener(this, beginSignInResult -> {
                IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(beginSignInResult.getPendingIntent().getIntentSender()).build();
                oneTapCredentialResultLauncher.launch(intentSenderRequest);
            }).addOnFailureListener(this, e -> {
                // Because no Google account has been found, allow for the user to create an account with their email and password
                Log.e("NO_GOOGLE_ACCOUNT_FOUND", e.getLocalizedMessage());
                Snackbar.make(rootView, "No Google Account has been found, try logging in with your email and password", Snackbar.LENGTH_SHORT).show();
            });
        }
    }


//    private LiveData<TravelHopperUser> createNewEmailPasswordUser(String userEmail, String userPassword) {
//        return firebaseAuthViewModel.createNewEmailPasswordUser(userEmail, userPassword);
//    }

    private void createEmailPasswordSignInIntent() {
        List<AuthUI.IdpConfig> emailBuilder = List.of(new AuthUI.IdpConfig.EmailBuilder().build());
        Intent signInEmailPasswordIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(emailBuilder)
                .build();
        signInWithEmailPasswordLauncher.launch(signInEmailPasswordIntent);
    }

    private final ActivityResultLauncher<Intent> signInWithEmailPasswordLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            emailPasswordResult -> onEmailPasswordSignIn(emailPasswordResult));

    private void onEmailPasswordSignIn(FirebaseAuthUIAuthenticationResult emailPasswordResult) {
        if (emailPasswordResult.getResultCode() == Activity.RESULT_OK) {
            FirebaseUser emailPasswordUser = FirebaseAuth.getInstance().getCurrentUser();
            if (emailPasswordUser != null){
                Log.d("FIREBASE_SIGN_IN_SUCCESSFUL", "Successfully signed in through Firebase");
                navigateToMainActivityWithFirebaseUser(emailPasswordUser);
            } else {
                Log.e("FIREBASE_SIGN_IN_ERROR","Unable to get details from Firebase, please try again later");
            }
        }
    }


    ActivityResultLauncher<IntentSenderRequest> oneTapCredentialResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                try {
                    SignInCredential googleCredential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                    String googleIdToken = googleCredential.getGoogleIdToken();
                    if (googleIdToken != null) {
                        getGoogleAuthCredential(googleIdToken);
                    } else {
                        Log.e("GOOGLE_ID_TOKEN_ERROR", "No Google ID Token available");
                        Snackbar.make(rootView, "No Google ID Token available", Snackbar.LENGTH_SHORT).show();
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                    if (e.getStatusCode() == CommonStatusCodes.CANCELED) {
                        // If the status returned is CANCELLED, don't show the One Tap UI
                        Log.d("ONE_TAP_DIALOG_CLOSED", "One Tap UI was closed");
                        showOneTapUI = false;
                    } else if (e.getStatusCode() == CommonStatusCodes.NETWORK_ERROR) {
                        // If the status returned is NETWORK_ERROR, show the One Tap UI to allow for the user to try again
                        Log.d("ONE_TAP_NETWORK_ERROR", "One Tap UI encountered a network error, please try again");
                        showOneTapUI = true;
                    }
                    // Get the Exception from the result of the intent sender
                    assert result.getData() != null;
                    Exception resultException = (Exception) result.getData().getSerializableExtra(ActivityResultContracts.StartIntentSenderForResult.EXTRA_SEND_INTENT_EXCEPTION);
                    Log.e("ONE_TAP_RESULT_EXCEPTION", "Unable to start One Tap UI: " + resultException.getLocalizedMessage());
                }
            }
            // Get the client for One Tap UI
            oneTapClient = Identity.getSignInClient(getApplicationContext());

        }
    });

    private void getGoogleAuthCredential(String googleTokenID) {
        // Retrieve the credentials of the Google Account using the ID token given either from the Google sign in button or the One Tap UI
        AuthCredential googleAuthCredential = GoogleAuthProvider.getCredential(googleTokenID, null);
        signInWithGoogleCredential(googleAuthCredential); // Use the credential to sign in with Google
    }

    private void signInWithGoogleCredential(AuthCredential googleAuthCredential) {
        firebaseAuthViewModel.signInWithGoogle(googleAuthCredential);
    }


    private void navigateToMainActivityWithUser(TravelHopperUser travelHopperUser) {
        // Once a new user account is created, navigate to the MainActivity
        Intent navigateToMainActivity = new Intent(SignInActivity.this, MainActivity.class);
        navigateToMainActivity.putExtra(TRAVELHOPPER_USER, travelHopperUser);
        startActivity(navigateToMainActivity);
        // Close this current activity
        finish();
    }

    private void navigateToMainActivityWithFirebaseUser(FirebaseUser firebaseUser) {
        // Once a new user account is created, navigate to the MainActivity
        Intent navigateToMainActivity = new Intent(SignInActivity.this, MainActivity.class);
        navigateToMainActivity.putExtra(FIREBASE_USER, firebaseUser);
        startActivity(navigateToMainActivity);
        // Close this current activity
        finish();
    }

    private void navigateToMainActivity() {
        // Navigate to the MainActivity
        Intent navigateToMainActivity = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(navigateToMainActivity);
        // Close this current activity
        finish();
    }

    private void snackBarMessage(String userName) {
        Snackbar.make(rootView, "Welcome " + userName + "!", Snackbar.LENGTH_SHORT).show();
    }

}