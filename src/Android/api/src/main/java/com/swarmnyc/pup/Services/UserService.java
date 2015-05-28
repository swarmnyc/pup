package com.swarmnyc.pup.Services;

import com.swarmnyc.pup.models.CurrentUserInfo;

public interface UserService
{
    void login( String email, ServiceCallback<CurrentUserInfo> callback );

    void register( String email, String username, String file , ServiceCallback<CurrentUserInfo> callback );

    void updatePortrait(String file , ServiceCallback<String> callback );
}
