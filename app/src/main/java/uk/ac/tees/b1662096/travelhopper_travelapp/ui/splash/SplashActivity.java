package uk.ac.tees.b1662096.travelhopper_travelapp.ui.splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import uk.ac.tees.b1662096.travelhopper_travelapp.MainActivity;
import uk.ac.tees.b1662096.travelhopper_travelapp.ViewModelInjector;
import uk.ac.tees.b1662096.travelhopper_travelapp.data.model.TravelHopperUser;
import uk.ac.tees.b1662096.travelhopper_travelapp.ui.firebaseAuth.SignInActivity;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private SplashViewModel splashViewModel;

    private SplashViewModelFactory splashViewModelFactory;

    private String TRAVELHOPPER_USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        // IMPORTANT! - Make sure to first test the application with Firebase emulators before using in production!
        firebaseAuth.useEmulator("127.0.0.1", 9099);

        splashViewModelFactory = ViewModelInjector.getSplashViewModelFactory();
        splashViewModel = new ViewModelProvider(this, splashViewModelFactory).get(SplashViewModel.class);
        splashViewModel.checkIfUserIsAuthenticated();
        splashViewModel.isUserAuthenticatedData.observe(this, travelHopperUser -> {
            if (!travelHopperUser.isUserAuthenticated && firebaseAuth.getCurrentUser() == null) {
                navigateToSignInActivity();
            } else {
                getUserFromFirestore(travelHopperUser.userId);
            }
        });

    }

    private void getUserFromFirestore(String userID) {
        splashViewModel.setUserIDData(userID);
        splashViewModel.userLiveData.observe(this, travelHopperUser -> navigateToMainActivity(travelHopperUser));
    }

    private void navigateToSignInActivity() {
        // If a user is not authenticated, navigate to the SignInActivity
        Intent navigateToMainActivity = new Intent(SplashActivity.this, SignInActivity.class);
        startActivity(navigateToMainActivity);
        // Close this current activity
        finish();
    }

    private void navigateToMainActivity(TravelHopperUser travelHopperUser) {
        // Once a new user account is created, navigate to the MainActivity
        Intent navigateToMainActivity = new Intent(SplashActivity.this, MainActivity.class);
        navigateToMainActivity.putExtra(TRAVELHOPPER_USER, travelHopperUser);
        startActivity(navigateToMainActivity);
        // Close this current activity
        finish();
    }

}