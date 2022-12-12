package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TravelHopperRepository {

    private final TravelHopperDAO travelHopperDAO;

    private static volatile TravelHopperRepository travelHopperRepositoryInstance;

    TravelHopperRepository(@NonNull Application application) {
        TravelHopperDatabase travelHopperDatabase = TravelHopperDatabase.getDatabase(application);
        travelHopperDAO = travelHopperDatabase.travelHopperDAO();
    }

    public static TravelHopperRepository getInstance(Application application) {
        if (travelHopperRepositoryInstance == null) {
            synchronized (TravelHopperRepository.class) {
                if (travelHopperRepositoryInstance == null) {
                    travelHopperRepositoryInstance = new TravelHopperRepository(application);
                }
            }
        }

        return travelHopperRepositoryInstance;
    }

    @WorkerThread
    LiveData<List<TripEntity>> getAllTripEntities() {
        return travelHopperDAO.getAllTrips();
    }

    @WorkerThread
    int getIDFromTripEntity() {
        return travelHopperDAO.getIDFromTrip();
    }

    @WorkerThread
    String getNameFromTripEntity() {
        return travelHopperDAO.getNameFromTrip();
    }

    @WorkerThread
    String getLocationFromTripEntity() {
        return travelHopperDAO.getLocationFromTrip();
    }

    @WorkerThread
    String getMediaPathFromTripEntity() {
        return travelHopperDAO.getMediaPathFromTrip();
    }

    @WorkerThread
    String getStartDateFromTripEntity() {
        return travelHopperDAO.getStartDateFromTrip();
    }

    @WorkerThread
    String getEndDateFromTripEntity() {
        return travelHopperDAO.getEndDateFromTrip();
    }

    @WorkerThread
    String getDetailsFromTripEntity() {
        return travelHopperDAO.getDetailsFromTrip();
    }


    @WorkerThread
    LiveData<List<TripEntity>> getAllFavouriteTrips() {
        return travelHopperDAO.getAllFavouriteTrips(true);
    }

    @WorkerThread
    LiveData<List<TripEntity>> getAllTripsByName(String tripName) {
        return travelHopperDAO.getAllTripsByName(tripName);
    }

    @WorkerThread
    LiveData<List<TripEntity>> getAllTripsAscendingOrder() {
        return travelHopperDAO.getAllTripsByAscendingOrder();
    }

    @WorkerThread
    LiveData<List<TripEntity>> getAllTripsDescendingOrder() {
        return travelHopperDAO.getAllTripsByDescendingOrder();
    }

    @WorkerThread
    LiveData<List<TripEntity>> getAllTripsByLocation(String tripLocation) {
        return travelHopperDAO.getAllTripsByLocation(tripLocation);
    }

    @WorkerThread
    LiveData<List<TripEntity>> getAllTripsByStartDate(Long tripStartDate) {
        return travelHopperDAO.getAllTripsByStartDate(tripStartDate);
    }

    @WorkerThread
    LiveData<List<TripEntity>> getAllTripsByEndDate(Long tripEndDate) {
        return travelHopperDAO.getAllTripsByEndDate(tripEndDate);
    }

    @WorkerThread
    void insertTripEntity(TripEntity tripEntity) {
        TravelHopperDatabase.databaseWriteExecutor.execute(() -> travelHopperDAO.insertTripEntity(tripEntity));
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
    void updateFavouriteTripEntityByID(int tripID, boolean isTripFavourite) {
        TravelHopperDatabase.databaseWriteExecutor.execute(() -> travelHopperDAO.updateFavouriteTripEntityByID(tripID, isTripFavourite));
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
