package uk.ac.tees.b1662096.travelhopper_travelapp.ui.firebaseAuth;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import uk.ac.tees.b1662096.travelhopper_travelapp.data.FirebaseAuthRepository;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TravelHopperRepository;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripListViewModel;

public class FirebaseAuthViewModelFactory implements ViewModelProvider.Factory {

    private FirebaseAuthRepository firebaseAuthRepository;

    public FirebaseAuthViewModelFactory(FirebaseAuthRepository firebaseAuthRepository) {
        this.firebaseAuthRepository = firebaseAuthRepository;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        FirebaseAuthViewModel firebaseAuthViewModel = new FirebaseAuthViewModel(firebaseAuthRepository);
        return (T) firebaseAuthViewModel;
    }

}
