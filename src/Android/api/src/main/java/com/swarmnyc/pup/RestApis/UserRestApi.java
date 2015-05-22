package com.swarmnyc.pup.RestApis;

import com.swarmnyc.pup.viewmodels.UserRequestResult;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface UserRestApi
{

	@FormUrlEncoded
	@POST( "/Login" )
	void login(
		@Field( "email" )
		String email,
		@Field( "password" )
		String password, RestApiCallback<UserRequestResult> callback
	);

	@FormUrlEncoded
	@POST( "/ExternalLogin" )
	void externalLogin(
		@Field( "provider" )
		String provider,
		@Field( "email" )
		String email,
		@Field( "token" )
		String token, RestApiCallback<UserRequestResult> callback
	);

	@FormUrlEncoded
	@POST( "/Register" )
	void register(
		@Field( "email" )
		String email,
		@Field( "password" )
		String password,
		@Field( "username" )
		String username, RestApiCallback<UserRequestResult> callback
	);
}
