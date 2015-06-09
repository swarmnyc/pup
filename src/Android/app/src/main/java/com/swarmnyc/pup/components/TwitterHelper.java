package com.swarmnyc.pup.components;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.Services.UserService;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.models.SocialMedium;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

public class TwitterHelper
{
	private static ServiceCallback serviceCallback;
	private static int twitter_requestCode = 140;

	public static void startLoginRequire( final ServiceCallback serviceCallback )
	{
		TwitterHelper.serviceCallback = serviceCallback;

		TwitterAuthClient client = new TwitterAuthClient();

		client.authorize(
			MainActivity.getInstance(), new Callback<TwitterSession>()
			{
				@Override
				public void success( final Result<TwitterSession> result )
				{
					Log.d( "Twitter Connection", "Success: " + result );
				}

				@Override
				public void failure( final TwitterException e )
				{
					Toast.makeText( MainActivity.getInstance(), R.string.message_connect_failure, Toast.LENGTH_LONG )
					     .show();
				}
			}
		);
	}

	public static void handleActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		if ( requestCode == twitter_requestCode && resultCode == Activity.RESULT_OK )
		{
			UserService userService = PuPApplication.getInstance().getComponent().getUserService();
			userService.addTwitterToken(
				String.valueOf( data.getLongExtra( "user_id", 0 ) ), data.getStringExtra( "tk" ), data.getStringExtra(
					"ts"
				), new ServiceCallback()
				{
					@Override
					public void success( final Object value )
					{
						User.addMedium( Consts.KEY_TWITTER );
						Toast.makeText(
							MainActivity.getInstance(), R.string.message_connect_success, Toast.LENGTH_LONG
						).show();

						if ( serviceCallback != null )
						{ serviceCallback.success( null ); }
					}
				}
			);
		}
	}
}
