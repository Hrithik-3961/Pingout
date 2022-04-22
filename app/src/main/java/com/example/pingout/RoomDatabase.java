package com.example.pingout;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.TypeConverters;

@Database(entities = {Users.class, UserMessages.class}, version = 1)
@TypeConverters(Converter.class)
public abstract class RoomDatabase extends androidx.room.RoomDatabase {

    public static RoomDatabase instance;
    public abstract RoomDao roomDao();

    public static synchronized RoomDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    RoomDatabase.class, "room_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
