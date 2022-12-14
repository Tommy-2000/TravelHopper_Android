package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.bumptech.glide.Glide;

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

    @ColumnInfo(name = "trip_media_uri")
    private final String tripMediaUri;

    @ColumnInfo(name = "trip_start_date")
    private final String tripStartDate;

    @ColumnInfo(name = "trip_end_date")
    private final String tripEndDate;

    @ColumnInfo(name = "trip_details")
    private final String tripDetails;

    @NonNull
    @ColumnInfo(name = "trip_favourite")
    private final boolean tripFavourite;

    public TripEntity(int tripID, @NonNull String tripName, String tripLocation, String tripMediaUri, @NonNull String tripStartDate, @NonNull String tripEndDate, String tripDetails, boolean tripFavourite) {
        this.tripID = tripID;
        this.tripName = tripName;
        this.tripLocation = tripLocation;
        this.tripMediaUri = tripMediaUri;
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

    public String getTripMediaUri() {
        return tripMediaUri;
    }


    @BindingAdapter("imageFromUri")
    public static void tripImageFromUri(ImageView imageView, String tripMediaUri) {
        if (tripMediaUri != null && !tripMediaUri.isEmpty()) {
            Glide.with(imageView.getContext()).load(tripMediaUri).into(imageView);
        }
    }

    public String getTripStartDate() {
        return tripStartDate;
    }

    public String getTripEndDate() {
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

//@Fts4(contentEntity = TripEntity.class)
//class TripEntityFTS {
//    @NonNull
//    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = "tripId")
//    int tripID;
//
//    @NonNull
//    @ColumnInfo(name = "trip_name")
//    private String tripName;
//
//    @ColumnInfo(name = "trip_location")
//    private String tripLocation;
//
//    @ColumnInfo(name = "trip_media_path")
//    private String tripMediaPath;
//
//    @ColumnInfo(name = "trip_start_date")
//    private Long tripStartDate;
//
//    @ColumnInfo(name = "trip_end_date")
//    private Long tripEndDate;
//
//    @ColumnInfo(name = "trip_details")
//    private String tripDetails;
//
//    @NonNull
//    @ColumnInfo(name = "trip_favourite")
//    private boolean tripFavourite;
//
//}


