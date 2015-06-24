package com.swarmnyc.pup;

import com.google.gson.Gson;
import com.quickblox.core.helper.StringUtils;
import com.swarmnyc.pup.events.UserChangedEvent;
import com.swarmnyc.pup.models.CurrentUserInfo;

public class User
{
	private static final String KEY_USER_EXPIRES = "UserExpires";
	private static final String KEY_USER         = "User";

	public static  CurrentUserInfo current = CurrentUserInfo.Null;
	private static Gson            gson    = new Gson();

	public static void init()
	{
		Long expires = Config.getLong( KEY_USER_EXPIRES );
		if ( expires > System.currentTimeMillis() )
		{
			String json = Config.getString( KEY_USER );

			if ( !StringUtils.isEmpty( json ) )
			{
				current = gson.fromJson( json, CurrentUserInfo.class );
			}
		}
	}

	public static void login( CurrentUserInfo userInfo )
	{
		login( userInfo, true );
	}

	public static void login( final CurrentUserInfo userInfo, final boolean goHome )
	{
		User.current = userInfo;

		Config.setLong( KEY_USER_EXPIRES, System.currentTimeMillis() + (int) current.getExpiresIn() );
		Config.setString( KEY_USER, gson.toJson( current ) );

		EventBus.getBus().post( new UserChangedEvent( goHome ) );
	}

	public static void Logout()
	{
		User.current = CurrentUserInfo.Null;

		Config.remove( KEY_USER_EXPIRES );

		EventBus.getBus().post( new UserChangedEvent( true ) );
	}

	public static boolean isLoggedIn()
	{
		return current != CurrentUserInfo.Null;
	}

	public static void addSocialMedium(final String type) {
		current.getSocialMedia().add( type );
		Config.setString( KEY_USER, gson.toJson( current ) );
	}

	public static void removeSocialMedium(final String type) {
		current.getSocialMedia().remove( type );
		Config.setString( KEY_USER, gson.toJson( current ) );
	}

	public static void update()
	{
		Config.setString( KEY_USER, gson.toJson( current ) );
	}
}
