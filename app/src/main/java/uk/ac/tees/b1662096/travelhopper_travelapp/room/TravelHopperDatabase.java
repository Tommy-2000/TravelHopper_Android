package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {TripEntity.class}, version = 1, autoMigrations = {@AutoMigration(from = 1, to = 2)})
@TypeConverters({TripDateConverters.class})
public abstract class TravelHopperDatabase extends RoomDatabase {
    public abstract TravelHopperDAO travelHopperDAO();

    private static volatile TravelHopperDatabase DATABASE_INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static TravelHopperDatabase getDatabase(final Context databaseContext) {
        if (DATABASE_INSTANCE == null) {
            synchronized (TravelHopperDatabase.class) {
                if (DATABASE_INSTANCE == null) {
                    DATABASE_INSTANCE = Room.databaseBuilder(databaseContext.getApplicationContext(), TravelHopperDatabase.class, "travelhopper_database").allowMainThreadQueries().build();
                }
            }
        }
        return DATABASE_INSTANCE;
    }

}
