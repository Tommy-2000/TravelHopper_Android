package uk.ac.tees.b1662096.travelhopper_travelapp.data.model;

import java.io.Serializable;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class TravelHopperUser implements Serializable {

    public String userId;
    public String userDisplayName;
    public String userEmail;
    public boolean isUserAuthenticated, isNewUser, isUserCreated;

    public TravelHopperUser() {}

    public TravelHopperUser(String userId, String userDisplayName, String userEmail) {
        this.userId = userId;
        this.userDisplayName = userDisplayName;
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }


    public String getUserDisplayName() {
        return userDisplayName;
    }


    public String getUserEmail() {
        return userEmail;
    }


}