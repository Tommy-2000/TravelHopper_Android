package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class TripDetailViewModel extends ViewModel {

    private TravelHopperRepository travelHopperRepository;

    private LiveData<List<TripEntity>> allTripsLiveData;

    private LiveData<List<TripEntity>> allFavouriteTripsLiveData;

    public LiveData<TripEntity> tripEntity;

    public String tripID;

    public TripDetailViewModel(@NonNull TravelHopperRepository travelHopperRepository, String tripID) {
        this.travelHopperRepository = travelHopperRepository;
        this.tripEntity = travelHopperRepository.getTripEntity(tripID);
        this.tripID = tripID;
        allTripsLiveData = travelHopperRepository.getAllTripEntities();
        allFavouriteTripsLiveData = travelHopperRepository.getAllFavouriteTrips();
    }

    public LiveData<TripEntity> getTripEntity() {
        return tripEntity;
    }

    public LiveData<List<TripEntity>> getAllTripsLiveData() {
        return allTripsLiveData;
    }

    public LiveData<List<TripEntity>> getAllFavouriteTripsLiveData() {
        return allFavouriteTripsLiveData;
    }

}
