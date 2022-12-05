package uk.ac.tees.b1662096.travelhopper_travelapp.ui.firebaseAuth;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import uk.ac.tees.b1662096.travelhopper_travelapp.MainActivity;
import uk.ac.tees.b1662096.travelhopper_travelapp.R;
import uk.ac.tees.b1662096.travelhopper_travelapp.data.model.TravelHopperUser;
import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.ActivityLoginBinding;

public class SignInActivity extends AppCompatActivity {

    private ActivityLoginBinding activityLoginBinding;

    private View rootView;

    private FirebaseAuthViewModel firebaseAuthViewModel;

    private GoogleSignInClient googleSignInClient;

//    private SignInClient oneTapClient;
//    private BeginSignInRequest signInRequest;

    private String TRAVELHOPPER_USER;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        rootView = activityLoginBinding.getRoot();
        setContentView(rootView);

        firebaseAuthViewModel = new ViewModelProvider(this).get(FirebaseAuthViewModel.class);

        // Get the Google Sign In Client to be used with the Google Sign In button with predefined sign in options
        getGoogleSignInClient();

//        // Set up the One Tap UI for Google Accounts
//        oneTapClient = Identity.getSignInClient(this);
//        // Set up the request to sign with using One Tap UI
//        signInRequest = BeginSignInRequest
//                .builder()
//                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
//                        .setSupported(true)
//                        .build())
//                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                        .setSupported(true)
//                        .setServerClientId(getString(R.string.oauth_client_ID))
//                        .setFilterByAuthorizedAccounts(true)
//                        .build())
//                .setAutoSelectEnabled(true)
//                .build();

//        // Begin sign in process using One Tap UI
//        oneTapClient.beginSignIn(signInRequest).addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
//            @Override
//            public void onSuccess(BeginSignInResult beginSignInResult) {
//                try {
//                    IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(beginSignInResult.getPendingIntent().getIntentSender()).build();
//                    oneTapIntentResultLauncher.launch(intentSenderRequest);
//                }
//            }
//        })

//        TextInputEditText usernameEditText = activityLoginBinding.usernameEditText;
//        TextInputEditText passwordEditText = activityLoginBinding.passwordEditText;
        CircularProgressIndicator loadingProgressBar = (CircularProgressIndicator) activityLoginBinding.circularProgressBar;

        SignInButton googleSignInButton = activityLoginBinding.signInWithGoogleButton;
        googleSignInButton.setOnClickListener(view -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            signInWithGoogle();
        });
    }

    private void getGoogleSignInClient() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.oauth_client_ID)) // Get the OAuth Client ID from strings
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions); // Get the client based with these options
    }

    private void signInWithGoogle() {
        Intent googleSignInIntent = googleSignInClient.getSignInIntent();
        googleSignInIntentResolver.launch(googleSignInIntent); // Resolve the intent to sign in with Google
    }

    ActivityResultLauncher<Intent> googleSignInIntentResolver = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), intentResult -> {
        if (intentResult.getResultCode() == SignInActivity.RESULT_OK) {
            // If the result of the activity of ok, get the account from the sent intent
            Task<GoogleSignInAccount> googleSignInTask = GoogleSignIn.getSignedInAccountFromIntent(intentResult.getData());
            try {
                GoogleSignInAccount googleSignInAccount = googleSignInTask.getResult(ApiException.class);
                if (googleSignInAccount != null) {
                    // Get the credentials of the user's Google Account
                    getGoogleAuthCredential(googleSignInAccount);
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
    });

//    ActivityResultLauncher<IntentSenderRequest> oneTapIntentResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
//        @Override
//        public void onActivityResult(ActivityResult result) {
//            if (result.getResultCode() != Activity.RESULT_OK) {
//                if (result.getData().getAction().equals(ActivityResultContracts.StartIntentSenderForResult.ACTION_INTENT_SENDER_REQUEST)) {
//                    // Get the Exception from the intent sender
//                    Exception resultException = (Exception) result.getData().getSerializableExtra(ActivityResultContracts.StartIntentSenderForResult.EXTRA_SEND_INTENT_EXCEPTION);
//                    Log.e("", "Unable to start One Tap UI: " + resultException.getLocalizedMessage());
//                }
//                return ;
//            }
//            // Get the client for One Tap UI
//            oneTapClient = Identity.getSignInClient(getApplicationContext());
//
//        }
//    });

    private void getGoogleAuthCredential(GoogleSignInAccount googleSignInAccount) {
        String googleTokenID = googleSignInAccount.getIdToken(); // Retrieve the ID token from the account
        AuthCredential googleAuthCredential = GoogleAuthProvider.getCredential(googleTokenID, null); // Retrieve the credentials of the Google Account using the ID token
        signInWithGoogleCredential(googleAuthCredential); // Use the credential to sign in with Google
    }

    private void signInWithGoogleCredential(AuthCredential googleAuthCredential) {
        firebaseAuthViewModel.signInWithGoogle(googleAuthCredential);
        firebaseAuthViewModel.authenticatedUserData.observe(this, travelHopperUser -> {
            // Check if the user is new then create the new user in the Firestore
            if (travelHopperUser.isNewUser) {
                createNewUser(travelHopperUser);
            } else {
                navigateToMainActivity(travelHopperUser);
            }
        });
    }

    private void createNewUser(TravelHopperUser travelHopperUser) {
        firebaseAuthViewModel.createNewUser(travelHopperUser);
        firebaseAuthViewModel.createdUserData.observe(this, new Observer<>() {
            @Override
            public void onChanged(TravelHopperUser travelHopperUser) {
                if (travelHopperUser.isUserCreated) {
                    snackBarMessage(travelHopperUser.getUserName());
                }
                navigateToMainActivity(travelHopperUser);
            }
        });
    }

//    private void isUserSignedIn(TravelHopperUser travelHopperUser) {
//
//    }

    private void navigateToMainActivity(TravelHopperUser travelHopperUser) {
        // Once a new user account is created, navigate to the MainActivity
        Intent navigateToMainActivity = new Intent(SignInActivity.this, MainActivity.class);
        navigateToMainActivity.putExtra(TRAVELHOPPER_USER, travelHopperUser);
        startActivity(navigateToMainActivity);
        // Close this current activity
        finish();
    }

    private void snackBarMessage(String userName) {
        Snackbar.make(rootView, "Welcome " + userName + "!", Snackbar.LENGTH_SHORT).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Snackbar.make(rootView, errorString, Snackbar.LENGTH_SHORT).show();
    }
}