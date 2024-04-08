package com.example.finalproject.data.ticketmaster;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import java.util.List;

/**
 * Data Access Object (DAO) interface for accessing event data in the database.
 */
@Dao
public interface EventDao {
    /**
     * Retrieves all events from the database.
     * @return A list of all events.
     */
    @Query("SELECT * FROM Event")
    List<Event> getAllEvents();

    /**
     * Inserts a new event into the database.
     * @param event The event to insert.
     */
    @Insert
    void insert(Event event);

    /**
     * Deletes an event from the database.
     * @param event The event to delete.
     */
    @Delete
    void delete(Event event);
}
