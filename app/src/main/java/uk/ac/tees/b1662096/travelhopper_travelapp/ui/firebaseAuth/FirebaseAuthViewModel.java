package uk.ac.tees.b1662096.travelhopper_travelapp.ui.firebaseAuth;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthCredential;

import uk.ac.tees.b1662096.travelhopper_travelapp.data.FirebaseAuthRepository;
import uk.ac.tees.b1662096.travelhopper_travelapp.data.model.TravelHopperUser;

public class FirebaseAuthViewModel extends ViewModel {

    private FirebaseAuthRepository firebaseAuthRepository;

    public MutableLiveData<TravelHopperUser> authenticatedGoogleUserLiveData;

    public MutableLiveData<TravelHopperUser> newGoogleUserLiveData;

    public FirebaseAuthViewModel(FirebaseAuthRepository firebaseAuthRepository) {
        this.firebaseAuthRepository = firebaseAuthRepository;
    }

    public void signInWithGoogle(AuthCredential googleAuthCredential) {
        authenticatedGoogleUserLiveData = firebaseAuthRepository.firebaseSignInWithGoogle(googleAuthCredential);
    }

    public void addNewGoogleUser(TravelHopperUser travelHopperUser) {
        newGoogleUserLiveData = firebaseAuthRepository.addNewUserToFirestore(travelHopperUser);
    }

}