package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentMyHomeBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.MyHomeTripCardViewBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.TripCardViewBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripEntity;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripListViewModel;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripListViewModelFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyHomeFragment#getNewInstance} factory method to
 * create an instance of this fragment.
 */
public class MyHomeFragment extends Fragment {

    private FragmentMyHomeBinding fragmentMyHomeBinding;

    private View rootFragmentView;

    private MyHomeTripCardViewBinding myHomeTripCardViewBinding;

    private FirebaseStorage firebaseStorage;

    private StorageReference rootStorageRef;

    private TripListViewModelFactory tripListViewModelFactory;

    private TripListViewModel tripListViewModel;

    private ArrayList<Uri> homeMediaList;

    private List<TripEntity> tripEntityList;

    private RecyclerView myHomeCardCarousel;

    private MyTripsRecyclerViewAdapter allTripsAdapter;

    private SwipeRefreshLayout cardCarouselRefreshLayout;



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
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater fragmentInflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the root view from the layout of this fragment and then return it
        fragmentMyHomeBinding = FragmentMyHomeBinding.inflate(fragmentInflater, container, false);
        rootFragmentView = fragmentMyHomeBinding.getRoot();


        // Create an instance of Google Firebase Cloud Storage
        firebaseStorage = FirebaseStorage.getInstance();
        // IMPORTANT! - Make sure to first test the application with Firebase emulators before using in production!
//        firebaseStorage.useEmulator("127.0.0.1", 9199);
        // Get the root reference from Firebase Cloud Storage
        rootStorageRef = firebaseStorage.getReference();


        return rootFragmentView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeMediaList = new ArrayList<>();

        cardCarouselRefreshLayout = fragmentMyHomeBinding.cardCarouselRefreshLayout;

        cardCarouselRefreshLayout.setOnRefreshListener(() -> {
            cardCarouselRefreshLayout.setRefreshing(true);
            listImagesFromFirebase(homeMediaList);
            cardCarouselRefreshLayout.setRefreshing(false);
        });

        cardCarouselRefreshLayout.post(() -> {
            // Allow for the user to manually refresh the recommendations in the list
            cardCarouselRefreshLayout.setRefreshing(true);
            // Get all recommendations from the Firebase Storage and add them to the adapter
            listImagesFromFirebase(homeMediaList);
            cardCarouselRefreshLayout.setRefreshing(false);
        });

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Try to get the images from the Firebase Cloud Storage
            listImagesFromFirebase(homeMediaList);
        }

        myHomeCardCarousel = fragmentMyHomeBinding.myHomeCardCarousel;
    }

    private void listImagesFromFirebase(ArrayList<Uri> homeMediaList) {
        try {
            StorageReference imageStorageRef = rootStorageRef.child("Popular Trips");
            imageStorageRef.listAll().addOnSuccessListener(listResult -> {
                // If successful, get items from the result
                List<StorageReference> listResultItems = listResult.getItems();
                // Get the download Url for each image in the result
                for (StorageReference listItem : listResultItems) {
                    // Add Uris to the ArrayList, then launch the 'Card carousel' with the updated ArrayList
                    listItem.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri firebaseStorageUri) {
                            homeMediaList.add(firebaseStorageUri);
                            // Create the RecyclerViewPreloader
                            ViewPreloadSizeProvider viewPreloadSizeProvider = new ViewPreloadSizeProvider<>();
                            ListPreloader.PreloadModelProvider preloadModelProvider = new MyHomeRecyclerViewPreloadProvider();
                            RecyclerViewPreloader<Uri> recyclerViewPreloader = new RecyclerViewPreloader<Uri>(Glide.with(requireActivity().getApplicationContext()), preloadModelProvider, viewPreloadSizeProvider, 5);
                            myHomeCardCarousel.addOnScrollListener(recyclerViewPreloader);
                            myHomeCardCarousel.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
                            MyHomeRecyclerViewAdapter firebaseStorageAdapter = new MyHomeRecyclerViewAdapter(requireActivity().getApplicationContext(), homeMediaList);
                            myHomeCardCarousel.setAdapter(firebaseStorageAdapter);
                            // Set a decorator for the RecyclerView
                            int tripCardWidthPixels = (int) (requireActivity().getResources().getDisplayMetrics().widthPixels * 0.90f);
                            float tripCardHintPercent = 0.01f;
                            myHomeCardCarousel.addItemDecoration(new MyHomeRecyclerViewDecorator(requireContext(), tripCardWidthPixels, tripCardHintPercent));
                        }
                    }).addOnFailureListener(e -> Log.e("GET_DOWNLOAD_URL_TASK_FAILED", e.getLocalizedMessage()));
                }
            }).addOnFailureListener(e -> Log.e("LIST_RESULT_TASK_FAILED", e.getLocalizedMessage()));
        } catch (OutOfMemoryError e) {
            Log.e("OUT_OF_MEMORY_ERROR", e.getLocalizedMessage());
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentMyHomeBinding = null;
    }


    public class MyTripsRecyclerViewAdapter extends RecyclerView.Adapter<MyTripsRecyclerViewAdapter.MyHomeTripsViewHolder> {

        private TripCardViewBinding bindingTripCardView;


        public MyTripsRecyclerViewAdapter(List<TripEntity> updatedTripEntityList) {
            tripEntityList = updatedTripEntityList;
        }

        class MyHomeTripsViewHolder extends RecyclerView.ViewHolder {

            TripCardViewBinding tripCardViewBinding;

            public MyHomeTripsViewHolder(TripCardViewBinding tripCardViewBinding) {
                super(tripCardViewBinding.getRoot());
                this.tripCardViewBinding = tripCardViewBinding;
            }

            void bindOnClickListener(View.OnClickListener onClickListener) {
                tripCardViewBinding.myHomeTripCardView.setOnClickListener(onClickListener);
            }

        }

        @NonNull
        @Override
        public MyHomeTripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            bindingTripCardView = TripCardViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new MyHomeTripsViewHolder(bindingTripCardView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHomeTripsViewHolder myHomeTripsViewHolder, int position) {
            TripEntity tripEntity = tripEntityList.get(position);
            if (tripEntityList.get(position) != null) {
                bindingTripCardView.tripName.setText(tripEntityList.get(position).getTripName());
                bindingTripCardView.tripLocation.setText(tripEntityList.get(position).getTripLocation());
                bindingTripCardView.tripStartDate.setText(tripEntityList.get(position).getTripStartDate());
                bindingTripCardView.tripEndDate.setText(tripEntityList.get(position).getTripEndDate());
                bindingTripCardView.tripFavouriteIcon.setChecked(tripEntityList.get(position).isTripFavourite());
            }

            myHomeTripsViewHolder.bindOnClickListener(createOnClickListener(String.valueOf(tripEntity.getTripID())));

        }


        private View.OnClickListener createOnClickListener(String tripID) {
            return holderView -> {
                NavDirections tripDetailsNavigation = MyTripsFragmentDirections.actionMyTripsFragmentToTripDetailsFragment(tripID);
                Navigation.findNavController(holderView).navigate(tripDetailsNavigation);
                Snackbar.make(rootFragmentView, "Trip has been clicked", Snackbar.LENGTH_SHORT).show();
            };
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

    class MyHomeRecyclerViewPreloadProvider implements ListPreloader.PreloadModelProvider {


        @NonNull
        @Override
        public List getPreloadItems(int position) {
            Uri mediaUri = homeMediaList.get(position);
            if (mediaUri == null) {
                return Collections.emptyList();
            }
            return Collections.singletonList(mediaUri);
        }

        @Nullable
        @Override
        public RequestBuilder<?> getPreloadRequestBuilder(@NonNull Object mediaUri) {
            return Glide.with(MyHomeFragment.this).load(mediaUri);
        }
    }

    public class MyHomeRecyclerViewAdapter extends RecyclerView.Adapter<MyHomeRecyclerViewAdapter.MyHomeViewHolder> {

        Context adapterContext;
        ArrayList<Uri> homeMediaList;

        public MyHomeRecyclerViewAdapter(Context adapterContext, ArrayList<Uri> homeMediaList) {
            this.adapterContext = adapterContext;
            this.homeMediaList = homeMediaList;
        }

        public class MyHomeViewHolder extends RecyclerView.ViewHolder {

            final ImageView imageView;

            public MyHomeViewHolder(MyHomeTripCardViewBinding myHomeTripCardViewBinding) {
                super(myHomeTripCardViewBinding.getRoot());
                imageView = myHomeTripCardViewBinding.tripMedia;
            }
        }

        @NonNull
        @Override
        public MyHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            myHomeTripCardViewBinding = MyHomeTripCardViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new MyHomeViewHolder(myHomeTripCardViewBinding);
        }

        @Override
        public int getItemCount() {
            return homeMediaList.size();
        }

        @Override
        public void onBindViewHolder(final MyHomeViewHolder myHomeViewHolder, int position) {
            // Use Glide to load the Uri's into the binding view by using the context of the ViewHolder items
            Glide.with(myHomeViewHolder.itemView.getContext())
                    .load(homeMediaList.get(position))
                    .centerCrop()
                    .thumbnail(Glide.with(MyHomeFragment.this).load(R.drawable.ic_media_placeholder_icon))
                    .placeholder(R.drawable.unsplash_placeholder_small)
                    .error(R.drawable.ic_error_icon)
                    .into(myHomeTripCardViewBinding.tripMedia);

            // Check that the imageView containing the image in the ViewHolder is clickable
            myHomeTripCardViewBinding.tripMedia.setOnClickListener(view -> Snackbar.make(myHomeTripCardViewBinding.tripMedia.getRootView(), "Recommended trip has been clicked", Snackbar.LENGTH_SHORT).show());
        }

    }

}