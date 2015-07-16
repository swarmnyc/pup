package com.swarmnyc.pup.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;
import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.components.DialogHelper;
import com.swarmnyc.pup.components.SoftKeyboardHelper;

public class LobbyCreateActivity extends AppCompatActivity
{

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_lobby_create );

		PuPApplication.getInstance().sendScreenToTracker( "Create Lobby" );
	}

	@Override
	protected void onResume() {
		super.onResume();
		EventBus.getBus().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EventBus.getBus().unregister(this);
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_lobby_create, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

//		//noinspection SimplifiableIfStatement
//		if ( id == R.id.action_settings )
//		{
//			return true;
//		}

		return super.onOptionsItemSelected( item );
	}

	@Subscribe
	public void runtimeError(final RuntimeException exception) {
		this.runOnUiThread(
				new Runnable() {
					@Override
					public void run() {
						// TODO: Better Message content
						DialogHelper.showError(LobbyCreateActivity.this, exception.getMessage());
					}
				}
		);
	}
}
