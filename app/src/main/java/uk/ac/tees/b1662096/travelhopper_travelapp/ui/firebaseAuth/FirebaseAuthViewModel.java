package uk.ac.tees.b1662096.travelhopper_travelapp.ui.firebaseAuth;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import android.app.Application;

import com.google.firebase.auth.AuthCredential;

import uk.ac.tees.b1662096.travelhopper_travelapp.data.AuthRepository;
import uk.ac.tees.b1662096.travelhopper_travelapp.data.model.TravelHopperUser;

public class FirebaseAuthViewModel extends AndroidViewModel {

    private AuthRepository authRepository;

    public LiveData<TravelHopperUser> authenticatedUserData;
    public LiveData<TravelHopperUser> createdUserData;

    public FirebaseAuthViewModel(Application application) {
        super(application);
        authRepository = AuthRepository.getInstance();
    }

    public void createNewUser(TravelHopperUser authenticatedUser) {
        createdUserData = authRepository.addNewUserToFirestore(authenticatedUser);
    }

    public void signInWithGoogle(AuthCredential googleAuthCredential) {
        authenticatedUserData = authRepository.firebaseSignInWithGoogle(googleAuthCredential);
    }

}