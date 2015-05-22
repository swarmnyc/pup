package com.swarmnyc.pup.Services;

import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.RestApis.RestApiCallback;
import com.swarmnyc.pup.RestApis.UserRestApi;
import com.swarmnyc.pup.viewmodels.UserInfo;
import com.swarmnyc.pup.viewmodels.UserRequestException;
import com.swarmnyc.pup.viewmodels.UserRequestResult;
import retrofit.client.Response;

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
		final String email, final ServiceCallback<UserInfo> callback
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
						callback.success( userRequestResult.getUser() );
					}
					else
					{
						EventBus.getBus().post( new UserRequestException( userRequestResult.getErrorMessage() ) );
					}
				}
			}
		);
	}

	@Override
	public void register(
		final String email, final String username, final ServiceCallback<UserInfo> callback
	)
	{
		m_userApi.register(
			email, PASSWORD, username, new RestApiCallback<UserRequestResult>()
			{
				@Override
				public void success( final UserRequestResult userRequestResult, final Response response )
				{
					if ( userRequestResult.isSuccess() )
					{
						callback.success( userRequestResult.getUser() );
					}
					else
					{
						EventBus.getBus().post( new UserRequestException( userRequestResult.getErrorMessage() ) );
					}
				}
			}
		);
	}
}
