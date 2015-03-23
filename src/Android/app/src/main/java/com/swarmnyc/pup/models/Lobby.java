package com.swarmnyc.pup.models;

import com.swarmnyc.pup.components.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class Lobby extends Game {
    private String gameId;
    private String chatRoomId;
    private String description;
    private Date startTimeUtc;
    private PlayStyle playStyle;
    private SkillLevel skillLevel;
    private List<String> userIds;

    public Lobby() {

    }

    public Lobby(JSONObject json) throws Exception {
        super(json);
        setGameId(json.getString("gameId"));
        setChatRoomId(json.getString("chatRoomId"));
        setDescription(json.optString("description"));

        setPlayStyle(PlayStyle.get(json.getInt("playStyle")));
        setSkillLevel(SkillLevel.get(json.getInt("skillLevel")));
        setStartTimeUtc(Utility.getDateFromJsonString(json.getString("startTimeUtc")));
        //usersId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
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

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    //TODO: Change to better code
    public static Hashtable<String, Lobby> Lobbies = new Hashtable<>();

    public static List<Lobby> FromJsonArray(JSONArray json) throws Exception {
        List<Lobby> list = new ArrayList<>();

        for (int i = 0; i < json.length(); i++) {
            Lobby lobby = new Lobby(json.getJSONObject(i));
            list.add(lobby);
            Lobbies.put(lobby.getId(), lobby);
        }

        return list;
    }


}
