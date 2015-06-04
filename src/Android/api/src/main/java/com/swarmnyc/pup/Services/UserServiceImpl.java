package com.swarmnyc.pup.Services;

import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.RestApis.RestApiCallback;
import com.swarmnyc.pup.RestApis.UserRestApi;
import com.swarmnyc.pup.StringUtils;
import com.swarmnyc.pup.models.CurrentUserInfo;
import com.swarmnyc.pup.viewmodels.UserRequestResult;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import java.io.File;
import java.util.Date;

public class UserServiceImpl implements UserService
{

	public static final String PASSWORD = "swarmnyc";
	private UserRestApi m_userApi;

	public UserServiceImpl( UserRestApi userApi )
	{
		m_userApi = userApi;
	}

	@Override
	public void login(
		final String email, final ServiceCallback<CurrentUserInfo> callback
	)
	{
		m_userApi.login(
			email, PASSWORD, new RestApiCallback<UserRequestResult>()
			{
				@Override
				public void success( final UserRequestResult userRequestResult, final Response response )
				{
					if ( userRequestResult.isSuccess() )
					{
						if ( callback != null )
							callback.success( userRequestResult.getUser() );
					}
					else
					{
						EventBus.getBus().post( new RuntimeException( userRequestResult.getErrorMessage() ) );
					}
				}
			}
		);
	}

	@Override
	public void register(
		final String email, final String username, String file, final ServiceCallback<CurrentUserInfo> callback
	)
	{
		TypedFile tf = null;
		if ( StringUtils.isNotEmpty( file ) )
		{ tf = new TypedFile( "multipart/form-data", new File( file ) ); }

		m_userApi.register(
			email, PASSWORD, username, tf, new RestApiCallback<UserRequestResult>()
			{
				@Override
				public void success( final UserRequestResult userRequestResult, final Response response )
				{
					if ( userRequestResult.isSuccess() )
					{
						if ( callback != null )
							callback.success( userRequestResult.getUser() );
					}
					else
					{
						EventBus.getBus().post( new RuntimeException( userRequestResult.getErrorMessage() ) );
					}
				}
			}
		);
	}

	@Override
	public void updatePortrait( final String file, final ServiceCallback<String> callback )
	{
		TypedFile tf = null;
		if ( StringUtils.isNotEmpty( file ) )
		{ tf = new TypedFile( "multipart/form-data", new File( file ) ); }

		m_userApi.updatePortrait(
			tf, new RestApiCallback<String>()
			{
				@Override
				public void success( final String s, final Response response )
				{
					if ( callback != null )
						callback.success( s );
				}
			}
		);
	}

	@Override
	public void addFacebookToken(
		final String userId, final String token, final Date expireAt, final ServiceCallback callback
	)
	{
		addMedium("Facebook", userId, token, expireAt,callback);
	}

	@Override
	public void deleteFacebookToken( final ServiceCallback callback )
	{
		deleteMedium( "Facebook", callback );
	}

	private void addMedium(
		final String type, final String userId, final String token, final Date expireAt, final ServiceCallback callback
	)
	{
		m_userApi.addMedium(
			type, userId, token,StringUtils.toDateString( expireAt ) , new RestApiCallback() {
				@Override
				public void success( final Object o, final Response response )
				{
					if ( callback != null )
						callback.success( o );
				}
			}
		);
	}


	private void deleteMedium( final String type, final ServiceCallback callback )
	{
		m_userApi.deleteMedium(
			type, new RestApiCallback()
			{
				@Override
				public void success( final Object o, final Response response )
				{
					if ( callback != null )
						callback.success( o );
				}
			}
		);
	}
}
