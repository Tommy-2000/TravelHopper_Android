package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

public class TravelHopperRepository {

    private final TravelHopperDAO travelHopperDAO;

    private final LiveData<List<TripEntity>> allTripEntities;

    TravelHopperRepository(Application application) {
        TravelHopperDatabase travelHopperDatabase = TravelHopperDatabase.getDatabase(application);
        travelHopperDAO = travelHopperDatabase.travelHopperDAO();
        allTripEntities = travelHopperDAO.getAllTrips();
    }


    LiveData<List<TripEntity>> getAllTripEntities() {
        return allTripEntities;
    }

    void insertTripEntity(TripEntity tripEntity) {
        TravelHopperDatabase.databaseWriteExecutor.execute(() -> travelHopperDAO.insertTripEntity(tripEntity));
    }

    void insertAllTripEntities(List<TripEntity> allTripEntities) {
        TravelHopperDatabase.databaseWriteExecutor.execute(() -> travelHopperDAO.insertAllTripEntities(allTripEntities));
    }

    void updateAllTripEntities(List<TripEntity> allTripEntities) {
        TravelHopperDatabase.databaseWriteExecutor.execute(() -> travelHopperDAO.updateAllTripsEntities(allTripEntities));
    }

    void deleteTripEntity(TripEntity tripEntity) {
        TravelHopperDatabase.databaseWriteExecutor.execute(() -> travelHopperDAO.deleteTripEntity(tripEntity));
    }

    void deleteAllTripEntities(List<TripEntity> allTripEntities) {
        TravelHopperDatabase.databaseWriteExecutor.execute(() -> travelHopperDAO.deleteAllTripEntities(allTripEntities));
    }

}
