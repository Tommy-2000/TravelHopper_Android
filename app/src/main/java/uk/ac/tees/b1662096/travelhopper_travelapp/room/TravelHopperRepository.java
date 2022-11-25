package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class TravelHopperRepository {

    private final TravelHopperDAO travelHopperDAO;

    private final LiveData<List<TripEntity>> allTripEntities;

    private final LiveData<List<TripEntity>> favouriteTripEntities;

    TravelHopperRepository(@NonNull Application application) {
        TravelHopperDatabase travelHopperDatabase = TravelHopperDatabase.getDatabase(application);
        travelHopperDAO = travelHopperDatabase.travelHopperDAO();
        allTripEntities = travelHopperDAO.getAllTrips();
        favouriteTripEntities = travelHopperDAO.getAllFavouriteTrips(true);
    }


    @WorkerThread
    LiveData<List<TripEntity>> getAllTripEntities() {
        return allTripEntities;
    }

    @WorkerThread
    LiveData<List<TripEntity>> getAllFavouriteTrips() { return favouriteTripEntities; }

    @WorkerThread
    void insertTripEntity(TripEntity tripEntity) {
        TravelHopperDatabase.databaseWriteExecutor.execute(() -> travelHopperDAO.insertTripEntity(tripEntity));
    }

    @WorkerThread
    void insertFavouriteTripEntity(TripEntity tripEntity, boolean isTripFavourite) {
        TravelHopperDatabase.databaseWriteExecutor.execute(() -> travelHopperDAO.insertFavouriteTripEntity(tripEntity, isTripFavourite));
    }

    @WorkerThread
    void insertAllTripEntities(List<TripEntity> allTripEntities) {
        TravelHopperDatabase.databaseWriteExecutor.execute(() -> travelHopperDAO.insertAllTripEntities(allTripEntities));
    }

    @WorkerThread
    void updateTripEntity(TripEntity tripEntity) {
        TravelHopperDatabase.databaseWriteExecutor.execute(() -> travelHopperDAO.updateTripEntity(tripEntity));
    }

    @WorkerThread
    void updateFavouriteTripEntityByID(TripEntity tripEntity, boolean isTripFavourite, int tripID) {
        TravelHopperDatabase.databaseWriteExecutor.execute(() -> travelHopperDAO.updateFavouriteTripEntityByID(tripEntity, isTripFavourite, tripID));
    }

    @WorkerThread
    void updateAllTripEntities(List<TripEntity> allTripEntities) {
        TravelHopperDatabase.databaseWriteExecutor.execute(() -> travelHopperDAO.updateAllTripsEntities(allTripEntities));
    }

    @WorkerThread
    void deleteTripEntity(TripEntity tripEntity) {
        TravelHopperDatabase.databaseWriteExecutor.execute(() -> travelHopperDAO.deleteTripEntity(tripEntity));
    }

    @WorkerThread
    void deleteAllTripEntities(List<TripEntity> allTripEntities) {
        TravelHopperDatabase.databaseWriteExecutor.execute(() -> travelHopperDAO.deleteAllTripEntities(allTripEntities));
    }

}
