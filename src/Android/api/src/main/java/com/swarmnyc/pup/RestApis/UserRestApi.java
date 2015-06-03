package com.swarmnyc.pup.RestApis;

import com.swarmnyc.pup.viewmodels.UserRequestResult;

import retrofit.Callback;
import retrofit.http.*;
import retrofit.mime.TypedFile;

import java.util.Date;

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

	@Multipart
	@POST( "/Register" )
	void register(
		@Part( "email" )
		String email,
		@Part( "password" )
		String password,
		@Part( "username" )
		String username,
		@Part( "portrait" )
		TypedFile portrait,
		RestApiCallback<UserRequestResult> callback
	);

	@Multipart
	@POST( "/UpdatePortrait" )
	void updatePortrait(
		@Part( "portrait" )
		TypedFile portrait,
		RestApiCallback<String> callback
	);

	@FormUrlEncoded
	@POST( "/User/Medium" )
	void addMedium(
		@Field( "type" )
		String type,
		@Field( "userId" )
		String userId,
		@Field( "token" )
		String token,
		@Field( "ExpireAtUtc" )
		String expireAt,
		RestApiCallback callback
	);

	@FormUrlEncoded
	@DELETE( "/User/Medium" )
	void deleteMedium(
		@Field( "type" )
		String type,
		RestApiCallback callback
	);
}
