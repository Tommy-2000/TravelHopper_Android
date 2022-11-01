package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MediaDAO {
    @Query("SELECT * FROM media")
    List<Media> getAll();

    @Query("SELECT * FROM media WHERE mediaID IN (:mediaIDs)")
    List<Media> getAllMediaByID(int[] mediaIDs);

    @Query("SELECT * FROM media ORDER BY media_name ASC")
    List<Media> getAllMediaAlphabetically();

    @Query("SELECT * FROM media WHERE media_favourite LIKE :isFavourite")
    Media findFavouriteMedia(boolean isFavourite);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllMedia(Media media);

    @Delete
    void deleteAllMedia(Media media);
}
