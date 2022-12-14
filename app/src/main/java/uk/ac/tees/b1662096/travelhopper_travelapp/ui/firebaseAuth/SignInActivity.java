package uk.ac.tees.b1662096.travelhopper_travelapp.ui.firebaseAuth;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import uk.ac.tees.b1662096.travelhopper_travelapp.BuildConfig;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import uk.ac.tees.b1662096.travelhopper_travelapp.MainActivity;
import uk.ac.tees.b1662096.travelhopper_travelapp.ViewModelInjector;
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

    private TextInputLayout editEmailLayout, editPasswordLayout;

    private EditText editEmailText, editPasswordText;

    private String travelHopperEmailString, travelHopperPasswordString;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        rootView = activitySignInBinding.getRoot();
        setContentView(rootView);


        // Instantiate the view model with its factory class
        FirebaseAuthViewModelFactory viewModelFactory = ViewModelInjector.getFirebaseAuthViewModelFactory();
        firebaseAuthViewModel = new ViewModelProvider(this, viewModelFactory).get(FirebaseAuthViewModel.class);

        // Get an instance of FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
//        // IMPORTANT! - Make sure to first test the application with Firebase emulators before using in production!
//        firebaseAuth.useEmulator("127.0.0.1", 9099);

        // Get the Google Sign In Client to be used with the Google Sign In button with predefined sign in options
        getOneTapSignInClient();

        // Open the One Tap UI Sign In
        signInWithOneTap();


        editEmailLayout = activitySignInBinding.usernameInputLayout;
        editPasswordLayout = activitySignInBinding.passwordInputLayout;
        editEmailText = Objects.requireNonNull(editEmailLayout).getEditText();
        editPasswordText = Objects.requireNonNull(editPasswordLayout).getEditText();
        travelHopperEmailString = Objects.requireNonNull(editEmailText).toString();
        travelHopperPasswordString = Objects.requireNonNull(editPasswordText).toString();

        CircularProgressIndicator loadingProgressBar = activitySignInBinding.circularProgressBar;

        MaterialButton signInButton = activitySignInBinding.signInButton;
        signInButton.setOnClickListener(signInView -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            checkSignInDetails(travelHopperEmailString, travelHopperPasswordString);
        });


        MaterialButton registerButton = activitySignInBinding.registerButton;
        registerButton.setOnClickListener(registerView -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            navigateToRegisterActivity();
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
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
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


    public void checkSignInDetails(String userEmail, String userPassword) {
        if (TextUtils.isEmpty(userEmail)) {
            editEmailLayout.setError("Email cannot be empty");
            editEmailLayout.requestFocus();
        } else if (TextUtils.isEmpty(userPassword)) {
            editPasswordLayout.setError("Password cannot be empty");
            editPasswordLayout.requestFocus();
        } else if (!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPassword)) {
            firebaseSignInWithEmailPassword(userEmail, userPassword);
        }
    }


    public void firebaseSignInWithEmailPassword(String userEmail, String userPassword) {
        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(emailPasswordSignInTask -> {
                    if (emailPasswordSignInTask.isSuccessful()) {
                        Log.d("FIREBASE_SIGN_IN_SUCCESSFUL", "User successfully signed in");
                        Toast.makeText(this, "Sign in successful!", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    } else {
                        Log.e("FIREBASE_USER_EXCEPTION", "Sign in unsuccessful");
                        Toast.makeText(this, "Sign in unsuccessful, please try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    ActivityResultLauncher<IntentSenderRequest> oneTapCredentialResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                try {
                    SignInCredential googleCredential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                    String googleIdToken = googleCredential.getGoogleIdToken();
                    if (googleIdToken != null) {
                        // Retrieve the credentials of the Google Account using the ID token given either from the Google sign in button or the One Tap UI
                        AuthCredential googleAuthCredential = GoogleAuthProvider.getCredential(googleIdToken, null);
                        signInWithGoogleCredential(googleAuthCredential); // Use the credential to sign in with Google
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

        }
    });


    private void signInWithGoogleCredential(AuthCredential googleAuthCredential) {
        firebaseAuthViewModel.signInWithGoogle(googleAuthCredential);
        firebaseAuthViewModel.authenticatedGoogleUserLiveData.observe(this, new Observer<TravelHopperUser>() {
            @Override
            public void onChanged(TravelHopperUser authenticatedUser) {
                if (authenticatedUser.isNewUser) {
                    addNewGoogleUser(authenticatedUser);
                } else {
                    navigateToMainActivity();
                }
            }
        });
    }


    private void addNewGoogleUser(TravelHopperUser authenticatedUser) {
        firebaseAuthViewModel.addNewGoogleUser(authenticatedUser);
        firebaseAuthViewModel.newGoogleUserLiveData.observe(this, newGoogleUser -> {
            if (newGoogleUser.isUserCreated) {
                toastUserCreatedMessage(newGoogleUser.getUserDisplayName());
            }
            navigateToMainActivity();
        });
    }


    private void toastUserCreatedMessage(String userDisplayName) {
        Toast.makeText(getApplicationContext(), "User - " + userDisplayName + " has been created", Toast.LENGTH_SHORT).show();
    }

    private void navigateToMainActivity() {
        // Navigate to the MainActivity
        Intent navigateToMainActivity = new Intent(this, MainActivity.class);
        startActivity(navigateToMainActivity);
        // Close this current activity
        finish();
    }


    private void navigateToRegisterActivity() {
        // Navigate to the RegisterActivity
        Intent navigateToRegisterActivity = new Intent(this, RegisterActivity.class);
        startActivity(navigateToRegisterActivity);
        // Close this current activity
        finish();
    }


    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}