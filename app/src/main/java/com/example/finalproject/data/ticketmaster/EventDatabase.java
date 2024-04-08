package com.example.finalproject.data.ticketmaster;

import androidx.room.Database;
import androidx.room.RoomDatabase;


/**
 * Database class representing the Room database for events.
 */
@Database(entities = {Event.class}, version = 1)
public abstract class EventDatabase extends RoomDatabase {

    /**
     * Retrieves the EventDao object for accessing event data in the database.
     * @return The EventDao object.
     */
    public abstract EventDao eventDao();
}
