package com.swarmnyc.pup.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.Services.UserService;
import com.swarmnyc.pup.User;

import javax.inject.Inject;
import java.util.Arrays;

public class SettingsFragment extends Fragment
{
	@Inject
	UserService m_userService;

	@InjectView( R.id.text_description )
	TextView test;
	CallbackManager callbackManager;


	@OnClick( R.id.btn_logout )
	void logout()
	{
		User.Logout();
	}

	@OnClick( R.id.btn_login_fb )
	void connectFacebook()
	{
		callbackManager = CallbackManager.Factory.create();
		FacebookSdk.sdkInitialize( getActivity().getApplicationContext() );

		LoginManager.getInstance().registerCallback(
			callbackManager, new FacebookCallback<LoginResult>()
			{
				@Override
				public void onSuccess( final LoginResult loginResult )
				{
					AccessToken at = loginResult.getAccessToken();
					m_userService.addFacebookToken(
						at.getUserId(), at.getToken(), at.getExpires(), new ServiceCallback()
						{
							@Override
							public void success( final Object value )
							{
								User.addMedia("Facebook");
								Toast.makeText( getActivity(), "Connected", Toast.LENGTH_LONG ).show();
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

				}
			}
		);

		LoginManager.getInstance().logInWithPublishPermissions(
			this, Arrays.asList(
				"publish_actions"
			)
		);
	}

	@Override
	public String toString()
	{
		return "Settings";
	}

	@Override
	public void onActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		super.onActivityResult( requestCode, resultCode, data );
		callbackManager.onActivityResult( requestCode, resultCode, data );
	}

	@Override
	public View onCreateView(
		LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
	)
	{
		return inflater.inflate( R.layout.fragment_settings, container, false );
	}

	@Override
	public void onViewCreated( View view, Bundle savedInstanceState )
	{
		super.onViewCreated( view, savedInstanceState );
		ButterKnife.inject( this, view );
		PuPApplication.getInstance().getComponent().inject( this );
	}

	@Override
	public void onStart()
	{
		super.onStart();
		MainDrawerFragment.getInstance().highLight( Consts.KEY_SETTINGS );
	}
}
