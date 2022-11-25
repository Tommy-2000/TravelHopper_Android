package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TripViewModel extends AndroidViewModel {

    private TravelHopperRepository travelHopperRepository;

    private final LiveData<List<TripEntity>> allTripsLiveData;

    private final LiveData<List<TripEntity>> allFavouriteTripsLiveData;

    public TripViewModel(@NonNull Application application) {
        super(application);
        travelHopperRepository = new TravelHopperRepository(application);
        allTripsLiveData = travelHopperRepository.getAllTripEntities();
        allFavouriteTripsLiveData = travelHopperRepository.getAllFavouriteTrips();
    }


    public LiveData<List<TripEntity>> getAllTrips() {
        return allTripsLiveData;
    }

    public LiveData<List<TripEntity>> getAllFavouriteTrips() {
        return allFavouriteTripsLiveData;
    }

    public void insertTrip(TripEntity tripEntity) {
        travelHopperRepository.insertTripEntity(tripEntity);
    }

    public void insertFavouriteTrip(TripEntity tripEntity, boolean isTripFavourite) {
        travelHopperRepository.insertFavouriteTripEntity(tripEntity, isTripFavourite);
    }

    public void insertAllTrips(List<TripEntity> allTripEntities) {
        travelHopperRepository.insertAllTripEntities(allTripEntities);
    }

    public void deleteTrip(TripEntity tripEntity) {
        travelHopperRepository.deleteTripEntity(tripEntity);
    }

    public void deleteAllTrips(List<TripEntity> allTripEntities) {
        travelHopperRepository.deleteAllTripEntities(allTripEntities);
    }

}
