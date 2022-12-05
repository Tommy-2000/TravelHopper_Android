package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentMyHomeBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripEntity;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyHomeFragment#getNewInstance} factory method to
 * create an instance of this fragment.
 */
public class MyHomeFragment extends Fragment {

    private FragmentMyHomeBinding fragmentMyHomeBinding;

    private TripViewModel tripViewModel;


    public MyHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment
     *
     * @return A new instance of fragment HomeFragment.
     */
    @SuppressWarnings("unused")
    public static MyHomeFragment getNewInstance() {
        MyHomeFragment myHomeFragment = new MyHomeFragment();
        Bundle fragmentBundle = new Bundle();
        myHomeFragment.setArguments(fragmentBundle);
        return myHomeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Manage navigation callback when back button is pressed
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Ensure that the parent fragment (MyHomeFragment) loads into the NavHostFragment while using the bottom navigation menu
                NavHostFragment.findNavController(MyHomeFragment.this).navigateUp();
            }
        };
        requireParentFragment().requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater fragmentInflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the root view from the layout of this fragment and then return it
        fragmentMyHomeBinding = FragmentMyHomeBinding.inflate(fragmentInflater, container, false);
        View rootFragmentView = fragmentMyHomeBinding.getRoot();

        RecyclerView myHomeCardCarousel = fragmentMyHomeBinding.myHomeCardCarousel;
        myHomeCardCarousel.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
//        MyHomeRecyclerViewAdapter myHomeRecyclerViewAdapter = new MyHomeRecyclerViewAdapter();
//        myHomeCardCarousel.setAdapter(myHomeRecyclerViewAdapter);
        // Set a decorator for the RecyclerView
        int tripCardWidthPixels = (int) (requireActivity().getResources().getDisplayMetrics().widthPixels * 0.80f);
        float tripCardHintPercent = 0.01f;
        myHomeCardCarousel.addItemDecoration(new MyHomeRecyclerViewDecorator(requireContext(), tripCardWidthPixels, 0.01f));

        return rootFragmentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentMyHomeBinding = null;
    }

}