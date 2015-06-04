package com.swarmnyc.pup.components;

import android.content.Intent;
import android.widget.Toast;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.Services.UserService;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.activities.MainActivity;

import java.util.Arrays;

public class FacebookHelper
{
	static CallbackManager callbackManager;

	public static void getAndSubmitToken( final ServiceCallback serviceCallback )
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
						userService.addFacebookToken(
							at.getUserId(), at.getToken(), at.getExpires(), new ServiceCallback()
							{
								@Override
								public void success( final Object value )
								{
									User.addMedia( "Facebook" );
									Toast.makeText( MainActivity.getInstance(), "Connected", Toast.LENGTH_LONG )
									     .show();

									if ( serviceCallback != null )
									{ serviceCallback.success( null ); }
								}
							}
						);
					}

					@Override
					public void onCancel()
					{

					}

					@Override
					public void onError( final FacebookException e )
					{
						Toast.makeText( MainActivity.getInstance(), "Connection Failed", Toast.LENGTH_LONG ).show();
					}
				}
			);
		}

		LoginManager.getInstance().logInWithPublishPermissions(
			MainActivity.getInstance(), Arrays.asList( "publish_actions" )
		);
	}


	public static void checkActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		if ( callbackManager != null )
		{ callbackManager.onActivityResult( requestCode, resultCode, data ); }
	}
}
