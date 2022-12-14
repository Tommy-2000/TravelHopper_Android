package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentTripDetailsBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripDetailViewModel;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripDetailViewModelFactory;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripEntity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TripDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TripDetailsFragment extends Fragment {

    private ViewDataBinding fragmentTripDetailsBinding;

    private View rootFragmentView;

    private static final String FRAGMENT_TAG = "TripDetailsFragment";
    private static final String ARG_TRIP_ITEM = "trip_id";

    private TripDetailViewModel tripDetailViewModel;

    private TripDetailViewModelFactory tripDetailViewModelFactory;


    public TripDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment TripDetailsFragment.
     */
    public static TripDetailsFragment newInstance(String tripID) {
        Bundle args = new Bundle();
        args.putString(ARG_TRIP_ITEM, tripID);
        TripDetailsFragment fragment = new TripDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater fragmentInflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the root view from the layout of this fragment and then return it
        fragmentTripDetailsBinding = DataBindingUtil.inflate(fragmentInflater, R.layout.fragment_trip_details, container, false);
        rootFragmentView = fragmentTripDetailsBinding.getRoot();


        String tripID = TripDetailsFragmentArgs.fromBundle(getArguments()).getTripID();
        initViewModel(tripID);

        return rootFragmentView;
    }

    private void initViewModel(String tripID) {
        try {
            TripDetailViewModelFactory viewModelFactory = ViewModelInjector.getTripDetailViewModelFactory(requireActivity(), tripID);
            tripDetailViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(TripDetailViewModel.class);
            tripDetailViewModel.tripEntity.observe(requireActivity(), new Observer<TripEntity>() {
                @Override
                public void onChanged(TripEntity tripEntity) {
                    if (tripEntity == null) {

                    }
                }
            });
        } catch (IllegalArgumentException e) {
            Log.e("VIEW_MODEL_ERROR", e.getLocalizedMessage());
        }
    }
}