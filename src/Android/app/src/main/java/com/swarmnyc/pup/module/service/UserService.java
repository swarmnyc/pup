package com.swarmnyc.pup.module.service;

import com.swarmnyc.pup.module.models.CurrentUserInfo;
import com.swarmnyc.pup.module.models.PuPTag;
import com.swarmnyc.pup.module.models.UserDevice;

import java.util.Date;

public interface UserService
{
    void login( String email, ServiceCallback<CurrentUserInfo> callback );

    void register( String email, String username, String file ,String platform ,String cid, ServiceCallback<CurrentUserInfo> callback );

    void updatePortrait(String file , ServiceCallback<String> callback );

    void addMedium(String type, String userId, String token, String secret,Date expiredAt, ServiceCallback<String> callback );

    void deleteMedium(String type, ServiceCallback<String> callback);

    void addTag(PuPTag tag, ServiceCallback<String> callback );

    void deleteTag(String tagId, ServiceCallback<String> callback);

    void addDevice(UserDevice device, ServiceCallback<String> callback );
}
