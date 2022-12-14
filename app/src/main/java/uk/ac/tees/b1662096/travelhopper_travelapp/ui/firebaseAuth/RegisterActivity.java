package uk.ac.tees.b1662096.travelhopper_travelapp.ui.firebaseAuth;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding activityRegisterBinding;

    private View rootView;

    private FirebaseAuthViewModel firebaseAuthViewModel;

    private FirebaseAuth firebaseAuth;


    private TextInputLayout editEmailLayout, editPasswordLayout;

    private String travelHopperEmailString, travelHopperPasswordString;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegisterBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        rootView = activityRegisterBinding.getRoot();
        setContentView(rootView);

        // Get an instance of FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
//        // IMPORTANT! - Make sure to first test the application with Firebase emulators before using in production!
//        firebaseAuth.useEmulator("127.0.0.1", 9099);


        editEmailLayout = activityRegisterBinding.usernameInputLayout;
        editPasswordLayout = activityRegisterBinding.passwordInputLayout;
        EditText editEmailText = Objects.requireNonNull(editEmailLayout).getEditText();
        EditText editPasswordText = Objects.requireNonNull(editPasswordLayout).getEditText();
        travelHopperEmailString = Objects.requireNonNull(editEmailText).toString();
        travelHopperPasswordString = Objects.requireNonNull(editPasswordText).toString();

        CircularProgressIndicator loadingProgressBar = activityRegisterBinding.circularProgressBar;

        MaterialButton registerButton = activityRegisterBinding.registerButton;
        registerButton.setOnClickListener(view -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            checkRegisterDetails(travelHopperEmailString, travelHopperPasswordString);
        });

    }


    public void checkRegisterDetails(String userEmail, String userPassword) {
        if (TextUtils.isEmpty(userEmail)) {
            editEmailLayout.setError("Email cannot be empty");
            editEmailLayout.requestFocus();
        } else if (TextUtils.isEmpty(userPassword)) {
            editPasswordLayout.setError("Password cannot be empty");
            editPasswordLayout.requestFocus();
        } else if (!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPassword)) {
            firebaseCreateEmailPasswordAccount(userEmail, userPassword);
        }
    }



    public void firebaseCreateEmailPasswordAccount(String userEmail, String userPassword) {
        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(createEmailPasswordAccountResult -> {
                    if (createEmailPasswordAccountResult.isSuccessful()) {
                        Log.d("FIREBASE_REGISTRATION_SUCCESSFUL", "User successfully created");
                        Toast.makeText(this, "New account created!", Toast.LENGTH_SHORT).show();
                        navigateToSignInActivity();
                    } else {
                        Log.e("FIREBASE_REGISTRATION_EXCEPTION", "Sign in unsuccessful");
                        Toast.makeText(this, "Sign in unsuccessful, please try again", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void navigateToSignInActivity() {
        // Navigate to the SignInActivity
        Intent navigateToSignInActivity = new Intent(this, SignInActivity.class);
        startActivity(navigateToSignInActivity);
        // Close this current activity
        finish();
    }

}