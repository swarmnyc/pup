package com.swarmnyc.pup.RestApis;

import com.swarmnyc.pup.models.SocialMedium;
import com.swarmnyc.pup.viewmodels.UserRequestResult;

import retrofit.http.*;
import retrofit.mime.TypedFile;

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
	@POST( "/User/UpdatePortrait" )
	void updatePortrait(
		@Part( "portrait" )
		TypedFile portrait,
		RestApiCallback<String> callback
	);

	@POST( "/User/Medium" )
	void addMedium(
		@Body
		SocialMedium token,
		RestApiCallback callback
	);

	@DELETE( "/User/Medium/{Type}" )
	void deleteMedium(
		@Path( "Type" )
		String type,
		RestApiCallback callback
	);
}
