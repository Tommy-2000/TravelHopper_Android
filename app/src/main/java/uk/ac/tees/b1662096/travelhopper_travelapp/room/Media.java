package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Date;

@Entity
public class Media {
    @PrimaryKey
    @NonNull
    public int mediaID;

    @NonNull
    @ColumnInfo(name = "media_name")
    public String mediaDisplayName;

    @ColumnInfo(name = "media_title")
    public String mediaTitle;

    @ColumnInfo(name = "media_date_added")
    public Date mediaDateAdded;

    @ColumnInfo(name = "media_favourite")
    public boolean mediaFavourite;
}
