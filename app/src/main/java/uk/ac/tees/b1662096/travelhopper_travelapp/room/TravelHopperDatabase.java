package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Media.class}, version = 1)
public abstract class TravelHopperDatabase extends RoomDatabase {
    public abstract MediaDAO mediaDAO();
}
