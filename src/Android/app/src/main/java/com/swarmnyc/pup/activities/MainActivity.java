package com.swarmnyc.pup.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.components.DialogHelper;
import com.swarmnyc.pup.components.Navigator;
import com.swarmnyc.pup.fragments.MainDrawerFragment;
import com.uservoice.uservoicesdk.UserVoice;

public class MainActivity extends AppCompatActivity
{
	private static MainActivity instance;

	@InjectView( R.id.toolbar )
	Toolbar toolbar;

	public MainActivity()
	{
		instance = this;
	}

	public static MainActivity getInstance()
	{
		return instance;
	}

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		ButterKnife.inject( this );
		PuPApplication.getInstance().getComponent().inject( this );
		Navigator.init( this );
		EventBus.getBus().register( this );

		com.uservoice.uservoicesdk.Config config = new com.uservoice.uservoicesdk.Config( "swarmnyc.uservoice.com" );
		config.setForumId( 272754 );
		UserVoice.init( config, this );

		Display display = getWindowManager().getDefaultDisplay();
		Point windowSize = new Point();
		display.getSize( windowSize );
		Consts.windowWidth = windowSize.x;
		Consts.windowHeight = windowSize.y;

		if ( !Config.getBool( "ShowedSplash" ) )
		{
			Config.setBool( "ShowedSplash", true );
			startActivity( new Intent( this, SplashActivity.class ) );
		}
	}

	public void retrieveMessage( final String message )
	{

	}

	public void hideToolbar()
	{
		toolbar.setVisibility( View.GONE );
	}

	public void showToolbar()
	{
		toolbar.setVisibility( View.VISIBLE );
	}

	public Toolbar getToolbar()
	{
		return toolbar;
	}

	@Subscribe
	public void runtimeError( final RuntimeException exception )
	{
		this.runOnUiThread(
			new Runnable()
			{
				@Override
				public void run()
				{
					// TODO: Better Message content
					DialogHelper.showError( exception.getMessage() );
				}
			}
		);
	}

	public void hideIme()
	{
		InputMethodManager imm = (InputMethodManager) getSystemService(
			Context.INPUT_METHOD_SERVICE
		);

		if ( getCurrentFocus() == null )
		{ return; }
		if ( getCurrentFocus().getWindowToken() == null )
		{ return; }
		imm.hideSoftInputFromWindow( getCurrentFocus().getWindowToken(), 0 );
	}

	@Override
	public void onBackPressed()
	{
		if ( MainDrawerFragment.getInstance().isDrawOpens() )
		{
			MainDrawerFragment.getInstance().closeDrawers();
		}
		else
		{
			super.onBackPressed();
		}
	}
}
