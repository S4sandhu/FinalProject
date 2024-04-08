package com.example.finalproject.data.movieinfo;

import androidx.room.Database;
import androidx.room.RoomDatabase;


/**
 * Database class representing the Room database for movies.
 */
@Database(entities = {Movie.class}, version = 1)
public abstract class MovieDatabase extends RoomDatabase {

    /**
     * Retrieves the MovieDao object for accessing movie data in the database.
     * @return The MovieDao object.
     */
    public abstract MovieDao movieDAO();
}
