package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tripEntity")
public class TripEntity {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tripId")
    private final int tripID;

    @NonNull
    @ColumnInfo(name = "trip_name")
    private final String tripName;

    @ColumnInfo(name = "trip_location")
    private final String tripLocation;

    @ColumnInfo(name = "trip_start_date")
    private final Long tripStartDate;

    @ColumnInfo(name = "trip_end_date")
    private final Long tripEndDate;

    @ColumnInfo(name = "trip_details")
    private final String tripDetails;

    @NonNull
    @ColumnInfo(name = "trip_favourite")
    private final boolean tripFavourite;

    public TripEntity(int tripID, @NonNull String tripName, String tripLocation, @NonNull Long tripStartDate, @NonNull Long tripEndDate, String tripDetails, boolean tripFavourite) {
        this.tripID = tripID;
        this.tripName = tripName;
        this.tripLocation = tripLocation;
        this.tripStartDate = tripStartDate;
        this.tripEndDate = tripEndDate;
        this.tripDetails = tripDetails;
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

    public String getTripLocation() {
        return tripLocation;
    }

    public Long getTripStartDate() {
        return tripStartDate;
    }

    public Long getTripEndDate() {
        return tripEndDate;
    }

    public String getTripDetails() {
        return tripDetails;
    }

    @NonNull
    public boolean isTripFavourite() {
        return tripFavourite;
    }

}
