package uk.ac.tees.b1662096.travelhopper_travelapp.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Objects;

import uk.ac.tees.b1662096.travelhopper_travelapp.data.model.TravelHopperUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class SplashRepository {

    // Initialise Firebase Auth functionality within the repository
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    // Initialise Firestore functionality within the repository
    private final FirebaseFirestore firestoreRef = FirebaseFirestore.getInstance();
    private final CollectionReference usersRef = firestoreRef.collection("travelhopper_auth");

    private static volatile SplashRepository instance;

    private final TravelHopperUser travelHopperUser = new TravelHopperUser();

//    // Mandatory empty constructor for the repository
//    private SplashRepository() {
//
//    }

    // Singleton method for creating a new instance of this repository
    public static SplashRepository getInstance() {
        if (instance == null) {
            instance = new SplashRepository();
        }
        return instance;
    }

    public MutableLiveData<TravelHopperUser> checkIfUserIsAuthenticated() {
        MutableLiveData<TravelHopperUser> isUserAuthenticatedData = new MutableLiveData<>();
        // Get currently authenticated user through Firebase Auth
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            travelHopperUser.isUserAuthenticated = false;
            isUserAuthenticatedData.setValue(travelHopperUser);
        } else {
            travelHopperUser.userId = firebaseUser.getUid();
            travelHopperUser.isUserAuthenticated = true;
            isUserAuthenticatedData.setValue(travelHopperUser);
        }
        return isUserAuthenticatedData;
    }

    public MutableLiveData<TravelHopperUser> addUserToLiveData(String userID) {
        MutableLiveData<TravelHopperUser> travelHopperUserLiveData = new MutableLiveData<>();
        usersRef.document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> snapshotTask) {
                if (snapshotTask.isSuccessful()) {
                    DocumentSnapshot documentResult = snapshotTask.getResult();
                    if (documentResult.exists()) {
                        TravelHopperUser travelHopperUser = documentResult.toObject(TravelHopperUser.class);
                        travelHopperUserLiveData.setValue(travelHopperUser);
                    }
                } else {
                    Log.e("SNAPSHOT_TASK_EXCEPTION", Objects.requireNonNull(snapshotTask.getException()).getLocalizedMessage());
                }
            }
        });
        return travelHopperUserLiveData;
    }

}