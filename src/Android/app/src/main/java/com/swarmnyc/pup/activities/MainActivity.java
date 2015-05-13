package com.swarmnyc.pup.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.swarmnyc.pup.*;
import com.squareup.otto.Subscribe;
import com.swarmnyc.pup.components.*;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity
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
}
