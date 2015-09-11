package com.swarmnyc.pup.module.restapi;

import com.swarmnyc.pup.module.models.PuPTag;
import com.swarmnyc.pup.module.models.SocialMedium;
import com.swarmnyc.pup.module.models.UserDevice;
import com.swarmnyc.pup.module.viewmodels.UserRequestResult;

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
		@Part( "platform" )
		String platform ,
		@Part( "deviceToken" )
		String token,
		RestApiCallback<UserRequestResult> callback
	);

	@Multipart
	@POST( "/User/UpdatePortrait" )
	void updatePortrait(
		@Part( "portrait" )
		TypedFile portrait,
		RestApiCallback<String> callback
	);

	@POST( "/User/SocialMedia" )
	void addMedium(
		@Body
		SocialMedium token,
		RestApiCallback callback
	);

	@DELETE( "/User/SocialMedia/{Type}" )
	void deleteMedium(
		@Path( "Type" )
		String type,
		RestApiCallback callback
	);

	@POST( "/User/UserTag" )
	void addTag(
			@Body
			PuPTag tag,
			RestApiCallback callback
	);

	@DELETE( "/User/UserTag/{tagId}" )
	void deleteTag(
			@Path( "tagId" )
			String tagId,
			RestApiCallback callback
	);

	@POST( "/User/Device" )
	void addDevice(
			@Body
			UserDevice device,
			RestApiCallback callback
	);
}

