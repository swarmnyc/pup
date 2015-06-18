package com.swarmnyc.pup.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.squareup.otto.Subscribe;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.components.DialogHelper;
import com.swarmnyc.pup.components.FacebookHelper;
import com.swarmnyc.pup.components.Navigator;
import com.swarmnyc.pup.fragments.MainDrawerFragment;
import com.uservoice.uservoicesdk.UserVoice;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
	private static MainActivity instance;

	@InjectView( R.id.toolbar )
	Toolbar m_toolbar;
	private GoogleAnalytics m_googleAnalytics;
	private Tracker         m_tracker;
	private boolean         launchDefault;

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
		EventBus.getBus().register( this );
		m_toolbar.setSubtitleTextColor( getResources().getColor( R.color.pup_grey ) );

		Display display = getWindowManager().getDefaultDisplay();
		Point windowSize = new Point();
		display.getSize( windowSize );
		Consts.windowWidth = windowSize.x;
		Consts.windowHeight = windowSize.y;

		//Google
		m_googleAnalytics = GoogleAnalytics.getInstance( this );
		m_googleAnalytics.setLocalDispatchPeriod( 1800 );

		m_tracker = m_googleAnalytics.newTracker( "UA-43683040-6" );
		m_tracker.enableExceptionReporting( false );

		Navigator.init( this, m_tracker );

		if ( !Config.getBool( "ShowedSplash" ) )
		{
			Config.setBool( "ShowedSplash", true );
			startActivity( new Intent( this, SplashActivity.class ) );
		}

		Intent intent = getIntent();
		Uri data = intent.getData();
		launchDefault = true;
		if ( data != null )
		{
			List<String> p = data.getPathSegments();
			if ( p.size() == 2 && p.get( 0 ).equals( "lobby" ) )
			{
				launchDefault = false;
				Navigator.ToLobby( p.get( 1 ), "From Intend", false );
			}
		}

		//User Voice
		com.uservoice.uservoicesdk.Config config = new com.uservoice.uservoicesdk.Config( getString( R.string.uservoice_id ) );
		config.setForumId( 272754 );
		UserVoice.init( config, this );
	}

	public Toolbar getToolbar()
	{
		return m_toolbar;
	}

	public boolean isLaunchDefaultFragment()
	{
		return launchDefault;
	}

	@Override
	protected void onActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		super.onActivityResult( requestCode, resultCode, data );
		FacebookHelper.handleActivityResult( requestCode, resultCode, data );
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

	public void retrieveMessage( final String message )
	{
	}

	public void hideToolbar()
	{
		m_toolbar.setVisibility( View.GONE );
	}

	public void showToolbar()
	{
		m_toolbar.setVisibility( View.VISIBLE );
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

	@Override
	public boolean dispatchTouchEvent( final MotionEvent ev )
	{
		hideIme();
		return super.dispatchTouchEvent( ev );
	}

	public void hideIme()
	{
		if ( getCurrentFocus() == null )
		{ return; }
		if ( getCurrentFocus().getWindowToken() == null )
		{ return; }

		InputMethodManager imm = (InputMethodManager) getSystemService(
			Context.INPUT_METHOD_SERVICE
		);

		imm.hideSoftInputFromWindow( getCurrentFocus().getWindowToken(), 0 );
	}
}
