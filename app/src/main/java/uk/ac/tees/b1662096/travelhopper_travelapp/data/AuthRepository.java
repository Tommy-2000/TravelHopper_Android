package uk.ac.tees.b1662096.travelhopper_travelapp.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
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
public class AuthRepository {

    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private static final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private MutableLiveData<TravelHopperUser> authenticatedEmailPasswordUser = new MutableLiveData<>();

    private MutableLiveData<TravelHopperUser> authenticatedGoogleUser = new MutableLiveData<>();

    private static volatile AuthRepository instance;

    // Singleton method for creating a new instance of this repository
    public static AuthRepository getInstance() {
        // IMPORTANT! - Make sure to first test the application with Firebase emulators before using in production!
        // Initialise Firebase Auth functionality within the repository
        firebaseAuth.useEmulator("127.0.0.1", 9099);
        // Initialise Firestore functionality within the repository
        firebaseFirestore.useEmulator("127.0.0.1", 9000);
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

//    public void firebaseCreateEmailPasswordAccount(String userEmail, String userPassword) {
//        MutableLiveData<TravelHopperUser> emailPasswordUserLiveData = new MutableLiveData<>();
//        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> createEmailPasswordAccountResult) {
//                        if (createEmailPasswordAccountResult.isSuccessful()) {
//                            Log.d("CREATE_EMAIL_PASSWORD_ACCOUNT_SUCCESS", "Successfully created new account with email and password");
//                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//                            if (firebaseUser != null) {
//                                String userID = firebaseUser.getUid();
//                                String userDisplayName = firebaseUser.getDisplayName();
//                                TravelHopperUser emailPasswordUser = new TravelHopperUser(userID, userDisplayName, userEmail);
//                                emailPasswordUserLiveData.setValue(emailPasswordUser);
//                                addNewUserToFirestore(emailPasswordUserLiveData);
//                            }
//                        } else {
//                            Log.d("CREATE_EMAIL_PASSWORD_ACCOUNT_EXCEPTION", Objects.requireNonNull(createEmailPasswordAccountResult.getException()).getLocalizedMessage());
//                        }
//                    }
//                });
//    }

//    public MutableLiveData<TravelHopperUser> firebaseSignInWithEmailPassword(String userEmail, String userPassword) {
//        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword)
//                .addOnCompleteListener(emailPasswordSignInTask -> {
//            if (emailPasswordSignInTask.isSuccessful()) {
//                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//                if (firebaseUser != null) {
//                    authenticatedEmailPasswordUser.setValue(new TravelHopperUser(firebaseUser.getUid(), firebaseUser.getDisplayName(), userEmail));
//                    Log.d("FIREBASE_SIGN_IN_SUCCESSFUL","Successfully signed into Firebase");
//                } else {
//                }
//            } else {
//                Log.e("FIREBASE_AUTHENTICATION_EXCEPTION", Objects.requireNonNull(emailPasswordSignInTask.getException()).getLocalizedMessage());
//            }
//        });
//        return authenticatedEmailPasswordUser;
//    }

    public void firebaseSignInWithGoogle(AuthCredential googleAuthCredential) {
        firebaseAuth.signInWithCredential(googleAuthCredential).addOnCompleteListener(googleSignInTask -> {
            if (googleSignInTask.isSuccessful()) {
                boolean isNewGoogleUser = Objects.requireNonNull(googleSignInTask.getResult().getAdditionalUserInfo()).isNewUser();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String userID = firebaseUser.getUid();
                    String userDisplayName = firebaseUser.getDisplayName();
                    String userEmail = firebaseUser.getEmail();
                    TravelHopperUser travelHopperUser = new TravelHopperUser(userID, userDisplayName, userEmail);
                    authenticatedGoogleUser.setValue(travelHopperUser);
                    if (isNewGoogleUser) {
                        addNewUserToFirestore(authenticatedGoogleUser);
                    }
                }
            } else {
                Log.e("AUTHENTICATION_EXCEPTION", Objects.requireNonNull(googleSignInTask.getException()).getLocalizedMessage());
            }
        });
    }

    public void addNewUserToFirestore(MutableLiveData<TravelHopperUser> authenticatedUser) {
        CollectionReference usersRef = firebaseFirestore.collection("travelhopper_auth");
        DocumentReference userIDRef = usersRef.document(Objects.requireNonNull(authenticatedUser.getValue()).userId);
        userIDRef.get().addOnCompleteListener(addToFirestoreTask -> {
            if (addToFirestoreTask.isSuccessful()) {
                DocumentSnapshot documentResult = addToFirestoreTask.getResult();
                if (!documentResult.exists()) {
                    userIDRef.set(authenticatedUser).addOnCompleteListener(createUserTask -> {
                        if (createUserTask.isSuccessful()) {
                            authenticatedUser.getValue().isUserCreated = true;
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
    }

}