package com.swarmnyc.pup;

import com.swarmnyc.pup.viewmodels.UserLoginResult;
import com.swarmnyc.pup.viewmodels.UserRegisterResult;

import retrofit.Callback;
import retrofit.http.POST;

public interface UserService {
    @POST("/Login")
    void login(String email, String password, Callback<UserLoginResult> callback);

    @POST("/ExternalLogin")
    void externalLogin(String provider, String email, String password, Callback<UserLoginResult> callback);

    @POST("/User/Register")
    void register(String email, String password, Callback<UserRegisterResult> callback);
}
