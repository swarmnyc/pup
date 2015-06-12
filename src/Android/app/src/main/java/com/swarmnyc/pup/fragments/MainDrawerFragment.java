package com.swarmnyc.pup.fragments;

import android.content.res.Resources;
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
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.components.Navigator;
import com.swarmnyc.pup.events.UserChangedEvent;
import com.uservoice.uservoicesdk.UserVoice;

public class MainDrawerFragment extends Fragment
{
	private static MainDrawerFragment m_instance;
	@InjectView( R.id.drawer_container )
	View m_drawerMenuContainer;

	@InjectView( R.id.drawer_list )
	ListView m_drawerListView;

	private int m_currentSelection = -1;
	private int m_slideWay         = 0;
	private ActionBarDrawerToggle m_drawerToggle;
	private DrawerLayout          m_drawerLayout;
	private String[]              m_items;
	private String[]              m_itemCodes;
	private String[]              m_itemsForUser;
	private String[]              m_itemCodesForUser;
	private int                   m_defaultItem;
	private int                   m_defaultItemForUser;

	public MainDrawerFragment()
	{
		m_instance = this;
	}

	public static MainDrawerFragment getInstance()
	{
		return m_instance;
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
	{
		View view = inflater.inflate( R.layout.fragment_drawer_main, container, false );
		ButterKnife.inject( this, view );

		EventBus.getBus().register( this );

		Resources r = getResources();
		m_items = r.getStringArray( R.array.menu_items );
		m_itemCodes = r.getStringArray( R.array.menu_item_code );
		m_itemsForUser = r.getStringArray( R.array.menu_items_for_user );
		m_itemCodesForUser = r.getStringArray( R.array.menu_item_code_for_user );
		m_defaultItem = r.getInteger( R.integer.menu_item_default );
		m_defaultItemForUser = r.getInteger( R.integer.menu_item_default_for_user );

		return view;
	}

	@Override
	public void onActivityCreated( Bundle savedInstanceState )
	{
		super.onActivityCreated( savedInstanceState );

		m_drawerLayout = (DrawerLayout) this.getActivity().findViewById( R.id.drawer_layout );
		m_drawerLayout.setDrawerShadow( R.drawable.drawer_shadow, GravityCompat.START );
		m_drawerLayout.setDrawerLockMode( DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END );

		Toolbar toolbar = (Toolbar) this.getActivity().findViewById( R.id.toolbar );

		( (AppCompatActivity) this.getActivity() ).setSupportActionBar( toolbar );

		/*if ( android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
		{
			toolbar.setElevation( 2 );
		}*/

		m_drawerToggle = new ActionBarDrawerToggle(
			this.getActivity(), m_drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
		)
		{
			@Override
			public void onDrawerSlide( final View drawerView, final float slideOffset )
			{
				int way = drawerView.getId() == R.id.main_drawer_left ? 0 : 1;
				if ( way != m_slideWay )
				{
					m_slideWay = way;
					if ( m_slideWay == 0 )
					{
						m_drawerLayout.setDrawerShadow( R.drawable.drawer_shadow, GravityCompat.START );
					}
					else
					{
						m_drawerLayout.setDrawerShadow( R.drawable.drawer_shadow_right, GravityCompat.END );
					}
				}
				super.onDrawerSlide( drawerView, slideOffset );
			}
		};

		m_drawerLayout.post(
			new Runnable()
			{
				@Override
				public void run()
				{
					m_drawerToggle.syncState();
				}
			}
		);

		m_drawerLayout.setDrawerListener( m_drawerToggle );

		m_drawerListView.setOnItemClickListener(
			new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick( AdapterView<?> parent, View view, int position, long id )
				{
					selectItem( position );
				}
			}
		);

		m_drawerMenuContainer.getLayoutParams().width = (int) ( Consts.windowWidth * 0.90 );

		initializeDrawer( MainActivity.getInstance().isLaunchDefaultFragment() );
	}

	public void selectItem( int position )
	{
		if ( position != m_currentSelection )
		{
			m_currentSelection = position;
			Class fragment = null;
			String code;

			if ( User.isLoggedIn() )
			{
				code = m_itemCodesForUser[position];
			}
			else
			{
				code = m_itemCodes[position];
			}

			switch ( code )
			{
				case Consts.KEY_MY_LOBBIES:
					fragment = MyChatsFragment.class;
					break;
				case Consts.KEY_LOBBIES:
					fragment = LobbyListFragment.class;
					break;
				case Consts.KEY_FEEDBACK:
					//UserVoice.launchUserVoice( this.getActivity() );
					UserVoice.launchPostIdea(this.getActivity());
					break;
				case Consts.KEY_SETTINGS:
					fragment = SettingsFragment.class;
					break;
			}

			if ( fragment != null )
			{
				Navigator.To( fragment, null, true );
			}
		}

		m_drawerLayout.closeDrawers();
	}

	private void initializeDrawer( boolean change )
	{
		String[] items;
		if ( User.isLoggedIn() )
		{
			items = m_itemsForUser;
		}
		else
		{
			items = m_items;
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<>(
			this.getActivity(), R.layout.item_drawer_menu, R.id.text_name, items
		);
		m_drawerListView.setAdapter( adapter );

		if ( change )
		{
			int position = User.isLoggedIn() ? m_defaultItemForUser : m_defaultItem;
			selectItem( position );
			m_drawerListView.setItemChecked( position, true );
		}
	}

	@Override
	public void onDestroy()
	{
		EventBus.getBus().unregister( this );
		super.onDestroy();
	}

	public void highLight( String code )
	{
		if ( code == null ){
			m_drawerListView.setItemChecked( m_currentSelection, false );
			m_currentSelection=-1;
		}
		else
		{
			int position = -1;

			if ( User.isLoggedIn() )
			{
				for ( int i = 0; i < m_itemCodesForUser.length; i++ )
				{
					if ( m_itemCodesForUser[i].equals( code ) )
					{ position = i; }
				}
			}
			else
			{
				for ( int i = 0; i < m_itemCodes.length; i++ )
				{
					if ( m_itemCodes[i].equals( code ) )
					{ position = i; }
				}
			}

			m_currentSelection = position;
			m_drawerListView.setItemChecked( position, true );
		}
	}

	@Subscribe
	public void postUserChanged( UserChangedEvent event )
	{
		initializeDrawer( event.isGoHome() );
	}

	public void closeDrawers()
	{
		m_drawerLayout.closeDrawers();
	}

	public boolean isDrawOpens()
	{
		return m_drawerLayout.isDrawerOpen( GravityCompat.START ) || m_drawerLayout.isDrawerOpen( GravityCompat.END );
	}

	public void setRightDrawer( final Fragment fragment )
	{
		getActivity().getSupportFragmentManager().beginTransaction().replace( R.id.main_drawer_right, fragment ).commit();

		m_drawerLayout.setDrawerLockMode( DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END );
	}

	public void removeRightDrawer( final Fragment fragment )
	{
		if ( fragment != null )
		{
			getActivity().getSupportFragmentManager().beginTransaction().remove( fragment ).commitAllowingStateLoss();
		}

		m_drawerLayout.setDrawerLockMode( DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END );
	}

	public void showRightDrawer()
	{
		if ( m_drawerLayout.getDrawerLockMode( GravityCompat.END ) != DrawerLayout.LOCK_MODE_LOCKED_CLOSED )
		{
			m_drawerLayout.openDrawer( GravityCompat.END );
		}
	}
}
