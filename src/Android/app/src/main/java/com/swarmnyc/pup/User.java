package com.swarmnyc.pup;

import com.google.gson.Gson;
import com.quickblox.core.helper.StringUtils;
import com.swarmnyc.pup.events.UserChangedEvent;
import com.swarmnyc.pup.viewmodels.UserInfo;

public class User
{
	private static final String KEY_USER_EXPIRES = "UserExpires";
	private static final String KEY_USER         = "User";

	public static UserInfo current;
	private static Gson gson = new Gson();

	public static void init()
	{
		Long expires = Config.getLong( KEY_USER_EXPIRES );
		if ( expires > System.currentTimeMillis() )
		{
			String json = Config.getString( KEY_USER );

			if ( !StringUtils.isEmpty( json ) )
			{
				current = gson.fromJson( json, UserInfo.class );
			}
		}
	}

	public static void login( UserInfo userInfo )
	{
		login( userInfo, true );
	}

	public static void login( final UserInfo userInfo, final boolean goHome )
	{
		User.current = userInfo;

		Config.setLong( KEY_USER_EXPIRES, System.currentTimeMillis() + (int) current.getExpiresIn() );
		Config.setString( KEY_USER, gson.toJson( current ) );

		EventBus.getBus().post( new UserChangedEvent( goHome ) );
	}

	public static void Logout()
	{
		User.current = null;

		Config.remove( KEY_USER_EXPIRES );

		EventBus.getBus().post( new UserChangedEvent( true ) );
	}

	public static boolean isLoggedIn()
	{
		return current != null;
	}
}
