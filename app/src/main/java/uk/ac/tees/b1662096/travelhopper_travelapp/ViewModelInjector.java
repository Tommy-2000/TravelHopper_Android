package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.content.Context;

import uk.ac.tees.b1662096.travelhopper_travelapp.data.FirebaseAuthRepository;
import uk.ac.tees.b1662096.travelhopper_travelapp.data.SplashRepository;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TravelHopperDatabase;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TravelHopperRepository;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripDetailViewModelFactory;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.TripListViewModelFactory;
import uk.ac.tees.b1662096.travelhopper_travelapp.ui.firebaseAuth.FirebaseAuthViewModelFactory;
import uk.ac.tees.b1662096.travelhopper_travelapp.ui.splash.SplashViewModelFactory;

public class ViewModelInjector {

    private static TravelHopperRepository getTravelHopperRepository(Context repositoryContext) {
        return TravelHopperRepository.getInstance(TravelHopperDatabase.getDatabase(repositoryContext).travelHopperDAO());
    }

    public static TripListViewModelFactory getTripListViewModelFactory(Context viewModelContext) {
        TravelHopperRepository travelHopperRepository = getTravelHopperRepository(viewModelContext);
        return new TripListViewModelFactory(travelHopperRepository);
    }

    public static TripDetailViewModelFactory getTripDetailViewModelFactory(Context viewModelContext, String tripID) {
        return new TripDetailViewModelFactory(getTravelHopperRepository(viewModelContext), tripID);
    }

    public static FirebaseAuthRepository getFirebaseAuthRepository() {
        return FirebaseAuthRepository.getInstance();
    }

    public static FirebaseAuthViewModelFactory getFirebaseAuthViewModelFactory() {
        FirebaseAuthRepository firebaseAuthRepository = getFirebaseAuthRepository();
        return new FirebaseAuthViewModelFactory(firebaseAuthRepository);
    }

    public static SplashRepository getSplashRepository() {
        return SplashRepository.getInstance();
    }

    public static SplashViewModelFactory getSplashViewModelFactory() {
        SplashRepository splashRepository = getSplashRepository();
        return new SplashViewModelFactory(splashRepository);
    }


}
