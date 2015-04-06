package com.swarmnyc.pup.models;

import java.util.List;

public class Game extends Taggable {
    private String id;
    private String name;
    private String pictureUrl;
    private List<GamePlatform> platforms;

    public Game() {
    }

    /*public Game(JSONObject json) throws JSONException {
        super(json);
        id = json.getString("id");
        name = json.getString("name");
        pictureUrl = json.optString("pictureUrl");
        platforms = GamePlatform.FromJsonArray(json.getJSONArray("platforms"));
    }
*/
    public String getId() {
        return id;
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
