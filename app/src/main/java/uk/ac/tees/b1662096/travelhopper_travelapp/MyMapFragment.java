package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentMyMapBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyMapFragment#getNewInstance} factory method to
 * create an instance of this fragment.
 */
public class MyMapFragment extends Fragment {

    private FragmentMyMapBinding fragmentMyMapBinding;

    private GoogleMap gMap;

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
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater fragmentInflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the root view from the layout of this fragment and then return it
        fragmentMyMapBinding = FragmentMyMapBinding.inflate(fragmentInflater, container, false);
        View rootFragmentView = fragmentMyMapBinding.getRoot();

        // Initialise the GoogleMapsFragment as a child fragment on top of the current (parent) fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(fragmentMyMapBinding.googleMapsFragment.getRoot().getId());

        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(googleMap -> {
                gMap = googleMap;

                // Initialise the marker position in Google Maps
                LatLng london = new LatLng(51.58640723830534, -0.07004327470628068);
                gMap.addMarker(new MarkerOptions().position(london).title("Marker in London"));
                gMap.moveCamera(CameraUpdateFactory.newLatLng(london));
            });
        }

        return rootFragmentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentMyMapBinding = null;
    }
}