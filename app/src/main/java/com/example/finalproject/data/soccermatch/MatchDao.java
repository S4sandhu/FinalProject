package com.example.finalproject.data.soccermatch;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import java.util.List;

/**
 * Data Access Object (DAO) interface for interacting with Match entities in the database.
 */
@Dao
public interface MatchDao {

    /**
     * Retrieves all matches from the database.
     * @return A list of all matches stored in the database.
     */
    @Query("SELECT * FROM Match")
    List<Match> getAllMatches();

    /**
     * Inserts a new match into the database.
     * @param match The match to insert.
     */
    @Insert
    void insert(Match match);

    /**
     * Deletes a match from the database.
     * @param match The match to delete.
     */
    @Delete
    void delete(Match match);
}