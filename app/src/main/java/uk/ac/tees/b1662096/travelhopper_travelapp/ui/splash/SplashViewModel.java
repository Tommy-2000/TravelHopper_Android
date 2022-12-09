package uk.ac.tees.b1662096.travelhopper_travelapp.ui.splash;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import uk.ac.tees.b1662096.travelhopper_travelapp.data.SplashRepository;
import uk.ac.tees.b1662096.travelhopper_travelapp.data.model.TravelHopperUser;

public class SplashViewModel extends AndroidViewModel {

    private SplashRepository splashRepository;

    public LiveData<TravelHopperUser> isUserAuthenticatedData;
    public LiveData<TravelHopperUser> userLiveData;

    public SplashViewModel(Application application) {
        super(application);
        splashRepository = SplashRepository.getInstance();
    }

    public void checkIfUserIsAuthenticated() {
        isUserAuthenticatedData = splashRepository.checkIfUserIsAuthenticated();
    }

    public void setUserIDData(String userID) {
        userLiveData = splashRepository.addUserToLiveData(userID);
    }

}