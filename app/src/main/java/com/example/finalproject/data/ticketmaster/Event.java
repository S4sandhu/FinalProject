package com.example.finalproject.data.ticketmaster;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Entity class representing an event.
 */
@Entity
public class Event {

    // Primary key
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    // Name of the event
    private String name;

    // Starting date of the event
    @ColumnInfo(name = "startingDate")
    private String startingDate;

    // Price range of the event
    @ColumnInfo(name = "priceRange")
    private String priceRange;

    // URL of the event
    @ColumnInfo(name = "url")
    private String url;

    // URL of the promotional image for the event
    @ColumnInfo(name = "promoImageUrl")
    private String promoImageUrl;

    /**
     * Default constructor for the Event class.
     */
    public Event() {
    }

    /**
     * Constructor to initialize an Event object from a JSON object.
     * @param eventObject The JSON object containing event information.
     */
    public Event(JSONObject eventObject) {
        try {
            this.name = eventObject.getString("name");
            JSONObject date = eventObject.getJSONObject("dates");
            this.startingDate = date.getJSONObject("start").getString("localDate");
//            JSONArray range = eventObject.getJSONArray("priceRanges");
//            JSONObject rangeFirst = range.getJSONObject(0);
//            double min = rangeFirst.getDouble("min");
//            double max = rangeFirst.getDouble("max");
            this.priceRange = "Min: 10  Max: 2220"; // Placeholder price range
            this.url = eventObject.getString("url");
            this.promoImageUrl = eventObject.getJSONArray("images").getJSONObject(0).getString("url");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // Getter and setter methods for each member variable
    // ...

    /**
     * Getter method for retrieving the event ID.
     * @return The event ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Setter method for setting the event ID.
     * @param id The event ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    // Other getter and setter methods for the remaining member variables

    /**
     * Getter method for retrieving the name of the event.
     * @return The name of the event.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method for setting the name of the event.
     * @param name The name of the event to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter method for retrieving the starting date of the event.
     * @return The starting date of the event.
     */
    public String getStartingDate() {
        return startingDate;
    }

    /**
     * Setter method for setting the starting date of the event.
     * @param startingDate The starting date of the event to set.
     */
    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    /**
     * Getter method for retrieving the price range of the event.
     * @return The price range of the event.
     */
    public String getPriceRange() {
        return priceRange;
    }

    /**
     * Setter method for setting the price range of the event.
     * @param priceRange The price range of the event to set.
     */
    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    /**
     * Getter method for retrieving the URL of the event.
     * @return The URL of the event.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Setter method for setting the URL of the event.
     * @param url The URL of the event to set.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Getter method for retrieving the URL of the promotional image for the event.
     * @return The URL of the promotional image for the event.
     */
    public String getPromoImageUrl() {
        return promoImageUrl;
    }

    /**
     * Setter method for setting the URL of the promotional image for the event.
     * @param promoImageUrl The URL of the promotional image for the event to set.
     */
    public void setPromoImageUrl(String promoImageUrl) {
        this.promoImageUrl = promoImageUrl;
    }
}
