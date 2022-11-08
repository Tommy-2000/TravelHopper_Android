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
    @Query("SELECT * FROM MediaEntity")
    LiveData<List<MediaEntity>> getAllMedia();

    @Query("SELECT * FROM MediaEntity WHERE mediaID IN (:mediaIDs)")
    LiveData<List<MediaEntity>> getAllMediaByID(int[] mediaIDs);

    @Query("SELECT * FROM MediaEntity ORDER BY media_name ASC")
    LiveData<List<MediaEntity>> getAllMediaAlphabetically();

    @Query("SELECT * FROM MediaEntity WHERE media_favourite LIKE :isFavourite")
    LiveData<List<MediaEntity>> findFavouriteMedia(boolean isFavourite);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMedia(MediaEntity mediaEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllMedia(List<MediaEntity> allMediaEntities);

    @Update
    void updateAllMedia(List<MediaEntity> allMediaEntities);

    @Delete
    void deleteMedia(MediaEntity mediaEntity);

    @Delete
    void deleteAllMedia(List<MediaEntity> allMediaEntities);
}
