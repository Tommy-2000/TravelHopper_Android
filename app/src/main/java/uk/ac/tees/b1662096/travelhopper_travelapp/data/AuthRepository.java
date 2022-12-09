package uk.ac.tees.b1662096.travelhopper_travelapp.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import uk.ac.tees.b1662096.travelhopper_travelapp.data.model.TravelHopperUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class AuthRepository {

    // Initialise Firebase Auth functionality within the repository
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    // Initialise Firestore functionality within the repository
    private final FirebaseFirestore firestoreRef = FirebaseFirestore.getInstance();
    private final CollectionReference usersRef = firestoreRef.collection("travelhopper_auth");

    private static volatile AuthRepository instance;

    // Singleton method for creating a new instance of this repository
    public static AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    public MutableLiveData<TravelHopperUser> firebaseSignInWithGoogle(AuthCredential googleAuthCredential) {
        MutableLiveData<TravelHopperUser> authenticatedUser = new MutableLiveData<>();
        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                boolean isNewUser = Objects.requireNonNull(authTask.getResult().getAdditionalUserInfo()).isNewUser();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String userID = firebaseUser.getUid();
                    String userDisplayName = firebaseUser.getDisplayName();
                    String userEmail = firebaseUser.getEmail();
                    TravelHopperUser travelHopperUser = new TravelHopperUser(userID, userDisplayName, userEmail);
                    travelHopperUser.isNewUser = isNewUser;
                    authenticatedUser.setValue(travelHopperUser);
                }
            } else {
                Log.e("AUTHENTICATION_EXCEPTION", Objects.requireNonNull(authTask.getException()).getMessage());
            }
        });
        return authenticatedUser;
    }

    public MutableLiveData<TravelHopperUser> addNewUserToFirestore(TravelHopperUser authenticatedUser) {
        MutableLiveData<TravelHopperUser> newUserLiveData = new MutableLiveData<>();
        DocumentReference userIDRef = usersRef.document(authenticatedUser.getUserId());
        userIDRef.get().addOnCompleteListener(userIDTask -> {
            if (userIDTask.isSuccessful()) {
                DocumentSnapshot documentResult = userIDTask.getResult();
                if (!documentResult.exists()) {
                    userIDRef.set(authenticatedUser).addOnCompleteListener(createUserTask -> {
                        if (createUserTask.isSuccessful()) {
                            authenticatedUser.isUserCreated = true;
                            newUserLiveData.setValue(authenticatedUser);
                        } else {
                            Log.e("CREATE_USER_EXCEPTION", Objects.requireNonNull(createUserTask.getException()).getMessage());
                        }
                    });
                } else {
                    newUserLiveData.setValue(authenticatedUser);
                }
            } else {
                Log.e("USER_ID_TASK_EXCEPTION", Objects.requireNonNull(userIDTask.getException()).getMessage());
            }
        });
        return newUserLiveData;
    }

}