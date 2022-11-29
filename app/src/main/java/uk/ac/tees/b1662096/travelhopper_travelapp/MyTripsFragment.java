package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.annotation.SuppressLint;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        return rootFragmentView;
    }


    @Override
    public void onViewCreated(@NonNull View fragmentView, @Nullable Bundle savedInstanceState) {

        MaterialButton filterTripButton = fragmentMyTripsBinding.filterTripButton;

        FloatingActionButton addNewTripButton = fragmentMyTripsBinding.addNewTripButton;

        SwipeRefreshLayout myTripsRefreshLayout = fragmentMyTripsBinding.myTripsRefreshLayout;

        // Once the filter button is clicked, show the menu that filters the items in the RecyclerView
        filterTripButton.setOnClickListener(filterRecyclerView -> showFilterMenu(filterRecyclerView, R.menu.filter_popup_menu));

        // Once the button is clicked, the CreateNewTripFragment will launch in the current fragment's parent layout
        addNewTripButton.setOnClickListener(navigateToFragment -> {
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
//            Fragment parentFragment = getChildFragmentManager().getPrimaryNavigationFragment();
            Fragment childFragment = CreateNewTripFragment.getNewInstance();
            // Replace the current parent fragment's layout for the child fragment (CreateNewTripFragment)
            fragmentTransaction.replace(fragmentMyTripsBinding.rootFragmentLayout.getId(), childFragment);
            fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            fragmentTransaction.setReorderingAllowed(true);
            fragmentTransaction.commit();

            addNewTripButton.setVisibility(View.GONE);

        });

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

    @SuppressLint("RestrictedApi")
    private void showFilterMenu(View filterRecyclerView, @MenuRes int filter_popup_menu) {
        PopupMenu filterPopup = new PopupMenu(requireActivity(), filterRecyclerView);
        filterPopup.getMenuInflater().inflate(filter_popup_menu, filterPopup.getMenu());
        if (filterPopup.getMenu() instanceof MenuBuilder) {
            MenuBuilder menuBuilder = (MenuBuilder) filterPopup.getMenu();
            menuBuilder.setOptionalIconsVisible(true);
            for (MenuItem menuItem : menuBuilder.getVisibleItems()) {
                float ICON_MARGIN = 16;
                int iconMarginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ICON_MARGIN, getResources().getDisplayMetrics());
                if (menuItem.getIcon() != null) {
                    new InsetDrawable(menuItem.getIcon(), iconMarginPx, 0, iconMarginPx, 0);
                } else {
                    new InsetDrawable(menuItem.getIcon(), iconMarginPx, 0, iconMarginPx, 0) {
                        @Override
                        public int getIntrinsicWidth() {
                            return getIntrinsicHeight() + iconMarginPx + iconMarginPx;
                        }
                    };
                }
            }
        }
        filterPopup.show();
        filterPopup.setOnMenuItemClickListener(item -> false);
        filterPopup.setOnDismissListener(menu -> {

        });
        filterPopup.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentMyTripsBinding = null;
    }

}