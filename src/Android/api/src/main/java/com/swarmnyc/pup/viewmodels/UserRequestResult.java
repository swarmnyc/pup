package com.swarmnyc.pup.viewmodels;

import com.google.gson.annotations.SerializedName;

public class UserRequestResult
{
    @SerializedName( "data" )
    private UserInfo user;
    private boolean  success;
    private String   errorMessage;

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage( final String errorMessage )
    {
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess( final boolean success )
    {
        this.success = success;
    }

    public UserInfo getUser()
    {
        return user;
    }

    public void setUser( final UserInfo user )
    {
        this.user = user;
    }
}
