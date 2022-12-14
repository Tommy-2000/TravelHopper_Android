package uk.ac.tees.b1662096.travelhopper_travelapp;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;

import java.util.Objects;

import uk.ac.tees.b1662096.travelhopper_travelapp.data.model.TravelHopperUser;
import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.ActivityMainBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.ui.firebaseAuth.SignInActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;

    private View rootView;

    private FirebaseAuth firebaseAuth;

    private SignInClient oneTapClient;

    private GoogleSignInClient googleSignInClient;

    private String TRAVELHOPPER_USER;

    private String FIREBASE_USER;

    private FloatingActionButton googleAccountProfileButton;

    // Initialise the parent fragments to be loaded into the MainActivity
    final Fragment myHomeFragment = new MyHomeFragment();
    final Fragment myTripsFragment = new MyTripsFragment();
    final Fragment myGalleryFragment = new MyGalleryFragment();
    final Fragment myMapFragment = new MyMapFragment();
    final FragmentManager parentFragmentManager = getSupportFragmentManager();
    // MyHomeFragment is the first fragment that appears in MainActivity
    Fragment activeFragment = myHomeFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivitiesIfAvailable(getApplication());
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        rootView = activityMainBinding.getRoot();
        setContentView(rootView);


        firebaseAuth = FirebaseAuth.getInstance();

        // Get the signed-in Firebase user from the intent
        FirebaseUser firebaseUser = getFirebaseUserFromLoginIntent();
        // Check if the user has signed in, if so show a snackbar message
        if (firebaseUser != null) {
            showSnackBarFirebaseSignInMessage(firebaseUser);
        }

        // Get the Google Sign In Client with predefined sign in options
        getGoogleSignInClient();

        // Get the Profile image associated with the Google Account and set it to the button
        getGoogleAccountProfileInfo();


        // Set up Navigation between the four different Fragments and the BottomNavigationView
        NavigationBarView bottomNavBar = activityMainBinding.bottomNavBar;
        bottomNavBar.setOnItemSelectedListener(parentFragmentNavigation);
        parentFragmentManager.beginTransaction().add(activityMainBinding.parentFragmentNavigationContainer.getId(), myHomeFragment, "My Home").hide(myHomeFragment).commit();
        parentFragmentManager.beginTransaction().add(activityMainBinding.parentFragmentNavigationContainer.getId(), myTripsFragment, "My Trips").hide(myTripsFragment).commit();
        parentFragmentManager.beginTransaction().add(activityMainBinding.parentFragmentNavigationContainer.getId(), myGalleryFragment, "My Gallery").hide(myGalleryFragment).commit();
        parentFragmentManager.beginTransaction().add(activityMainBinding.parentFragmentNavigationContainer.getId(), myMapFragment, "My Map").hide(myMapFragment).commit();


        // Initialise navigation from MainActivity to MyCameraActivity
        FloatingActionButton myCameraActivityButton = activityMainBinding.myCameraButton;
        Intent navigateToMyCamera = new Intent(this, MyCameraActivity.class);
        myCameraActivityButton.setOnClickListener(view -> startActivity(navigateToMyCamera));

        // Initialise the Google Account Profile Icon Button
        googleAccountProfileButton = activityMainBinding.googleAccountProfileButton;
        googleAccountProfileButton.setOnClickListener(view -> showAccountMenu(view, R.menu.profile_menu));
    }


    private NavigationBarView.OnItemSelectedListener parentFragmentNavigation = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem parentFragmentItem) {
            if (parentFragmentItem.getItemId() == R.id.myHomeFragment) {
                parentFragmentManager.beginTransaction().hide(activeFragment).show(myHomeFragment).commit();
                activeFragment = myHomeFragment;
                return true;
            } else if (parentFragmentItem.getItemId() == R.id.myTripsFragment) {
                parentFragmentManager.beginTransaction().hide(activeFragment).show(myTripsFragment).commit();
                activeFragment = myTripsFragment;
                return true;
            } else if (parentFragmentItem.getItemId() == R.id.myGalleryFragment) {
                parentFragmentManager.beginTransaction().hide(activeFragment).show(myGalleryFragment).commit();
                activeFragment = myGalleryFragment;
                return true;
            } else if (parentFragmentItem.getItemId() == R.id.myMapFragment) {
                parentFragmentManager.beginTransaction().hide(activeFragment).show(myMapFragment).commit();
                activeFragment = myMapFragment;
                return true;
            }
            return false;
        }
    };

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
            } else if (item.getItemId() == R.id.signOut) {
                signOutGoogleUser();
                return true;
            }
            return false;
        });
        accountMenu.show();
    }

    private void navigateToGoogleAccount() {
        Intent navigateToGoogleAccount = new Intent(this, GoogleAccountActivity.class);
        startActivity(navigateToGoogleAccount);
    }


    private void navigateToSignInActivity() {
        Intent navigateToSignIn = new Intent(this, SignInActivity.class);
        startActivity(navigateToSignIn);
        // Make sure that the account button from the Main Activity is not visible when navigating
        googleAccountProfileButton.setVisibility(View.GONE);
    }

//    private void showSnackBarSignInMessage(TravelHopperUser travelHopperUser) {
//        String signInMessage = "You are signed in as: " + travelHopperUser.getUserDisplayName();
//        Snackbar.make(rootView, signInMessage, Snackbar.LENGTH_SHORT).show();
//    }

    private void showSnackBarFirebaseSignInMessage(FirebaseUser firebaseUser) {
        String signInFirebaseMessage = "You are signed in as: " + firebaseUser.getDisplayName();
        Snackbar.make(rootView, signInFirebaseMessage, Snackbar.LENGTH_SHORT).show();
    }

    private void getGoogleAccountProfileInfo() {
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (googleAccount != null) {
//            String accountProfileID = googleAccount.getId();
            Uri accountProfileImage = googleAccount.getPhotoUrl();
            if (accountProfileImage != null) {
                Drawable googleAccountDrawable = Drawable.createFromPath(accountProfileImage.getPath());
                googleAccountProfileButton.setBackgroundDrawable(googleAccountDrawable);
            } else {
                Drawable defaultAccountDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_account_circle_icon, getTheme());
                googleAccountProfileButton.setBackgroundDrawable(defaultAccountDrawable);
            }
        }
    }

    private FirebaseUser getFirebaseUserFromLoginIntent() {
        return (FirebaseUser) getIntent().getSerializableExtra(FIREBASE_USER);
    }

    private GoogleSignInClient getGoogleSignInClient() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();

        return googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    private void signOutGoogleUser() {
        // Sign out the user from the app itself using Firebase Auth and Google Sign In
        FirebaseAuth.getInstance().signOut();
        getGoogleSignInClient().signOut();

        navigateToSignInActivity();
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
