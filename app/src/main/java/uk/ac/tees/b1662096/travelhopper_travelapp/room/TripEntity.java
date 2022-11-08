package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TripEntity {
    @PrimaryKey
    @NonNull
    private final int tripID;

    @NonNull
    @ColumnInfo(name = "trip_name")
    private final String tripName;

    @ColumnInfo(name = "trip_date")
    private final String tripDate;

    @NonNull
    @ColumnInfo(name = "trip_favourite")
    private final boolean tripFavourite;

    public TripEntity(int tripID, @NonNull String tripName, @NonNull String tripDate, boolean tripFavourite) {
        this.tripID = tripID;
        this.tripName = tripName;
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

    public String getTripDate() {
        return tripDate;
    }

    @NonNull
    public boolean isTripFavourite() {
        return tripFavourite;
    }
}
