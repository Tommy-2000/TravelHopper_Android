package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.ActivityMainBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentMyTripsBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripViewModel;


public class MyTripsFragment extends Fragment {

    private FragmentMyTripsBinding fragmentMyTripsBinding;

    private MyTripsRecyclerViewAdapter myTripsRecyclerViewAdapter;

    private TripViewModel tripViewModel;

    private static final String INSTANCE_ARG = "grid-column-count";
    private int gridColumnCount = 2;

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
//        if (getArguments() != null) {
//            gridColumnCount = getArguments().getInt(INSTANCE_ARG);
//        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater fragmentInflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the root view from the layout of this fragment and then return it
        fragmentMyTripsBinding = FragmentMyTripsBinding.inflate(fragmentInflater, container, false);
        View rootFragmentView = fragmentMyTripsBinding.getRoot();
        FloatingActionButton addNewTripButton = fragmentMyTripsBinding.addNewTripButton;

        // Once the button is clicked, the CreateNewTripFragment will launch in the current fragment's parent layout
        addNewTripButton.setOnClickListener(navigateForward -> {
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(fragmentMyTripsBinding.myTripsFragmentLayout.getId(), CreateNewTripFragment.getNewInstance());
            fragmentTransaction.setReorderingAllowed(true);
            fragmentTransaction.commit();
        });

        // Set up the LayoutManager for the RecyclerView
        RecyclerView myTripsRecyclerView = fragmentMyTripsBinding.myTripsRecyclerView;
        if (gridColumnCount <= 1) {
            myTripsRecyclerView.setLayoutManager(new LinearLayoutManager(myTripsRecyclerView.getContext()));
        } else {
            myTripsRecyclerView.setLayoutManager(new GridLayoutManager(myTripsRecyclerView.getContext(), gridColumnCount));
        }
        myTripsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // Set up the adapter for the RecyclerView
        MyTripsRecyclerViewAdapter myTripsRecyclerViewAdapter = new MyTripsRecyclerViewAdapter(new MyTripsRecyclerViewAdapter.TripEntityDiff());
//            TripViewModel tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);
//            tripViewModel.getAllTrips().observe(getViewLifecycleOwner(), tripEntityList -> myTripsRecyclerViewAdapter.submitList(tripEntityList));
        myTripsRecyclerView.setAdapter(myTripsRecyclerViewAdapter);

        return rootFragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View fragmentView, @Nullable Bundle savedInstanceState) {
        fragmentMyTripsBinding.myTripsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentMyTripsBinding = null;
    }

}