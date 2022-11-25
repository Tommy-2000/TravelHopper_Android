package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TravelHopperDAO {

    @Query("SELECT * FROM tripEntity")
    LiveData<List<TripEntity>> getAllTrips();

    @Query("SELECT * FROM tripEntity WHERE tripID = :tripID")
    LiveData<List<TripEntity>> getAllTripsByID(int tripID);

    @Query("SELECT * FROM tripEntity ORDER BY trip_name ASC")
    LiveData<List<TripEntity>> getAllTripsAlphabetically();

    @Query("SELECT * FROM tripEntity WHERE trip_location = :tripLocation")
    LiveData<List<TripEntity>> getAllTripsByLocation(String tripLocation);

    @Query("SELECT * FROM tripEntity WHERE trip_start_date = :tripStartDate")
    LiveData<List<TripEntity>> getAllTripsByStartDate(Long tripStartDate);

    @Query("SELECT * FROM tripEntity WHERE trip_end_date = :tripEndDate")
    LiveData<List<TripEntity>> getAllTripsByEndDate(Long tripEndDate);

    @Query("SELECT * FROM tripEntity WHERE trip_favourite LIKE :tripIsFavourite")
    LiveData<List<TripEntity>> getAllFavouriteTrips(boolean tripIsFavourite);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTripEntity(TripEntity tripEntity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertFavouriteTripEntity(TripEntity tripEntity, boolean isTripFavourite);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllTripEntities(List<TripEntity> allTripEntities);

    @Update
    void updateTripEntity(TripEntity tripEntity);

    @Query("UPDATE tripEntity SET trip_favourite = :isTripFavourite WHERE tripId = :tripID")
    void updateFavouriteTripEntityByID(TripEntity tripEntity, boolean isTripFavourite, int tripID);

    @Update
    void updateAllTripsEntities(List<TripEntity> allTripEntities);

    @Query("UPDATE tripEntity SET trip_favourite = :isTripFavourite")
    void updateAllFavouriteTripEntities(List<TripEntity> allTripEntities, boolean isTripFavourite);

    @Delete
    void deleteTripEntity(TripEntity tripEntity);

    @Delete
    void deleteAllTripEntities(List<TripEntity> allTripEntities);
}
