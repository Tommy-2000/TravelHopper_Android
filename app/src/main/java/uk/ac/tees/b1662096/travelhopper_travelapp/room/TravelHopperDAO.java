package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface TravelHopperDAO {

    @Query("SELECT * FROM tripEntity")
    LiveData<List<TripEntity>> getAllTrips();

    @Query("SELECT * FROM tripEntity WHERE tripID = :tripID")
    LiveData<List<TripEntity>> getTripsByID(int tripID);

    @Query("SELECT * FROM tripEntity ORDER BY trip_name ASC")
    LiveData<List<TripEntity>> getAllTripsAlphabetically();

    @Query("SELECT * FROM tripEntity WHERE trip_location = :tripLocation")
    LiveData<List<TripEntity>> getTripByLocation(String tripLocation);

    @Query("SELECT * FROM tripEntity WHERE trip_date = :tripDate")
    LiveData<List<TripEntity>> getTripByDate(Date tripDate);

    @Query("SELECT * FROM tripEntity WHERE trip_favourite LIKE :tripIsFavourite")
    LiveData<List<TripEntity>> findFavouriteTrip(boolean tripIsFavourite);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTripEntity(TripEntity tripEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllTripEntities(List<TripEntity> allTripEntities);

    @Update
    void updateAllTripsEntities(List<TripEntity> allTripEntities);

    @Delete
    void deleteTripEntity(TripEntity tripEntity);

    @Delete
    void deleteAllTripEntities(List<TripEntity> allTripEntities);
}
