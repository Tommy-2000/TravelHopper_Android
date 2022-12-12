package uk.ac.tees.b1662096.travelhopper_travelapp.ui.firebaseAuth;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.app.Application;

import com.google.firebase.auth.AuthCredential;

import uk.ac.tees.b1662096.travelhopper_travelapp.data.AuthRepository;
import uk.ac.tees.b1662096.travelhopper_travelapp.data.model.TravelHopperUser;

public class FirebaseAuthViewModel extends AndroidViewModel {

    private AuthRepository authRepository;

//    public MutableLiveData<TravelHopperUser> authenticatedEmailPasswordUserData;
//    public MutableLiveData<TravelHopperUser> newEmailPasswordUserData;
//    public MutableLiveData<TravelHopperUser> authenticatedGoogleLiveData;

    public FirebaseAuthViewModel(Application application) {
        super(application);
        authRepository = AuthRepository.getInstance();
    }

//    public void createNewEmailPasswordUser(String userName, String userPassword) {
//        authRepository.firebaseCreateEmailPasswordAccount(userName, userPassword);
//    }
//

//    public void addNewGoogleUser(TravelHopperUser googleAccountUser) {
//        authenticatedGoogleLiveData.setValue(googleAccountUser);
//        authRepository.addNewUserToFirestore(authenticatedGoogleLiveData);
//    }

    public void signInWithGoogle(AuthCredential googleAuthCredential) {
        authRepository.firebaseSignInWithGoogle(googleAuthCredential);
    }

}