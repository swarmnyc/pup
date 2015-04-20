package com.swarmnyc.pup.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Game extends Taggable {

    private String name;
    private String pictureUrl;
    private String thumbnailPictureUrl;
    private String description;
    private List<GamePlatform> platforms;
    private List<String> gameTypes;
    private int rank;
    @SerializedName("releaseDateUtc")
    private Date releaseDate;

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

    public String getThumbnailPictureUrl() {
        return thumbnailPictureUrl;
    }

    public void setThumbnailPictureUrl(String thumbnailPictureUrl) {
        this.thumbnailPictureUrl = thumbnailPictureUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<String> getGameTypes() {
        return gameTypes;
    }

    public void setGameTypes(List<String> gameTypes) {
        this.gameTypes = gameTypes;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
