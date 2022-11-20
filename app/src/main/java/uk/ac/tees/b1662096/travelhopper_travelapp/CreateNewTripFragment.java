package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentCreateNewTripBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateNewTripFragment#getNewInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateNewTripFragment extends Fragment {

    public FragmentCreateNewTripBinding fragmentCreateNewTripBinding;

//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "createNewTripFragment";
//
//    private String createNewTripFragment;

    public CreateNewTripFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateNewTripFragment.
     */
    public static CreateNewTripFragment getNewInstance() {
        CreateNewTripFragment createNewTripFragment = new CreateNewTripFragment();
        Bundle fragmentBundle = new Bundle();
        createNewTripFragment.setArguments(fragmentBundle);
        return createNewTripFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            createNewTripFragment = getArguments().getString(ARG_PARAM1);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater fragmentInflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentCreateNewTripBinding = FragmentCreateNewTripBinding.inflate(fragmentInflater, container, false);
        View rootFragmentView = fragmentCreateNewTripBinding.getRoot();

        // Initialise the DatePicker to allow for the user to add the dates of the trip
        MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().setTitleText("Select date").setSelection(new Pair<>(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds())).build();

        return rootFragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View rootFragmentView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootFragmentView, savedInstanceState);

        MaterialToolbar fragmentToolbar = fragmentCreateNewTripBinding.createNewTripFragmentToolbar;
        fragmentToolbar.setNavigationIcon(R.drawable.ic_arrow_back_icon);
        fragmentToolbar.setNavigationOnClickListener(navigateFragmentView -> {
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(fragmentCreateNewTripBinding.createNewTripFragmentLayout.getId(), MyTripsFragment.getNewInstance());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setReorderingAllowed(true);
            fragmentTransaction.commit();
            // Set the visibility of current fragment to gone when navigating back to previous fragment
            fragmentToolbar.setVisibility(View.GONE);
            fragmentCreateNewTripBinding.createNewTripFragmentLayout.setVisibility(View.GONE);
        });


    }
}