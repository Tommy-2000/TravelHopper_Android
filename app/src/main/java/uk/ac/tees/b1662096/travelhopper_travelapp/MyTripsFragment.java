package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentMyGalleryBinding;


public class MyTripsFragment extends Fragment {

    private Context myTripsFragmentContext;

    private FragmentMyGalleryBinding fragmentMyGalleryBinding;

    private static final String ARG_COLUMN_COUNT = "grid-column-count";
    private int gridColumnCount = 2;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MyTripsFragment() {
    }

    @SuppressWarnings("unused")
    public static MyTripsFragment newInstance(int columnCount) {
        MyTripsFragment fragment = new MyTripsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            gridColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentMyGalleryBinding = FragmentMyGalleryBinding.inflate(inflater, container, false);
        View fragmentView = fragmentMyGalleryBinding.getRoot();

        myTripsFragmentContext = fragmentView.getContext();

        // Set the adapter
        if (fragmentView instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) fragmentView;
            if (gridColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(myTripsFragmentContext));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(myTripsFragmentContext, gridColumnCount));
            }
//            recyclerView.setAdapter(new MyTripsRecyclerViewAdapter(PlaceholderContent.ITEMS));
        }
        return fragmentView;
    }
}