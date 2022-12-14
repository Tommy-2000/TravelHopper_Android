package uk.ac.tees.b1662096.travelhopper_travelapp.room;


import androidx.annotation.NonNull;
import androidx.lifecycle.AbstractSavedStateViewModelFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TripListViewModelFactory implements ViewModelProvider.Factory {

    private TravelHopperRepository travelHopperRepository;

    public TripListViewModelFactory(TravelHopperRepository travelHopperRepository) {
        this.travelHopperRepository = travelHopperRepository;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        TripListViewModel tripListViewModel = new TripListViewModel(travelHopperRepository);
        return (T) tripListViewModel;
    }

}
