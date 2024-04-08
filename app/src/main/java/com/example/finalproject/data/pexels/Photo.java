package com.example.finalproject.data.pexels;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Entity class representing a photo.
 */
@Entity
public class Photo {

    // Primary key
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    // Width of the photo
    @ColumnInfo(name = "width")
    private int width;

    // Height of the photo
    @ColumnInfo(name = "height")
    private int height;

    // URL of the photo
    @ColumnInfo(name = "url")
    private String url;

    // Photographer of the photo
    @ColumnInfo(name = "photographer")
    private String photographer;

    // URL of the thumbnail of the photo
    @ColumnInfo(name = "thumbnailUrl")
    private String thumbnailUrl;

    // URL of the original image
    @ColumnInfo(name = "imageUrl")
    private String imageUrl;

    /**
     * Default constructor for the Photo class.
     */
    public Photo() {
    }

    /**
     * Constructor to initialize a Photo object from a JSON object.
     * @param photoObject The JSON object containing photo information.
     */
    public Photo(JSONObject photoObject) {
        try {
            this.width = photoObject.getInt("width");
            this.height = photoObject.getInt("height");
            this.url = photoObject.getString("url");
            this.photographer = photoObject.getString("photographer");
            this.thumbnailUrl = photoObject.getJSONObject("src").getString("tiny");
            this.imageUrl = photoObject.getJSONObject("src").getString("original");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Getter method for retrieving the photo ID.
     * @return The photo ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Setter method for setting the photo ID.
     * @param id The photo ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter method for retrieving the width of the photo.
     * @return The width of the photo.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Setter method for setting the width of the photo.
     * @param width The width of the photo to set.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Getter method for retrieving the height of the photo.
     * @return The height of the photo.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Setter method for setting the height of the photo.
     * @param height The height of the photo to set.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Getter method for retrieving the URL of the photo.
     * @return The URL of the photo.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Setter method for setting the URL of the photo.
     * @param url The URL of the photo to set.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Getter method for retrieving the photographer of the photo.
     * @return The photographer of the photo.
     */
    public String getPhotographer() {
        return photographer;
    }

    /**
     * Setter method for setting the photographer of the photo.
     * @param photographer The photographer of the photo to set.
     */
    public void setPhotographer(String photographer) {
        this.photographer = photographer;
    }

    /**
     * Getter method for retrieving the thumbnail URL of the photo.
     * @return The thumbnail URL of the photo.
     */
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    /**
     * Setter method for setting the thumbnail URL of the photo.
     * @param thumbnailUrl The thumbnail URL of the photo to set.
     */
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    /**
     * Getter method for retrieving the URL of the original image.
     * @return The URL of the original image.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Setter method for setting the URL of the original image.
     * @param imageUrl The URL of the original image to set.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}