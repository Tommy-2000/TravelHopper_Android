package uk.ac.tees.b1662096.travelhopper_travelapp.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
public class FirebaseAuthRepository {

    private FirebaseAuth firebaseAuth;

    private SignInClient oneTapClient;

    private FirebaseFirestore firebaseFirestore;



    public FirebaseAuthRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private static volatile FirebaseAuthRepository instance;


    // Singleton method for creating a new instance of this repository
    public static FirebaseAuthRepository getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthRepository();
        }
        return instance;
    }



    public MutableLiveData<TravelHopperUser> firebaseSignInWithGoogle(AuthCredential googleAuthCredential) {
        MutableLiveData<TravelHopperUser> authenticatedGoogleUser = new MutableLiveData<>();
        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener(googleSignInTask -> {
            if (googleSignInTask.isSuccessful()) {
                boolean isNewGoogleUser = Objects.requireNonNull(googleSignInTask.getResult().getAdditionalUserInfo()).isNewUser();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String userID = firebaseUser.getUid();
                    String userDisplayName = firebaseUser.getDisplayName();
                    String userEmail = firebaseUser.getEmail();
                    TravelHopperUser travelHopperUser = new TravelHopperUser(userID, userDisplayName, userEmail);
                    travelHopperUser.isNewUser = isNewGoogleUser;
                    authenticatedGoogleUser.setValue(travelHopperUser);
                    googleSignInTask.isSuccessful();
                } else {
                    Log.e("FIREBASE_USER_EXCEPTION", "Firebase user returned null");
                    googleSignInTask.isCanceled();
                }
            } else {
                Log.e("GOOGLE_SIGN_IN_TASK_EXCEPTION", googleSignInTask.getException().getLocalizedMessage());
            }
        });
        return authenticatedGoogleUser;
    }

    public MutableLiveData<TravelHopperUser> addNewUserToFirestore(TravelHopperUser authenticatedUser) {
        MutableLiveData<TravelHopperUser> newUserLiveData = new MutableLiveData<>();
        CollectionReference usersRef = firebaseFirestore.collection("travelhopper_auth");
        DocumentReference userIDRef = usersRef.document(Objects.requireNonNull(authenticatedUser).userId);
        userIDRef.get().addOnCompleteListener(addToFirestoreTask -> {
            if (addToFirestoreTask.isSuccessful()) {
                DocumentSnapshot documentResult = addToFirestoreTask.getResult();
                if (!documentResult.exists()) {
                    userIDRef.set(authenticatedUser).addOnCompleteListener(createUserTask -> {
                        if (createUserTask.isSuccessful()) {
                            authenticatedUser.isUserCreated = true;
                            Log.d("CREATE_USER_SUCCESSFUL", "New user has been successfully created");
                        } else {
                            Log.e("CREATE_USER_EXCEPTION", Objects.requireNonNull(createUserTask.getException()).getLocalizedMessage());
                        }
                    });
                }
            } else {
                Log.e("USER_ID_TASK_EXCEPTION", Objects.requireNonNull(addToFirestoreTask.getException()).getLocalizedMessage());
            }
        });
        return newUserLiveData;
    }

}