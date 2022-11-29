package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TravelHopperDAO {

    @Query("SELECT * FROM tripEntity")
    LiveData<List<TripEntity>> getAllTrips();

    @Query("SELECT tripId FROM tripEntity")
    int getIDFromTrip();

    @Query("SELECT trip_name FROM tripEntity")
    String getNameFromTrip();

    @Query("SELECT trip_location FROM tripEntity")
    String getLocationFromTrip();

    @Query("SELECT trip_media_path FROM tripEntity")
    String getMediaPathFromTrip();

    @Query("SELECT trip_start_date FROM tripEntity")
    Long getStartDateFromTrip();

    @Query("SELECT trip_end_date FROM tripEntity")
    Long getEndDateFromTrip();

    @Query("SELECT trip_details FROM tripEntity")
    String getDetailsFromTrip();

    @Query("SELECT * FROM tripEntity WHERE trip_name = :tripName")
    LiveData<List<TripEntity>> getAllTripsByName(String tripName);

    @Query("SELECT * FROM tripEntity ORDER BY trip_name ASC")
    LiveData<List<TripEntity>> getAllTripsAlphabetically();

    @Query("SELECT * FROM tripEntity ORDER BY trip_name DESC")
    LiveData<List<TripEntity>> getAllTripsReverseAlphabetically();

    @Query("SELECT * FROM tripEntity WHERE trip_location = :tripLocation")
    LiveData<List<TripEntity>> getAllTripsByLocation(String tripLocation);

    @Query("SELECT * FROM tripEntity WHERE trip_start_date = :tripStartDate")
    LiveData<List<TripEntity>> getAllTripsByStartDate(Long tripStartDate);

    @Query("SELECT * FROM tripEntity WHERE trip_end_date = :tripEndDate")
    LiveData<List<TripEntity>> getAllTripsByEndDate(Long tripEndDate);

    @Query("SELECT * FROM tripEntity WHERE trip_media_path = :tripMediaPath")
    LiveData<List<TripEntity>> getAllTripsByMediaPath(String tripMediaPath);

    @Query("SELECT * FROM tripEntity WHERE trip_favourite LIKE :tripIsFavourite")
    LiveData<List<TripEntity>> getAllFavouriteTrips(boolean tripIsFavourite);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTripEntity(TripEntity tripEntity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllTripEntities(List<TripEntity> allTripEntities);

    @Update
    void updateTripEntity(TripEntity tripEntity);

    @Query("UPDATE tripEntity SET trip_favourite = :isTripFavourite WHERE tripId = :tripID")
    void updateFavouriteTripEntityByID(int tripID, boolean isTripFavourite);

    @Update
    void updateAllTripsEntities(List<TripEntity> allTripEntities);

    @Query("UPDATE tripEntity SET trip_favourite = :isTripFavourite")
    void updateAllFavouriteTripEntities(boolean isTripFavourite);

    @Delete
    void deleteTripEntity(TripEntity tripEntity);

    @Delete
    void deleteAllTripEntities(List<TripEntity> allTripEntities);
}
