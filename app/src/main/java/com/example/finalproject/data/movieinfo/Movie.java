package com.example.finalproject.data.movieinfo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Entity class representing a movie.
 */
@Entity
public class Movie {

    // Primary key
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    // Title of the movie
    @ColumnInfo(name = "title")
    private String title;

    // Year of release
    @ColumnInfo(name = "year")
    private String year;

    // Rating of the movie
    @ColumnInfo(name = "rating")
    private String rating;

    // Runtime of the movie
    @ColumnInfo(name = "runtime")
    private String runtime;

    // Main actors in the movie
    @ColumnInfo(name = "mainActors")
    private String mainActors;

    // Plot summary of the movie
    @ColumnInfo(name = "plot")
    private String plot;

    // URL of the movie poster
    @ColumnInfo(name = "posterUrl")
    private String posterUrl;

    /**
     * Default constructor for the Movie class.
     */
    public Movie() {
    }

    /**
     * Constructor to initialize a Movie object from a JSON object.
     * @param movieObject The JSON object containing movie information.
     */
    public Movie(JSONObject movieObject) {
        try {
            this.title = movieObject.getString("Title");
            this.year = movieObject.getString("Year");
            this.rating = movieObject.getString("imdbRating");
            this.runtime = movieObject.getString("Runtime");
            this.mainActors = movieObject.getString("Actors");
            this.plot = movieObject.getString("Plot");
            this.posterUrl = movieObject.getString("Poster");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Getter method for retrieving the movie ID.
     * @return The movie ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Setter method for setting the movie ID.
     * @param id The movie ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter method for retrieving the movie title.
     * @return The movie title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter method for setting the movie title.
     * @param title The movie title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter method for retrieving the movie year.
     * @return The movie year.
     */
    public String getYear() {
        return year;
    }

    /**
     * Setter method for setting the movie year.
     * @param year The movie year to set.
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Getter method for retrieving the movie rating.
     * @return The movie rating.
     */
    public String getRating() {
        return rating;
    }

    /**
     * Setter method for setting the movie rating.
     * @param rating The movie rating to set.
     */
    public void setRating(String rating) {
        this.rating = rating;
    }

    /**
     * Getter method for retrieving the movie runtime.
     * @return The movie runtime.
     */
    public String getRuntime() {
        return runtime;
    }

    /**
     * Setter method for setting the movie runtime.
     * @param runtime The movie runtime to set.
     */
    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    /**
     * Getter method for retrieving the main actors of the movie.
     * @return The main actors of the movie.
     */
    public String getMainActors() {
        return mainActors;
    }

    /**
     * Setter method for setting the main actors of the movie.
     * @param mainActors The main actors of the movie to set.
     */
    public void setMainActors(String mainActors) {
        this.mainActors = mainActors;
    }

    /**
     * Getter method for retrieving the plot summary of the movie.
     * @return The plot summary of the movie.
     */
    public String getPlot() {
        return plot;
    }

    /**
     * Setter method for setting the plot summary of the movie.
     * @param plot The plot summary of the movie to set.
     */
    public void setPlot(String plot) {
        this.plot = plot;
    }

    /**
     * Getter method for retrieving the URL of the movie poster.
     * @return The URL of the movie poster.
     */
    public String getPosterUrl() {
        return posterUrl;
    }

    /**
     * Setter method for setting the URL of the movie poster.
     * @param posterUrl The URL of the movie poster to set.
     */
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
}