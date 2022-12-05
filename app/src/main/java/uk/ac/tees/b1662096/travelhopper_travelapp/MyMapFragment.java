package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentMyMapBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyMapFragment#getNewInstance} factory method to
 * create an instance of this fragment.
 */
public class MyMapFragment extends Fragment implements OnMapsSdkInitializedCallback {

    private FragmentMyMapBinding fragmentMyMapBinding;

    private boolean permissionDenied = false;

    private String[] MY_MAP_FRAGMENT_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private int MY_MAP_FRAGMENT_PERMISSION_CODE = 15;

    private GoogleMap gMapSDK;

    public MyMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment MapFragment.
     */
    @SuppressWarnings("unused")
    public static MyMapFragment getNewInstance() {
        MyMapFragment myMapFragment = new MyMapFragment();
        Bundle fragmentBundle = new Bundle();
        myMapFragment.setArguments(fragmentBundle);
        return myMapFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Manage navigation callback when back button is pressed
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Ensure that the parent fragment (MyMapFragment) loads into the NavHostFragment while using the bottom navigation menu
                NavHostFragment.findNavController(MyMapFragment.this).navigateUp();
            }
        };
        requireParentFragment().requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater fragmentInflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the root view from the layout of this fragment and then return it
        fragmentMyMapBinding = FragmentMyMapBinding.inflate(fragmentInflater, container, false);
        View rootFragmentView = fragmentMyMapBinding.getRoot();

        // Initialise the Google Maps renderer object
        MapsInitializer.initialize(requireActivity().getApplicationContext(), MapsInitializer.Renderer.LATEST, this);

        // Initialise the GoogleMapOptions object
        GoogleMapOptions gMapOptions = new GoogleMapOptions();

        // Initialise the GoogleMapsFragment as a child fragment on top of the current (parent) fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentByTag("googleMapsFragment");

        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    // Initialise the Google Maps SDK
                    gMapSDK = googleMap;

                    // Initialise the marker position in Google Maps
                    LatLng kyoto = new LatLng(35.00116, 135.7681);
                    gMapSDK.addMarker(new MarkerOptions().position(kyoto).title("Marker in Kyoto, Japan"));
                    gMapSDK.moveCamera(CameraUpdateFactory.newLatLng(kyoto));

                    // Enable the user's location once permissions are checked
                    enableMapLocation();

                    gMapSDK.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
                        @Override
                        public void onMyLocationClick(@NonNull Location currentLocation) {
                            Snackbar.make(rootFragmentView, "Current location: \n" + currentLocation, Snackbar.LENGTH_SHORT).show();
                        }
                    });

                    // Initialise the current location button in Google Maps
                    gMapSDK.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                        @Override
                        public boolean onMyLocationButtonClick() {
                            Snackbar.make(rootFragmentView, "My Location button is clicked", Snackbar.LENGTH_SHORT).show();
                            return false;
                        }
                    });

                    // Set up options for Google Maps
                    gMapOptions.compassEnabled(true);
                    gMapOptions.scrollGesturesEnabled(true);
                    gMapOptions.rotateGesturesEnabled(true);
                    gMapOptions.zoomGesturesEnabled(true);
                    gMapOptions.scrollGesturesEnabledDuringRotateOrZoom(true);
                }
            });
        }

        return rootFragmentView;
    }

    private boolean accessCoarseLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(requireContext(), MY_MAP_FRAGMENT_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean accessFineLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(requireContext(), MY_MAP_FRAGMENT_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(requireActivity(), MY_MAP_FRAGMENT_PERMISSIONS, MY_MAP_FRAGMENT_PERMISSION_CODE);
    }


    @SuppressLint("MissingPermission")
    private void enableMapLocation() {
        if (accessCoarseLocationPermissionGranted() && accessFineLocationPermissionGranted()) {
            gMapSDK.setMyLocationEnabled(true);
            return;
        } else {
            requestLocationPermissions();
        }
    }

    @Override
    public void onMapsSdkInitialized(@NonNull MapsInitializer.Renderer renderer) {
        switch (renderer) {
            case LATEST:
                Log.d("MAPS_RENDERER", "The latest version of Google Maps is rendering");
                break;
            case LEGACY:
                Log.d("MAPS_RENDERER", "The legacy version of Google Maps is rendering");
                break;
        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if (permissionDenied) {
//
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentMyMapBinding = null;
    }
}