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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentMyTripsBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.TripCardViewBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripEntity;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripListViewModel;


public class MyTripsFragment extends Fragment {

    private FragmentMyTripsBinding fragmentMyTripsBinding;

    private TripListViewModel tripListViewModel;

    private RecyclerView myTripsRecyclerView;

    private MyTripsRecyclerViewAdapter allTripsAdapter, favouriteTripsAdapter;

    private List<TripEntity> tripEntityList;

    private SwipeRefreshLayout tripRefreshLayout;

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

        // Get all trips from the database by using the view model
        try {
            tripListViewModel = new ViewModelProvider(requireActivity()).get(TripListViewModel.class);
            // Set up the RecyclerView once data is retrieved from the view model
            tripListViewModel.getAllTrips().observe(getViewLifecycleOwner(), new Observer<List<TripEntity>>() {
                @Override
                public void onChanged(List<TripEntity> tripEntityList) {
                    getAllTrips();
                }
            });
        } catch (Exception e) {
            Log.e("View Model Observer Exception", e.getLocalizedMessage());
        }

        return rootFragmentView;
    }


    @Override
    public void onViewCreated(@NonNull View fragmentView, @Nullable Bundle savedInstanceState) {

        // Initialise the ArrayList that contains the Uris
        tripEntityList = new ArrayList<>();

        // Get the RecyclerView from the view binding object
        myTripsRecyclerView = fragmentMyTripsBinding.myTripsRecyclerView;

        // Set up the LayoutManager for the RecyclerView
        myTripsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false));
        myTripsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // Set up the adapter for the RecyclerView
        allTripsAdapter = new MyTripsRecyclerViewAdapter(tripEntityList);
        tripListViewModel.getAllTrips().observe(getViewLifecycleOwner(), existingTripEntityList -> allTripsAdapter.updateTripList(existingTripEntityList));
        myTripsRecyclerView.setAdapter(allTripsAdapter);

        // Get the RefreshLayout from the view binding object
        tripRefreshLayout = fragmentMyTripsBinding.myTripsRefreshLayout;

        tripRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tripRefreshLayout.setRefreshing(true);
                refreshAllTrips();
                tripRefreshLayout.setRefreshing(false);
            }
        });
        tripRefreshLayout.post(() -> {
            // Allow for the user to manually refresh the trips in the list
            tripRefreshLayout.setRefreshing(true);
            // Get all trips from the ViewModel and add them to the adapter
            refreshAllTrips();
            tripRefreshLayout.setRefreshing(false);
        });

        MaterialButton filterTripButton = fragmentMyTripsBinding.filterTripButton;

        FloatingActionButton addNewTripButton = fragmentMyTripsBinding.addNewTripButton;

        // Once the filter button is clicked, show the menu that filters the items in the RecyclerView
        filterTripButton.setOnClickListener(filterRecyclerView -> showTripFilterMenu(filterRecyclerView, R.menu.trip_filter_popup_menu));

        // Once the button is clicked, the CreateNewTripFragment will launch in the current fragment's parent layout
        addNewTripButton.setOnClickListener(navigateToFragment -> navigateToChildFragment());

    }


    private void navigateToChildFragment() {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        // Get the current fragment (MyTripsFragment) called currentFragment by its tag
        MyTripsFragment currentFragment = (MyTripsFragment) getParentFragmentManager().findFragmentByTag("MY_TRIPS_FRAGMENT");
        // Create an instance of the child fragment (CreateNewTripFragment) and call it childFragment
        CreateNewTripFragment childFragment = CreateNewTripFragment.getNewInstance();
        // Replace the current parent fragment's layout for the child fragment (CreateNewTripFragment)
        fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_from_left_anim, R.anim.fragment_slide_to_right_anim);
        fragmentTransaction.replace(fragmentMyTripsBinding.rootFragmentLayout.getId(), childFragment);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commit();
        // Make sure that the current fragment disappears when navigating to the child fragment
        if (currentFragment == null) {
            fragmentMyTripsBinding.myTripsFragmentLayout.setVisibility(View.GONE);
        } else {
            fragmentMyTripsBinding.myTripsFragmentLayout.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("RestrictedApi")
    private void showTripFilterMenu(View filterRecyclerView, @MenuRes int filter_popup_menu) {
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
        filterPopup.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.ascending_trips) {
                refreshAllAscendingTrips();
                return true;
            } else if (menuItem.getItemId() == R.id.descending_trips) {
                refreshAllDescendingTrips();
                return true;
            } else if (menuItem.getItemId() == R.id.favourite_trips) {
                refreshAllFavouriteTrips();
                return true;
            }
            return false;
        });
        filterPopup.show();
    }


    private void refreshAllTrips() {
        try {
            tripListViewModel.getAllTrips().observe(getViewLifecycleOwner(), existingTripEntityList -> {
                allTripsAdapter.updateTripList(existingTripEntityList);
            });
        } catch (IllegalArgumentException e) {
            Log.e("VIEW_MODEL_EXCEPTION", e.getLocalizedMessage());
        }
    }

    private List<TripEntity> getAllTrips() {
        return tripEntityList = tripListViewModel.getAllTrips().getValue();
    }

    private void refreshAllAscendingTrips() {
        tripListViewModel.getAllTripsAscendingOrder().observe(getViewLifecycleOwner(), tripEntityList -> allTripsAdapter.updateTripList(tripEntityList));
    }


    private void refreshAllDescendingTrips() {
        tripListViewModel.getAllTripsDescendingOrder().observe(getViewLifecycleOwner(), tripEntityList -> allTripsAdapter.updateTripList(tripEntityList));
    }

    private void refreshAllFavouriteTrips() {
        tripListViewModel.getAllFavouriteTrips().observe(getViewLifecycleOwner(), tripEntityList -> allTripsAdapter.updateTripList(tripEntityList));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentMyTripsBinding = null;
    }


    public class MyTripsRecyclerViewAdapter extends RecyclerView.Adapter<MyTripsRecyclerViewAdapter.MyTripsViewHolder> {

        private TripCardViewBinding bindingTripCardView;


        public MyTripsRecyclerViewAdapter(List<TripEntity> updatedTripEntityList) {
            tripEntityList = updatedTripEntityList;
        }

        class MyTripsViewHolder extends RecyclerView.ViewHolder {

            public MyTripsViewHolder(TripCardViewBinding bindingTripCardView) {
                super(bindingTripCardView.getRoot());
            }
        }

        @NonNull
        @Override
        public MyTripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            bindingTripCardView = TripCardViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new MyTripsViewHolder(bindingTripCardView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyTripsViewHolder myTripsViewHolder, int position) {
            if (tripEntityList.get(position) != null) {
                bindingTripCardView.tripName.setText(tripEntityList.get(position).getTripName());
                bindingTripCardView.tripLocation.setText(tripEntityList.get(position).getTripLocation());
                bindingTripCardView.tripStartDate.setText(String.format(Locale.getDefault(), tripEntityList.get(position).getTripStartDate().toString()));
                bindingTripCardView.tripEndDate.setText(String.format(Locale.getDefault(), tripEntityList.get(position).getTripEndDate().toString()));
            }
        }

        @Override
        public int getItemCount() {
            return tripEntityList.size();
        }

        public void updateTripList(final List<TripEntity> updatedTripEntityList) {
            tripEntityList.clear();
            tripEntityList = updatedTripEntityList;
            notifyItemChanged(getItemCount());
        }

    }

}