package com.swarmnyc.pup.Services;

import com.swarmnyc.pup.viewmodels.UserInfo;
import retrofit.mime.TypedFile;

public interface UserService
{
    void login( String email, ServiceCallback<UserInfo> callback );

    void register( String email, String username, String file , ServiceCallback<UserInfo> callback );

    void updatePortrait(String file , ServiceCallback<String> callback );
}
