package com.example.finalproject.data.pexels;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Data Access Object (DAO) interface for interacting with Photo entities in the database.
 */
@Dao
public interface PhotoDao {

    /**
     * Retrieves all photos from the database.
     * @return A list of all photos stored in the database.
     */
    @Query("SELECT * FROM Photo")
    List<Photo> getAllPhotos();

    /**
     * Inserts a new photo into the database.
     * @param photo The photo to insert.
     */
    @Insert
    void insert(Photo photo);

    /**
     * Deletes a photo from the database.
     * @param photo The photo to delete.
     */
    @Delete
    void delete(Photo photo);
}
