package com.swarmnyc.pup.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private String id;
    private String name;
    private String pictureUrl;
    private List<GamePlatform> platforms;
    public Game() {
    }

    public Game(JSONObject json) throws JSONException {
        setId(json.getString("id"));
        setName(json.getString("name"));
        setPictureUrl(json.optString("pictureUrl"));
        platforms = new ArrayList<>();
        JSONArray pj = json.getJSONArray("platforms");
        for (int i =0 ;i<pj.length();i++){
            platforms.add(GamePlatform.get(pj.getInt(i)));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public List<GamePlatform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<GamePlatform> platforms) {
        this.platforms = platforms;
    }
}
