package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "tripEntity")
public class TripEntity {
    @PrimaryKey
    @NonNull
    private final int tripID;

    @NonNull
    @ColumnInfo(name = "trip_name")
    private final String tripName;

    @ColumnInfo(name = "trip_location")
    private final String tripLocation;

    @ColumnInfo(name = "trip_details")
    private final String tripDetails;

    @ColumnInfo(name = "trip_date")
    private final Date tripDate;

    @NonNull
    @ColumnInfo(name = "trip_favourite")
    private final boolean tripFavourite;

    public TripEntity(int tripID, @NonNull String tripName, String tripLocation, String tripDetails, @NonNull Date tripDate, boolean tripFavourite) {
        this.tripID = tripID;
        this.tripName = tripName;
        this.tripLocation = tripLocation;
        this.tripDetails = tripDetails;
        this.tripDate = tripDate;
        this.tripFavourite = tripFavourite;
    }

    @NonNull
    public int getTripID() {
        return tripID;
    }

    @NonNull
    public String getTripName() {
        return tripName;
    }

    public String getTripDetails() {
        return tripDetails;
    }

    public String getTripLocation() {
        return tripLocation;
    }

    public Date getTripDate() {
        return tripDate;
    }

    public boolean isTripFavourite() {
        return tripFavourite;
    }

}
