package com.example.finalproject.data.soccermatch;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * Database class representing the Room database for matches.
 */
@Database(entities = {Match.class}, version = 1)
public abstract class MatchDatabase extends RoomDatabase {

    /**
     * Retrieves the MatchDao object for accessing match data in the database.
     * @return The MatchDao object.
     */
    public abstract MatchDao matchDAO();
}
