package com.swarmnyc.pup.components;

import android.content.Intent;
import android.widget.Toast;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.Services.UserService;
import com.swarmnyc.pup.activities.MainActivity;

import java.util.Arrays;

public class FacebookHelper
{
	static CallbackManager callbackManager;

	public static void startLoginRequire( final AsyncCallback callback )
	{
		if ( callbackManager == null )
		{
			callbackManager = CallbackManager.Factory.create();
			FacebookSdk.sdkInitialize( MainActivity.getInstance().getApplicationContext() );

			LoginManager.getInstance().registerCallback(
				callbackManager, new FacebookCallback<LoginResult>()
				{
					@Override
					public void onSuccess( final LoginResult loginResult )
					{
						AccessToken at = loginResult.getAccessToken();
						UserService userService = PuPApplication.getInstance().getComponent().getUserService();

						userService.addMedium(
							Consts.KEY_FACEBOOK, at.getUserId(), at.getToken(), null, at.getExpires(), new ServiceCallback()
							{
								@Override
								public void success( final Object value )
								{
									User.addSocialMedium(Consts.KEY_FACEBOOK);
									Toast.makeText( MainActivity.getInstance(), R.string.message_connect_success, Toast.LENGTH_LONG ).show();

									if ( callback != null )
									{ callback.success(); }
								}
							}
						);
					}

					@Override
					public void onCancel()
					{
						callback.failure();
					}

					@Override
					public void onError( final FacebookException e )
					{
						callback.failure();
						Toast.makeText( MainActivity.getInstance(), R.string.message_connect_failure, Toast.LENGTH_LONG ).show();
					}
				}
			);
		}

		LoginManager.getInstance().logInWithPublishPermissions(
			MainActivity.getInstance(), Arrays.asList( "publish_actions" )
		);
	}


	public static void handleActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		if ( callbackManager != null )
		{ callbackManager.onActivityResult( requestCode, resultCode, data ); }
	}
}
