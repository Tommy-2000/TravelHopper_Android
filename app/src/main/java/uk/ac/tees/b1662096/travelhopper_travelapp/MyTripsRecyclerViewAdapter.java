package uk.ac.tees.b1662096.travelhopper_travelapp;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripEntity;


public class MyTripsRecyclerViewAdapter extends ListAdapter<TripEntity, MyTripsViewHolder> {


    public MyTripsRecyclerViewAdapter(DiffUtil.ItemCallback<TripEntity> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public MyTripsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return MyTripsViewHolder.newInstanceViewHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(final MyTripsViewHolder myTripsViewHolder, int position) {
        TripEntity currentTrip = getItem(position);
        myTripsViewHolder.bindViewHolder(currentTrip.getTripName(), currentTrip.getTripLocation(), currentTrip.getTripStartDate(), currentTrip.getTripEndDate(), currentTrip.isTripFavourite());
    }


    public static class TripEntityDiff extends DiffUtil.ItemCallback<TripEntity> {
        @Override
        public boolean areItemsTheSame(TripEntity oldTrip, TripEntity newTrip) {
            return oldTrip.getTripID() == newTrip.getTripID();
        }

        @Override
        public boolean areContentsTheSame(TripEntity oldTrip, TripEntity newTrip) {
            return oldTrip.getTripName().equals(newTrip.getTripName());
        }
    }
}