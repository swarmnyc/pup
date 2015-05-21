package com.swarmnyc.pup.models;

import java.util.List;

public class LoggedInUser
{
    private String       id;
    private String       email;
    private String       userName;
    private String       pictureUrl;
    private String       accessToken;
    private String       tokenType;
    private double       expiresIn;
    private List<PuPTag> tags;

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName( String userName ) {
        this.userName = userName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public double getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(double expiresIn) {
        this.expiresIn = expiresIn;
    }

    public List<PuPTag> getTags() {
        return tags;
    }

    public void setTags(List<PuPTag> tags) {
        this.tags = tags;
    }
}
