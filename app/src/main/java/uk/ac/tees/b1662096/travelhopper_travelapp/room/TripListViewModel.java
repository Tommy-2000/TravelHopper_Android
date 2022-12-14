package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class TripListViewModel extends ViewModel {

    private TravelHopperRepository travelHopperRepository;

    private LiveData<List<TripEntity>> allTripsLiveData;

    private List<TripEntity> allTripsList;

    private List<TripEntity> allFavouriteTripsList;

    private LiveData<List<TripEntity>> allFavouriteTripsLiveData;

    public TripListViewModel(@NonNull TravelHopperRepository travelHopperRepository) {
        this.travelHopperRepository = travelHopperRepository;
        allTripsLiveData = travelHopperRepository.getAllTripEntities();
        allFavouriteTripsLiveData = travelHopperRepository.getAllFavouriteTrips();
    }


    public LiveData<List<TripEntity>> getAllTrips() {
        return allTripsLiveData;
    }

    public LiveData<List<TripEntity>> getAllTripsByName(String tripName) {
        return travelHopperRepository.getAllTripsByName(tripName);
    }

    public LiveData<List<TripEntity>> getAllTripsByLocation(String tripLocation) {
        return travelHopperRepository.getAllTripsByLocation(tripLocation);
    }

    public LiveData<List<TripEntity>> getAllTripsAscendingOrder() {
        return travelHopperRepository.getAllTripsAscendingOrder();
    }

    public LiveData<List<TripEntity>> getAllTripsDescendingOrder() {
        return travelHopperRepository.getAllTripsDescendingOrder();
    }

    public LiveData<List<TripEntity>> getAllTripsByStartDate(Long tripStartDate) {
        return travelHopperRepository.getAllTripsByStartDate(tripStartDate);
    }

    public LiveData<List<TripEntity>> getAllTripsByEndDate(Long tripEndDate) {
        return travelHopperRepository.getAllTripsByEndDate(tripEndDate);
    }

    public LiveData<List<TripEntity>> getAllFavouriteTrips() {
        return allFavouriteTripsLiveData;
    }

    public void insertTrip(TripEntity tripEntity) {
        travelHopperRepository.insertTripEntity(tripEntity);
        updateAllTrips(allTripsList);
    }

    public void insertAllTrips(List<TripEntity> allTripEntities) {
        travelHopperRepository.insertAllTripEntities(allTripEntities);
        updateAllTrips(allTripsList);
    }

    public void updateTrip(TripEntity tripEntity) {
        travelHopperRepository.updateTripEntity(tripEntity);
    }

    public void updateFavouriteTripByID(int tripID, boolean isTripFavourite) {
        travelHopperRepository.updateFavouriteTripEntityByID(tripID, isTripFavourite);
    }

    public void updateAllTrips(List<TripEntity> allTripEntities) {
        travelHopperRepository.updateAllTripEntities(allTripEntities);
    }

    public void deleteTrip(TripEntity tripEntity) {
        travelHopperRepository.deleteTripEntity(tripEntity);
    }

    public void deleteAllTrips(List<TripEntity> allTripEntities) {
        travelHopperRepository.deleteAllTripEntities(allTripEntities);
    }

}
