package com.example.finalproject.data.movieinfo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Data Access Object (DAO) interface for interacting with Movie entities in the database.
 */
@Dao
public interface MovieDao {

    /**
     * Retrieves all movies from the database.
     * @return A list of all movies stored in the database.
     */
    @Query("SELECT * FROM movie")
    List<Movie> getAllMovies();

    /**
     * Inserts a new movie into the database.
     * @param movie The movie to insert.
     */
    @Insert
    void insert(Movie movie);

    /**
     * Deletes a movie from the database.
     * @param movie The movie to delete.
     */
    @Delete
    void delete(Movie movie);
}
