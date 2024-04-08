package com.example.finalproject.data.pexels;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * Database class representing the Room database for photos.
 */
@Database(entities = {Photo.class}, version = 1)
public abstract class PhotoDatabase extends RoomDatabase {

    /**
     * Retrieves the PhotoDao object for accessing photo data in the database.
     * @return The PhotoDao object.
     */
    public abstract PhotoDao photoDAO();
}
