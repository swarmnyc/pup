package com.swarmnyc.pup.components;

import android.content.Intent;
import android.widget.Toast;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.Services.UserService;
import com.swarmnyc.pup.activities.MainActivity;
import com.twitter.sdk.android.core.*;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import io.fabric.sdk.android.Fabric;

public class TwitterHelper
{
	private static TwitterAuthClient client;

	public static void startLoginRequire( final AsyncCallback callback )
	{

		//Twitter and exception report
		TwitterAuthConfig twConfig = new TwitterAuthConfig(
			MainActivity.getInstance().getString( R.string.twitter_key ), MainActivity.getInstance().getString( R.string.twitter_key_secret )
		);

		Fabric.with( MainActivity.getInstance(), new TwitterCore( twConfig ) );

		client = new TwitterAuthClient();

		client.authorize(
			MainActivity.getInstance(), new Callback<TwitterSession>()
			{
				@Override
				public void success( final Result<TwitterSession> result )
				{
					UserService userService = PuPApplication.getInstance().getComponent().getUserService();
					userService.addTwitterToken(
						String.valueOf( result.data.getUserId() ), result.data.getAuthToken().token, result.data.getAuthToken().secret, new ServiceCallback()
						{
							@Override
							public void success( final Object value )
							{
								User.addMedium( Consts.KEY_TWITTER );
								Toast.makeText(
									MainActivity.getInstance(), R.string.message_connect_success, Toast.LENGTH_LONG
								).show();

								if ( callback != null )
								{ callback.success(); }
							}
						}
					);
				}

				@Override
				public void failure( final TwitterException e )
				{
					Toast.makeText( MainActivity.getInstance(), R.string.message_connect_failure, Toast.LENGTH_LONG ).show();
					if ( callback != null )
					{ callback.failure(); }
				}
			}
		);
	}

	public static void handleActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		if ( client != null )
		{
			client.onActivityResult( requestCode, resultCode, data );
		}
	}
}
