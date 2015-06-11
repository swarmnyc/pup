package com.swarmnyc.pup.Services;

import com.swarmnyc.pup.RestApis.RestApiCallback;
import com.swarmnyc.pup.models.CurrentUserInfo;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

import java.util.Date;

public interface UserService
{
    void login( String email, ServiceCallback<CurrentUserInfo> callback );

    void register( String email, String username, String file , ServiceCallback<CurrentUserInfo> callback );

    void updatePortrait(String file , ServiceCallback<String> callback );

    void addFacebookToken(
        String userId,
        String token,
        Date expireAt,
        ServiceCallback callback
    );

    void deleteFacebookToken(
        ServiceCallback callback
    );

    void addTwitterToken( String userId, String token, String secret, ServiceCallback callback );

    void deleteTwitterToken(
        ServiceCallback callback
    );
}
