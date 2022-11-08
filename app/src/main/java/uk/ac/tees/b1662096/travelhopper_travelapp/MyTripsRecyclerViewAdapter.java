package uk.ac.tees.b1662096.travelhopper_travelapp;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentMyTripsBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.TripCardViewBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.MediaEntity;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripEntity;

import java.util.List;


public class MyTripsRecyclerViewAdapter extends RecyclerView.Adapter<MyTripsRecyclerViewAdapter.ViewHolder> {

    private final List<TripEntity> tripEntityList;

    public MyTripsRecyclerViewAdapter(List<TripEntity> tripEntityItems) {
        tripEntityList = tripEntityItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tripName;
        public final TextView tripDate;
        public final CheckBox tripFavourite;

        public ViewHolder(TripCardViewBinding binding) {
            super(binding.getRoot());
            tripName = binding.tripTitle;
            tripDate = binding.tripDate;
            tripFavourite = binding.tripFavouriteIcon;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(TripCardViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.tripName.setText(tripEntityList.get(position).getTripName());
        holder.tripDate.setText(tripEntityList.get(position).getTripDate());
        holder.tripFavourite.setChecked(tripEntityList.get(position).isTripFavourite());
    }

    @Override
    public int getItemCount() {
        return tripEntityList.size();
    }

}