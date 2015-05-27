package com.swarmnyc.pup.viewmodels;

import com.swarmnyc.pup.models.PuPTag;
import com.swarmnyc.pup.models.Taggable;

import java.util.List;

public class UserInfo extends Taggable
{
    private String       email;
    private String       userName;
    private String       pictureUrl;
    private String       accessToken;
    private double       expiresIn;

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
