package com.swarmnyc.pup.RestApis;

import com.swarmnyc.pup.models.LoggedInUser;
import com.swarmnyc.pup.viewmodels.UserRegisterResult;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface UserRestApi {

    @FormUrlEncoded
    @POST("/Login")
    void login(@Field("email") String email, @Field("password") String password, RestApiCallback<LoggedInUser> callback);

    @FormUrlEncoded
    @POST("/ExternalLogin")
    void externalLogin(@Field("provider") String provider, @Field("email") String email, @Field("token") String token, RestApiCallback<LoggedInUser> callback);

    @FormUrlEncoded
    @POST("/User/Register")
    void register(@Field("email") String email, @Field("password") String password, Callback<UserRegisterResult> callback);
}
