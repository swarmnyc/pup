package com.swarmnyc.pup.models;

import java.util.Date;
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
    private List<LobbyUserInfo> users;

    public Lobby() {

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

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public List<LobbyUserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<LobbyUserInfo> users) {
        this.users = users;
    }
}
