package uk.ac.tees.b1662096.travelhopper_travelapp.data.model;

import java.io.Serializable;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class TravelHopperUser implements Serializable {

    public String userId;
    public String userName;
    public String userEmail;
    public boolean isUserAuthenticated, isNewUser, isUserCreated;

    public TravelHopperUser() {}

    public TravelHopperUser(String userId, String userName, String userEmail) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

}