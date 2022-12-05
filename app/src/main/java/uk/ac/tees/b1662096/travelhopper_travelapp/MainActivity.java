package uk.ac.tees.b1662096.travelhopper_travelapp;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.InputStream;

import uk.ac.tees.b1662096.travelhopper_travelapp.data.model.TravelHopperUser;
import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;

    private View rootView;

    private AppBarConfiguration appBarConfiguration;

    private String TRAVELHOPPER_USER;

    private GoogleSignInClient googleSignInClient;

    private FloatingActionButton googleAccountProfileButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivitiesIfAvailable(getApplication());
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        rootView = activityMainBinding.getRoot();
        setContentView(rootView);

        // Get the signed-in user from the intent
        TravelHopperUser travelHopperUser = getUserFromLoginIntent();

        showSnackBarSignInMessage(travelHopperUser);

        // Get the Google Sign In Client with predefined sign in options
        getGoogleSignInClient();

        // Get the Profile image associated with the Google Account and set it to the button
        getGoogleAccountProfileInfo();

        // Set up Navigation between the four different Fragments in the NavHostFragment and the BottomNavigationView
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(activityMainBinding.navHostFragment.getId());
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNavBar = activityMainBinding.bottomNavBar;
        NavigationUI.setupWithNavController(bottomNavBar, navController);


        // Initialise navigation from MainActivity to MyCameraActivity
        FloatingActionButton myCameraActivityButton = activityMainBinding.myCameraButton;
        Intent navigateToMyCamera = new Intent(this, MyCameraActivity.class);
        myCameraActivityButton.setOnClickListener(view -> startActivity(navigateToMyCamera));

        // Initialise the Google Account Profile Icon Button
        googleAccountProfileButton = activityMainBinding.googleAccountProfileButton;
        googleAccountProfileButton.setOnClickListener(view -> showAccountMenu(view, R.menu.profile_menu));
    }

    @SuppressLint("RestrictedApi")
    private void showAccountMenu(View view, @MenuRes int menuRes) {
        PopupMenu accountMenu = new PopupMenu(this, view);
        accountMenu.getMenuInflater().inflate(menuRes, accountMenu.getMenu());

        if (accountMenu.getMenu() instanceof MenuBuilder) {
            MenuBuilder menuBuilder = (MenuBuilder) accountMenu.getMenu();
            menuBuilder.setOptionalIconsVisible(true);
            for (MenuItem menuItem : menuBuilder.getVisibleItems()) {
                float ICON_MARGIN = 16;
                int iconMarginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ICON_MARGIN, getResources().getDisplayMetrics());
                if (menuItem.getIcon() != null) {
                    new InsetDrawable(menuItem.getIcon(), iconMarginPx, 0, iconMarginPx, 0);
                } else {
                    new InsetDrawable(menuItem.getIcon(), iconMarginPx, 0, iconMarginPx, 0) {
                        @Override
                        public int getIntrinsicWidth() {
                            return getIntrinsicHeight() + iconMarginPx + iconMarginPx;
                        }
                    };
                }
            }
        }
        accountMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.googleAccountInfo) {
                navigateToGoogleAccount();
                return true;
            } else if (item.getItemId() == R.id.mySettings) {
                navigateToMySettings();
                return true;
            } else if (item.getItemId() == R.id.signOut) {
                signOutGoogleUser();
                return true;
            }
            return false;
        });
        accountMenu.show();
    }

    private void navigateToGoogleAccount() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        GoogleAccountFragment googleAccountFragment = GoogleAccountFragment.newInstance();
        fragmentTransaction.replace(activityMainBinding.navHostFragment.getId(), googleAccountFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setReorderingAllowed(false);
        fragmentTransaction.commit();
        // Make sure that the account button from the Main Activity is not visible when navigating
        googleAccountProfileButton.setVisibility(View.GONE);
    }

    private void navigateToMySettings() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        MySettingsFragment mySettingsFragment = new MySettingsFragment();
        fragmentTransaction.replace(activityMainBinding.navHostFragment.getId(), mySettingsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setReorderingAllowed(false);
        fragmentTransaction.commit();
        // Make sure that the account button from the Main Activity is not visible when navigating
        googleAccountProfileButton.setVisibility(View.GONE);
    }

    private void showSnackBarSignInMessage(TravelHopperUser travelHopperUser) {
        String signInMessage = "You are signed in as: " + travelHopperUser.getUserName();
        Snackbar.make(rootView, signInMessage, Snackbar.LENGTH_SHORT).show();
    }

    private void getGoogleAccountProfileInfo() {
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (googleAccount != null) {
            String accountProfileID = googleAccount.getId();
            Uri accountProfileImage = googleAccount.getPhotoUrl();
            setProfileImageToButton(accountProfileImage);
        }
    }

    private void setProfileImageToButton(Uri accountProfileImage) {
        Drawable accountProfileDrawable;
        try {
            InputStream uriInputStream = getContentResolver().openInputStream(accountProfileImage);
            accountProfileDrawable = Drawable.createFromStream(uriInputStream, accountProfileImage.toString());
        } catch (FileNotFoundException e) {
            accountProfileDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_account_circle_icon, getTheme());
            e.printStackTrace();
        }
        googleAccountProfileButton.setBackgroundDrawable(accountProfileDrawable);
    }

    private TravelHopperUser getUserFromLoginIntent() {
        return (TravelHopperUser) getIntent().getSerializableExtra(TRAVELHOPPER_USER);
    }

    private void getGoogleSignInClient() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    private void signOutGoogleUser() {
        // Sign out the user from the app itself using Firebase Auth
        FirebaseAuth.getInstance().signOut();
//        googleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                Log.d("GOOGLE_CLIENT_SIGN_OUT", "User has signed out of ");
//            }
//        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, activityMainBinding.navHostFragment.getId());
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}
