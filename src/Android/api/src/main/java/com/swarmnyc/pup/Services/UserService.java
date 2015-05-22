package com.swarmnyc.pup.Services;

import com.swarmnyc.pup.viewmodels.UserInfo;

public interface UserService
{
    void login( String email, ServiceCallback<UserInfo> callback );

    void register( String email, String username, ServiceCallback<UserInfo> callback );
}
