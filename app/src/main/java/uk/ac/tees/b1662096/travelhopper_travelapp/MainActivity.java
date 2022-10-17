package uk.ac.tees.b1662096.travelhopper_travelapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

//import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding mainActivityBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View bindingView = mainActivityBinding.getRoot();
        setContentView(bindingView);

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) supportFragmentManager.findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

    }

    HomeFragment homeFragment = new HomeFragment();
    PlannerFragment plannerFragment = new PlannerFragment();
    MapsFragment mapsFragment = new MapsFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.home_fragment:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, homeFragment).commit();
                return true;
            case R.id.planner_fragment:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, plannerFragment).commit();
                return true;
            case R.id.camera_activity:
                Navigation.findNavController(mainActivityBinding.getRoot()).navigate(R.id.cameraActivity);
                return true;
            case R.id.maps_fragment:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, mapsFragment).commit();
                return true;
            case R.id.settings_fragment:
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, settingsFragment).commit();
                return true;
        }
        return false;
    }


    @Override
    public void onClick(View view) {

    }
}
