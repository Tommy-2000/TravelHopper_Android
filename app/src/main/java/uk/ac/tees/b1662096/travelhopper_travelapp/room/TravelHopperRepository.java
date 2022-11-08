package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TravelHopperRepository {

    private final TravelHopperDAO travelHopperDAO;

    private final LiveData<List<MediaEntity>> allMediaEntities;

    TravelHopperRepository(Application application) {
        TravelHopperDatabase travelHopperDatabase = TravelHopperDatabase.getDatabase(application);
        travelHopperDAO = travelHopperDatabase.mediaDAO();
        allMediaEntities = travelHopperDAO.getAllMedia();
    }


    LiveData<List<MediaEntity>> getAllMediaEntities() {
        return allMediaEntities;
    }

    void insertMediaEntity(MediaEntity mediaEntity) {
        TravelHopperDatabase.databaseWriteExecutor.execute(() -> travelHopperDAO.insertMedia(mediaEntity));
    }

    void insertMediaEntities(List<MediaEntity> allMediaEntities) {
        TravelHopperDatabase.databaseWriteExecutor.execute(() -> travelHopperDAO.insertAllMedia(allMediaEntities));
    }

    void deleteMediaEntity(MediaEntity mediaEntity) {
        TravelHopperDatabase.databaseWriteExecutor.execute(() -> travelHopperDAO.deleteMedia(mediaEntity));
    }

}
