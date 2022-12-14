package uk.ac.tees.b1662096.travelhopper_travelapp.room;


import androidx.annotation.NonNull;
import androidx.lifecycle.AbstractSavedStateViewModelFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TripDetailViewModelFactory implements ViewModelProvider.Factory {

    private TravelHopperRepository travelHopperRepository;

    private String tripID;

    public TripDetailViewModelFactory(TravelHopperRepository travelHopperRepository, String tripID) {
        this.travelHopperRepository = travelHopperRepository;
        this.tripID = tripID;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        TripDetailViewModel tripDetailViewModel = new TripDetailViewModel(travelHopperRepository, tripID);
        return (T) tripDetailViewModel;
    }
}
