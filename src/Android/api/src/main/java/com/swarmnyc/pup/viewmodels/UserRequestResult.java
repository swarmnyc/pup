package com.swarmnyc.pup.viewmodels;

import com.google.gson.annotations.SerializedName;
import com.swarmnyc.pup.models.CurrentUserInfo;

public class UserRequestResult
{
    @SerializedName( "data" )
    private CurrentUserInfo user;
    private boolean         success;
    private String          errorMessage;

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

    public CurrentUserInfo getUser()
    {
        return user;
    }

    public void setUser( final CurrentUserInfo user )
    {
        this.user = user;
    }
}
