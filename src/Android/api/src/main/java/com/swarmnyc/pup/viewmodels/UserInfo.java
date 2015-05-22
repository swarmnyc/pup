package com.swarmnyc.pup.viewmodels;

import com.swarmnyc.pup.models.PuPTag;

import java.util.List;

public class UserInfo
{
    private String       id;
    private String       email;
    private String       userName;
    private String       pictureUrl;
    private String       accessToken;
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

    public List<PuPTag> getTags() {
        return tags;
    }

    public void setTags(List<PuPTag> tags) {
        this.tags = tags;
    }

    public String getAccessToken()
    {
        return accessToken;
    }

    public void setAccessToken( final String accessToken )
    {
        this.accessToken = accessToken;
    }

    public double getExpiresIn()
    {
        return expiresIn;
    }

    public void setExpiresIn( double expiresIn )
    {
        this.expiresIn = expiresIn;
    }
}
