package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.ActivityMainBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentMyTripsBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripViewModel;


public class MyTripsFragment extends Fragment {

    private FragmentMyTripsBinding fragmentMyTripsBinding;

    private TripViewModel tripViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MyTripsFragment() {
    }

    @SuppressWarnings("unused")
    public static MyTripsFragment getNewInstance() {
        MyTripsFragment myTripsFragment = new MyTripsFragment();
        Bundle fragmentBundle = new Bundle();
        myTripsFragment.setArguments(fragmentBundle);
        return myTripsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Manage navigation callback when back button is pressed
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Ensure that the parent fragment (MyTripsFragment) loads into the NavHostFragment while using the bottom navigation menu
                NavHostFragment.findNavController(MyTripsFragment.this).navigateUp();
            }
        };
        requireParentFragment().requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater fragmentInflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the root view from the layout of this fragment and then return it
        fragmentMyTripsBinding = FragmentMyTripsBinding.inflate(fragmentInflater, container, false);
        View rootFragmentView = fragmentMyTripsBinding.getRoot();

        FloatingActionButton addNewTripButton = fragmentMyTripsBinding.addNewTripButton;

        // Once the button is clicked, the CreateNewTripFragment will launch in the current fragment's parent layout
        addNewTripButton.setOnClickListener(navigateToFragment -> {
            FragmentTransaction childFragmentTransaction = getChildFragmentManager().beginTransaction();
            // Replace the current parent fragment for the child fragment (CreateNewTripFragment)
            childFragmentTransaction.replace(fragmentMyTripsBinding.myTripsFragmentLayout.getId(), CreateNewTripFragment.getNewInstance());
            childFragmentTransaction.setCustomAnimations(R.anim.fragment_slide_in_anim, R.anim.fragment_slide_out_anim);
            childFragmentTransaction.setReorderingAllowed(false);
            childFragmentTransaction.commit();
            // Once the child fragment (CreateNewTripFragment) has loaded, hide the addNewTripButton
            addNewTripButton.setVisibility(View.GONE);
        });

        return rootFragmentView;
    }


    @Override
    public void onViewCreated(@NonNull View fragmentView, @Nullable Bundle savedInstanceState) {

        // Ensure that the parent fragment (MyTripsFragment) remains visible on navigation
        fragmentMyTripsBinding.myTripsFragmentLayout.setVisibility(View.VISIBLE);

        // Make sure that the RecyclerView is visible in this fragment's view
        fragmentMyTripsBinding.myTripsRecyclerView.setVisibility(View.VISIBLE);

        // Get all trips from the database by using the view model
        try {
            tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);
            tripViewModel.getAllTrips().observe(getViewLifecycleOwner(), tripEntityList -> {
                // Set up the LayoutManager for the RecyclerView
                RecyclerView myTripsRecyclerView = fragmentMyTripsBinding.myTripsRecyclerView;
                myTripsRecyclerView.setLayoutManager(new LinearLayoutManager(myTripsRecyclerView.getContext()));
                myTripsRecyclerView.setItemAnimator(new DefaultItemAnimator());
                // Set up the adapter for the RecyclerView
                MyTripsRecyclerViewAdapter myTripsRecyclerViewAdapter = new MyTripsRecyclerViewAdapter(new MyTripsRecyclerViewAdapter.TripEntityDiff());
                myTripsRecyclerView.setAdapter(myTripsRecyclerViewAdapter);
                // Update the list in the adapter based on the viewModel
                myTripsRecyclerViewAdapter.submitList(tripEntityList);
            });
        } catch (Exception e) {
            Log.e("View Model Observer Exception", e.getLocalizedMessage());
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentMyTripsBinding = null;
    }

}