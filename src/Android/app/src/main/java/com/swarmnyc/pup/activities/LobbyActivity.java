package com.swarmnyc.pup.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.fragments.LobbyFragment;

public class LobbyActivity extends AppCompatActivity
{
	@InjectView( R.id.drawer_layout ) DrawerLayout m_drawerLayout;

	@InjectView( R.id.toolbar ) Toolbar m_toolbar;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{

		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_lobby );
		ButterKnife.inject( this );

		setSupportActionBar( m_toolbar );

		if ( null == savedInstanceState )
		{
			final LobbyFragment fragment = new LobbyFragment();
			fragment.setArguments( getIntent().getExtras() );

			getSupportFragmentManager().beginTransaction().add( R.id.container, fragment, "LOBBY" ).commit();
		}
	}

	public void setRightDrawer( final Fragment fragment )
	{
		getSupportFragmentManager().beginTransaction().replace( R.id.main_drawer_right, fragment ).commit();

		m_drawerLayout.setDrawerLockMode( DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END );
	}
}
