package com.swarmnyc.pup.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.components.Navigator;
import com.swarmnyc.pup.events.UserChangedEvent;
import com.uservoice.uservoicesdk.UserVoice;

import java.util.ArrayList;
import java.util.List;

public class MainDrawerFragment extends Fragment
{
	int currentSelection = -1;

	@InjectView( R.id.drawer_container )
	View drawerMenuContainer;

	@InjectView( R.id.drawer_list )
	ListView drawerListView;

	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout          drawerLayout;

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View view = inflater.inflate( R.layout.fragment_drawer_main, container, false );
		ButterKnife.inject( this, view );

		EventBus.getBus().register( this );

		return view;
	}

	@Override
	public void onActivityCreated( Bundle savedInstanceState )
	{
		super.onActivityCreated( savedInstanceState );

		drawerLayout = (DrawerLayout) this.getActivity().findViewById( R.id.drawer_layout );
		drawerLayout.setDrawerShadow( R.drawable.drawer_shadow, GravityCompat.START );

		Toolbar toolbar = (Toolbar) this.getActivity().findViewById( R.id.toolbar );

		( (AppCompatActivity) this.getActivity() ).setSupportActionBar( toolbar );

		if ( android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
		{
			toolbar.setElevation( 2 );
		}

		mDrawerToggle = new ActionBarDrawerToggle(
			this.getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string
			.navigation_drawer_close
		);

		drawerLayout.post(
			new Runnable()
			{
				@Override
				public void run()
				{
					mDrawerToggle.syncState();
				}
			}
		);

		drawerLayout.setDrawerListener( mDrawerToggle );

		drawerListView.setOnItemClickListener(
			new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick( AdapterView<?> parent, View view, int position, long id )
				{
					selectItem( position );
				}
			}
		);

		drawerMenuContainer.getLayoutParams().width = (int) ( Consts.windowWidth * 0.90 );

		initializeDrawer( true );
	}

	public void selectItem( int position )
	{
		if ( position != currentSelection )
		{
			currentSelection = position;
			Class fragment = null;

			if ( User.isLoggedIn() )
			{
				switch ( position )
				{
					case 0:
						fragment = MyChatsFragment.class;
						break;
					case 1:
						fragment = LobbyListFragment.class;
						break;
					case 2:
						UserVoice.launchUserVoice( this.getActivity() );
						break;
					case 3:
						fragment = SettingsFragment.class;
						break;
				}
			}
			else
			{
				switch ( position )
				{
					case 0:
						fragment = LobbyListFragment.class;
						break;
					case 1:
						UserVoice.launchUserVoice( this.getActivity() );
						break;
					case 2:
						fragment = SettingsFragment.class;
						break;
				}
			}

			if ( fragment != null )
			{
				Navigator.To( fragment, null, true );
			}
		}

		drawerLayout.closeDrawers();
	}

	private void initializeDrawer( boolean change )
	{
		List<String> list = new ArrayList<>();
		// TODO:Move to resource, or better implement
		// TODO:Back button support;
		if ( User.isLoggedIn() )
		{
			list.add( "My Chats" );
		}

		list.add( "Find a Game" );
		list.add( "Feedback" );
		list.add( "Settings" );

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
			this.getActivity(), R.layout.item_drawer_menu, R.id.text_name, list
		);
		drawerListView.setAdapter( adapter );

		if ( change )
		{
			int position = User.isLoggedIn() ? 1 : 0;
			selectItem( position );
			drawerListView.setItemChecked( position, true );
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		EventBus.getBus().unregister( this );
	}

	@Subscribe
	public void postUserChanged( UserChangedEvent event )
	{
		initializeDrawer( event.isGoHome() );
	}

}
