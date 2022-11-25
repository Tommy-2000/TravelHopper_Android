package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentMyHomeBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyHomeFragment#getNewInstance} factory method to
 * create an instance of this fragment.
 */
public class MyHomeFragment extends Fragment {

    private FragmentMyHomeBinding fragmentMyHomeBinding;


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
//        if (getArguments() != null) {

//        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater fragmentInflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the root view from the layout of this fragment and then return it
        fragmentMyHomeBinding = FragmentMyHomeBinding.inflate(fragmentInflater, container, false);
        View rootFragmentView = fragmentMyHomeBinding.getRoot();

        return rootFragmentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentMyHomeBinding = null;
    }

}