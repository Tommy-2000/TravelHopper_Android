package uk.ac.tees.b1662096.travelhopper_travelapp.room;

import androidx.room.TypeConverter;

import java.util.Date;

public class TripDateConverters {

    @TypeConverter
    public static Date fromTripDateLong(Long tripDateLong) {
        return tripDateLong == null ? null : new Date(tripDateLong);
    }

    @TypeConverter
    public static Long tripDateToLong(Date tripDate) {
        return tripDate == null ? null : tripDate.getTime();
    }

}
