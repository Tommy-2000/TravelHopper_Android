package uk.ac.tees.b1662096.travelhopper_travelapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.ActivityMainBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TravelHopperDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivitiesIfAvailable(getApplication());
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View rootView = activityMainBinding.getRoot();
        setContentView(rootView);

        // Set up Navigation between the four different Fragments
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) supportFragmentManager.findFragmentById(R.id.navHostFragment);

        // Initialise NavController to setup the NavController (which will be the BottomNavigationView)
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        BottomAppBar bottomAppBar = activityMainBinding.bottomAppBar;
        NavigationUI.setupWithNavController(bottomAppBar, navController);

        // Initialise navigation from MainActivity to MyCameraActivity
        FloatingActionButton myCameraActivityButton = activityMainBinding.myCameraButton;
        Intent navigateToMyCamera = new Intent(this, MyCameraActivity.class);
        myCameraActivityButton.setOnClickListener(view -> startActivity(navigateToMyCamera));

        // Initialise the Room database for MyFavouritesFragment
        TravelHopperDatabase travelHopperDatabase = Room.databaseBuilder(getApplicationContext(), TravelHopperDatabase.class, "travelhopper_database").build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
    }
}
