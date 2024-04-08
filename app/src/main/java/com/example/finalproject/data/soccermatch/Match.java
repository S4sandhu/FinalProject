package com.example.finalproject.data.soccermatch;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Entity class representing a match.
 */
@Entity
public class Match {

    // Primary key
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    // Date of the match
    @ColumnInfo(name = "date")
    private String date;

    // Name of the first team
    @ColumnInfo(name = "team1")
    private String team1;

    // Name of the second team
    @ColumnInfo(name = "team2")
    private String team2;

    // URL of the match
    @ColumnInfo(name = "url")
    private String url;

    // Title of the match
    @ColumnInfo(name = "title")
    private String title;

    // URL of the match's thumbnail
    @ColumnInfo(name = "thumbnailUrl")
    private String thumbnailUrl;

    /**
     * Default constructor for the Match class.
     */
    public Match() {
    }

    /**
     * Constructor to initialize a Match object from a JSON object.
     * @param matchObject The JSON object containing match information.
     */
    public Match(JSONObject matchObject) {
        try {
            this.title = matchObject.getString("title");
            this.date = matchObject.getString("date");
            JSONObject highlights = matchObject.getJSONArray("videos").getJSONObject(0);
            this.url = extractSrcFromHtml(highlights.getString("embed"));
            this.team1 = matchObject.getJSONObject("side1").getString("name");
            this.team2 = matchObject.getJSONObject("side2").getString("name");
            this.thumbnailUrl = matchObject.getString("thumbnail");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Getter method for retrieving the match ID.
     * @return The match ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Setter method for setting the match ID.
     * @param id The match ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter method for retrieving the date of the match.
     * @return The date of the match.
     */
    public String getDate() {
        return date;
    }

    /**
     * Setter method for setting the date of the match.
     * @param date The date of the match to set.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Getter method for retrieving the name of the first team.
     * @return The name of the first team.
     */
    public String getTeam1() {
        return team1;
    }

    /**
     * Setter method for setting the name of the first team.
     * @param team1 The name of the first team to set.
     */
    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    /**
     * Getter method for retrieving the name of the second team.
     * @return The name of the second team.
     */
    public String getTeam2() {
        return team2;
    }

    /**
     * Setter method for setting the name of the second team.
     * @param team2 The name of the second team to set.
     */
    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    /**
     * Getter method for retrieving the URL of the match.
     * @return The URL of the match.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Setter method for setting the URL of the match.
     * @param url The URL of the match to set.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Getter method for retrieving the title of the match.
     * @return The title of the match.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter method for setting the title of the match.
     * @param title The title of the match to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter method for retrieving the URL of the match's thumbnail.
     * @return The URL of the match's thumbnail.
     */
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    /**
     * Setter method for setting the URL of the match's thumbnail.
     * @param thumbnailUrl The URL of the match's thumbnail to set.
     */
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    /**
     * Method to extract the source URL from HTML content.
     * @param html The HTML content containing an iframe tag.
     * @return The source URL extracted from the HTML content.
     */
    public static String extractSrcFromHtml(String html) {
        // Regex pattern to match the src attribute value of the iframe
        Pattern pattern = Pattern.compile("<iframe\\s+src='(.*?)'");
        Matcher matcher = pattern.matcher(html);

        // Check if src attribute is found and return its value
        if (matcher.find()) {
            return matcher.group(1);
        }

        // Return null if src attribute is not found
        return null;
    }
}
