package com.swarmnyc.pup.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class Lobby extends Taggable {

    private String id;
    private String name;
    private String pictureUrl;
    private GamePlatform platform;
    private String gameId;
    private String description;
    private Date startTimeUtc;
    private PlayStyle playStyle;
    private SkillLevel skillLevel;
    private Hashtable<String, LobbyUserInfo> users;

    public Lobby() {

    }

//    public Lobby(JSONObject json) throws Exception {
//        super(json);
//        id = json.getString("id");
//        name = json.getString("name");
//        pictureUrl = json.optString("pictureUrl");
//
//        gameId = json.getString("gameId");
//        description = json.optString("description");
//
//        playStyle = PlayStyle.get(json.getInt("playStyle"));
//        skillLevel = SkillLevel.get(json.getInt("skillLevel"));
//        startTimeUtc = Utility.getDateFromJsonString(json.getString("startTimeUtc"));
//        platform = GamePlatform.get(json.getInt("platform"));
//
//        users = LobbyUserInfo.FromJsonArray(json.optJSONArray("users"));
//    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartTime() {
        return startTimeUtc;
    }

    public void setStartTimeUtc(Date startTimeUtc) {
        this.startTimeUtc = startTimeUtc;
    }

    public PlayStyle getPlayStyle() {
        return playStyle;
    }

    public void setPlayStyle(PlayStyle playStyle) {
        this.playStyle = playStyle;
    }

    public SkillLevel getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(SkillLevel skillLevel) {
        this.skillLevel = skillLevel;
    }

    public GamePlatform getPlatform() {
        return platform;
    }

    public void setPlatform(GamePlatform platform) {
        this.platform = platform;
    }

    public Hashtable<String, LobbyUserInfo> getUsers() {
        return users;
    }

    /*public static List<Lobby> FromJsonArray(JSONArray json) throws Exception {
        List<Lobby> list = new ArrayList<>();

        for (int i = 0; i < json.length(); i++) {
            Lobby lobby = new Lobby(json.getJSONObject(i));
            list.add(lobby);
        }

        return list;
    }*/


}
