package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TripViewModel extends AndroidViewModel {

    private TravelHopperRepository travelHopperRepository;

    private final LiveData<List<TripEntity>> allTripEntities;

    public TripViewModel(@NonNull Application application) {
        super(application);
        travelHopperRepository = new TravelHopperRepository(application);
        allTripEntities = travelHopperRepository.getAllTripEntities();
    }

    public LiveData<List<TripEntity>> getAllTrips() {
        return allTripEntities;
    }

    public void insertTrip(TripEntity tripEntity) {
        travelHopperRepository.insertTripEntity(tripEntity);
    }

}
