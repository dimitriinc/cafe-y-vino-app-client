package com.cafeyvinowinebar.Cafe_y_Vino;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.ItemCanasta;

@Database(entities = {ItemCanasta.class}, version = 1)
public abstract class CanastaDatabase extends RoomDatabase {

    private static CanastaDatabase instance;
    public abstract CanastaDao canastaDao();
    public static synchronized CanastaDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    CanastaDatabase.class, "canasta_database")
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build();
        }
        return instance;
    }
}
