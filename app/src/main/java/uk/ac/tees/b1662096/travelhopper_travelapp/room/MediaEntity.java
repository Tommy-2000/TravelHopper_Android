package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MediaEntity {
    @PrimaryKey
    @NonNull
    private final int mediaID;

    @NonNull
    @ColumnInfo(name = "media_name")
    private final String mediaName;

    @ColumnInfo(name = "media_date")
    private final String mediaDate;

    @NonNull
    @ColumnInfo(name = "media_favourite")
    private final boolean mediaFavourite;

    public MediaEntity(int mediaID, @NonNull String mediaName, String mediaDate, boolean mediaFavourite) {
        this.mediaID = mediaID;
        this.mediaName = mediaName;
        this.mediaDate = mediaDate;
        this.mediaFavourite = mediaFavourite;
    }

    @NonNull
    public int getMediaID() {
        return mediaID;
    }

    @NonNull
    public String getMediaName() {
        return mediaName;
    }

    public String getMediaDate() {
        return mediaDate;
    }

    @NonNull
    public boolean isMediaFavourite() {
        return mediaFavourite;
    }
}
