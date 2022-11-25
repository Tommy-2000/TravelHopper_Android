package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.Locale;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.TripCardViewBinding;

public class MyTripsViewHolder extends RecyclerView.ViewHolder {

    public final TextView tripName;
    public final TextView tripStartDate;
    public final TextView tripEndDate;
    public final TextView tripLocation;
    public final CheckBox tripFavourite;

    public MyTripsViewHolder(TripCardViewBinding bindingTripCardView) {
        super(bindingTripCardView.getRoot());
        tripName = bindingTripCardView.tripName;
        tripLocation = bindingTripCardView.tripLocation;
        tripStartDate = bindingTripCardView.tripStartDate;
        tripEndDate = bindingTripCardView.tripEndDate;
        tripFavourite = bindingTripCardView.tripFavouriteIcon;
    }

    @NonNull
    static MyTripsViewHolder newInstanceViewHolder(LayoutInflater viewHolderInflater, @NonNull ViewGroup parent) {
        TripCardViewBinding bindingTripCardView = TripCardViewBinding.inflate(viewHolderInflater, parent, false);
        return new MyTripsViewHolder(bindingTripCardView);
    }

    public void bindViewHolder(String name, String location, Long startDate, Long endDate, boolean isFavourite) {
        tripName.setText(name);
        tripLocation.setText(location);
        tripStartDate.setText(String.format(Locale.getDefault(), startDate.toString()));
        tripEndDate.setText(String.format(Locale.getDefault(), endDate.toString()));
        tripFavourite.setChecked(isFavourite);
    }
}
