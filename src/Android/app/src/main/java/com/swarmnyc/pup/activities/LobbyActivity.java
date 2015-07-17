package com.swarmnyc.pup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.helpers.DialogHelper;
import com.swarmnyc.pup.helpers.FacebookHelper;
import com.swarmnyc.pup.events.AfterLeaveLobbyEvent;
import com.swarmnyc.pup.fragments.LobbyFragment;
import com.swarmnyc.pup.fragments.MemberFragment;
import com.swarmnyc.pup.models.Lobby;

import javax.inject.Inject;

public class LobbyActivity extends AppCompatActivity
{
	public static final String TAG_LOBBY     = "LOBBY";
	public static final String LOBBY_MEMBERS = "LOBBY_MEMBERS";
	@Inject LobbyService m_lobbyService;

	@InjectView( R.id.drawer_layout ) DrawerLayout m_drawerLayout;

	@InjectView( R.id.toolbar )  Toolbar   m_toolbar;
	@InjectView( R.id.backdrop ) ImageView m_backdropImage;
	private                      Lobby     m_lobby;
	private                      String    m_lobbyId;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{

		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_lobby );
		ButterKnife.inject( this );

		PuPApplication.getInstance().getComponent().inject( this );

		setSupportActionBar( m_toolbar );
		getSupportActionBar().setDisplayHomeAsUpEnabled( true );

		m_lobbyId = getIntent().getStringExtra( Consts.KEY_LOBBY_ID );

		m_drawerLayout.setDrawerLockMode( DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END );

		// once go into lobby, fresh my chat when go back.
		Config.setBool( Consts.KEY_NEED_UPDATE_MY, true );
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		m_lobbyService.getLobby(
				m_lobbyId, new ServiceCallback<Lobby>() {
					@Override
					public void success(final Lobby value) {
						m_lobby = value;
						init();
					}
				}
		);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		PuPApplication.getInstance().startMessageService();
		EventBus.getBus().register(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		EventBus.getBus().unregister(this);
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_lobby, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if ( id == android.R.id.home){
			finish();
			return true;
		}

		//noinspection SimplifiableIfStatement
		if ( id == R.id.menu_members )
		{
			m_drawerLayout.openDrawer( Gravity.RIGHT );
			return true;
		}

		return super.onOptionsItemSelected( item );
	}

	@Override
	protected void onActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		super.onActivityResult( requestCode, resultCode, data );
		FacebookHelper.handleActivityResult( requestCode, resultCode, data );
	}

	@Subscribe
	public void handleLeaveLobby( AfterLeaveLobbyEvent event )
	{
		m_drawerLayout.closeDrawers();
		init();
	}

	@Subscribe
	public void runtimeError(final RuntimeException exception) {
		this.runOnUiThread(
				new Runnable() {
					@Override
					public void run() {
						// TODO: Better Message content
						DialogHelper.showError(LobbyActivity.this, exception.getMessage());
					}
				}
		);
	}

	private void init()
	{
		final Fragment lobbyFragment = getSupportFragmentManager().findFragmentById( R.id.fragment_lobby );
		if ( null != lobbyFragment && lobbyFragment instanceof LobbyFragment )
		{
			( (LobbyFragment) lobbyFragment ).setLobby( m_lobby );
			PuPApplication.getInstance().sendScreenToTracker( ( (LobbyFragment) lobbyFragment ).getScreenName() );
		}

		final Fragment memberFragment = getSupportFragmentManager().findFragmentById( R.id
			                                                                              .fragment_lobby_members );
		if ( null != memberFragment && memberFragment instanceof MemberFragment )
		{
			( (MemberFragment) memberFragment ).setLobby( m_lobby );
		}
	}

}
