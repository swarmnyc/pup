package com.swarmnyc.pup.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Lobby extends Taggable implements PicturedModel {
    private String name;
    private String pictureUrl;
    private String thumbnailPictureUrl;
    private GamePlatform platform;
    private String gameId;
    private String description;
    @SerializedName("startTimeUtc")
    private Date startTime;
    private PlayStyle playStyle;
    private SkillLevel skillLevel;
    private List<LobbyUserInfo> users;

    public Lobby() {

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
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
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

    public LobbyUserInfo getUser(String userId) {
        for (LobbyUserInfo user : users) {
            if (user.id.equals(userId))
                return user;
        }

        return null;
    }

    public String getThumbnailPictureUrl() {
        return thumbnailPictureUrl;
    }

    public void setThumbnailPictureUrl(String thumbnailPictureUrl) {
        this.thumbnailPictureUrl = thumbnailPictureUrl;
    }
}
