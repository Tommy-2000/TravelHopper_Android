package uk.ac.tees.b1662096.travelhopper_travelapp.ui.splash;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import uk.ac.tees.b1662096.travelhopper_travelapp.data.SplashRepository;

public class SplashViewModelFactory implements ViewModelProvider.Factory {

    private SplashRepository splashRepository;

    public SplashViewModelFactory(SplashRepository splashRepository) {
        this.splashRepository = splashRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        SplashViewModel splashViewModel = new SplashViewModel(splashRepository);
        return (T) splashViewModel;
    }
}
