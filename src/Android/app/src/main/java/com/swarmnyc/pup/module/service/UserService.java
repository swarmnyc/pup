package com.swarmnyc.pup.module.service;

import com.swarmnyc.pup.module.models.CurrentUserInfo;

import java.util.Date;

public interface UserService
{
    void login( String email, ServiceCallback<CurrentUserInfo> callback );

    void register( String email, String username, String file , ServiceCallback<CurrentUserInfo> callback );

    void updatePortrait(String file , ServiceCallback<String> callback );

    void addMedium(String type, String userId, String token, String secret,Date expiredAt, ServiceCallback<String> callback );

    void deleteMedium(String type,
        ServiceCallback<String> callback
    );
}
