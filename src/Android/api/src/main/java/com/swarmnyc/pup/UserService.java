package com.swarmnyc.pup;

import com.swarmnyc.pup.models.LoggedInUser;
import com.swarmnyc.pup.viewmodels.UserLoginResult;
import com.swarmnyc.pup.viewmodels.UserRegisterResult;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface UserService {

    @FormUrlEncoded
    @POST("/Login")
    void login(@Field("email") String email, @Field("password") String password, PuPCallback<LoggedInUser> callback);

    @FormUrlEncoded
    @POST("/ExternalLogin")
    void externalLogin(@Field("provider") String provider, @Field("email") String email, @Field("token") String token, PuPCallback<LoggedInUser> callback);

    @FormUrlEncoded
    @POST("/User/Register")
    void register(@Field("email") String email, @Field("password") String password, Callback<UserRegisterResult> callback);
}
