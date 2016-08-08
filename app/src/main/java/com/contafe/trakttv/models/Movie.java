package com.contafe.trakttv.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Natig on 7/27/16.
 */
public class Movie {
    private String title;
    private String year;
    private String slug;
    private String traktId;
    private String thumbnail;
    private String medium;
    private String full;


    public Movie(JSONObject movieJSON) {
        // getting returned json object as parameter
        try {
            this.title = movieJSON.getString("title");
            this.year = movieJSON.getString("year").equals("null") ? "" : movieJSON.getString("year");
            this.slug = movieJSON.getJSONObject("ids").getString("slug");
            this.traktId = movieJSON.getJSONObject("ids").getString("trakt");
            this.thumbnail = movieJSON.getJSONObject("images").getJSONObject("fanart").getString("thumb");
            this.medium = movieJSON.getJSONObject("images").getJSONObject("fanart").getString("medium");
            this.full = movieJSON.getJSONObject("images").getJSONObject("fanart").getString("full");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // getters
    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getTraktId() {
        return traktId;
    }

    public String getSlug() {
        return slug;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getMedium() {
        return medium;
    }

    public String getFull() {
        return full;
    }
}
