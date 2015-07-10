package com.swarmnyc.pup.activities;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.components.DialogHelper;
import com.swarmnyc.pup.components.FacebookHelper;
import com.swarmnyc.pup.components.Navigator;
import com.swarmnyc.pup.events.UserChangedEvent;
import com.swarmnyc.pup.fragments.BaseFragment;
import com.swarmnyc.pup.fragments.LobbyListFragment;
import com.swarmnyc.pup.fragments.MyChatsFragment;
import com.swarmnyc.pup.fragments.SettingsFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
	@InjectView( R.id.toolbar )   Toolbar      m_toolbar;
	@InjectView( R.id.viewpager ) ViewPager    m_viewPager;
	@InjectView( R.id.tabs )      TabLayout    m_tabLayout;
	@InjectView( R.id.appbar )    AppBarLayout m_appBarLayout;

	@InjectView( R.id.layout_coordinator ) CoordinatorLayout m_coordinatorLayout;
	private                                TabPagerAdapter   m_tabPagerAdapter;
	private                                Boolean           isLoggedIn = null;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		ButterKnife.inject( this );
		PuPApplication.getInstance().getComponent().inject( this );
		m_toolbar.setSubtitleTextColor( getResources().getColor( R.color.pup_grey ) );
		setSupportActionBar( m_toolbar );

		Display display = getWindowManager().getDefaultDisplay();
		Point windowSize = new Point();
		display.getSize( windowSize );
		Consts.windowWidth = windowSize.x;
		Consts.windowHeight = windowSize.y;
		ViewConfiguration vc = ViewConfiguration.get( this );
		Consts.TOUCH_SLOP = vc.getScaledTouchSlop();

		//Show Splash or not
		if ( !Config.getBool( "ShowedSplash" ) )
		{
			Config.setBool( "ShowedSplash", true );
			startActivity( new Intent( this, SplashActivity.class ) );
		}

		//Redirect to Lobby
		Intent intent = getIntent();
		Uri data = intent.getData();

		if ( data != null )
		{
			List<String> p = data.getPathSegments();
			if ( p.size() == 2 && p.get( 0 ).equals( "lobby" ) )
			{
				Navigator.ToLobby( this, p.get( 1 ), "From Intent" );
			}
		}

		showTabsByUser( null );
	}


	@Override
	protected void onResume()
	{
		super.onResume();
		EventBus.getBus().register( this );
		PuPApplication.getInstance().startMessageService();
		showTabsByUser( null );
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		EventBus.getBus().unregister( this );


	}

	@Override
	protected void onStop()
	{
		super.onStop();
	}

	private void setupViewPager( ViewPager viewPager )
	{
		m_tabPagerAdapter = new TabPagerAdapter( getSupportFragmentManager() );
		m_tabPagerAdapter.addFragment( new LobbyListFragment(), "FIND A GAME" );
		if ( User.isLoggedIn() )
		{
			m_tabPagerAdapter.addFragment( new MyChatsFragment(), "MY GAMES" );
			m_tabPagerAdapter.addFragment( new SettingsFragment(), "PROFILE" );
		}
		viewPager.setAdapter( m_tabPagerAdapter );

		m_viewPager.addOnPageChangeListener(
			new ViewPager.OnPageChangeListener()
			{
				@Override
				public void onPageScrolled(
					final int position, final float positionOffset, final int positionOffsetPixels
				)
				{

				}

				@Override
				public void onPageSelected( final int position )
				{
					final Fragment fragment = m_tabPagerAdapter.getItem( position );
					if ( fragment instanceof BaseFragment )
					{
						BaseFragment bf = (BaseFragment) fragment;
						bf.updateTitle();

						PuPApplication.getInstance().sendScreenToTracker( bf.getScreenName() );
					}
				}

				@Override
				public void onPageScrollStateChanged( final int state )
				{

				}
			}
		);
	}

	@OnClick( R.id.fab_create_lobby )
	public void onCreateLobbyButtonClicked()
	{
		Navigator.ToCreateLobby( this );
	}

	@Override
	protected void onActivityResult( final int requestCode, final int resultCode, final Intent data )
	{
		super.onActivityResult( requestCode, resultCode, data );
		FacebookHelper.handleActivityResult( requestCode, resultCode, data );
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
					DialogHelper.showError( MainActivity.this, exception.getMessage() );
				}
			}
		);
	}

	@Subscribe
	public void showTabsByUser( UserChangedEvent event )
	{
		if ( isLoggedIn==null || isLoggedIn != User.isLoggedIn() )
		{
			isLoggedIn = User.isLoggedIn();

			setupViewPager( m_viewPager );

			m_tabLayout.setupWithViewPager( m_viewPager );

			if ( User.isLoggedIn() )
			{
				m_tabLayout.setVisibility( View.VISIBLE );
			}
			else
			{
				m_tabLayout.setVisibility( View.GONE );
			}
		}
	}


	static class TabPagerAdapter extends FragmentPagerAdapter
	{
		private final List<Fragment> mFragments      = new ArrayList<>();
		private final List<String>   mFragmentTitles = new ArrayList<>();

		public TabPagerAdapter( FragmentManager fm )
		{
			super( fm );
		}

		public void addFragment( Fragment fragment, String title )
		{
			mFragments.add( fragment );
			mFragmentTitles.add( title );
		}

		@Override
		public Fragment getItem( int position )
		{
			return mFragments.get( position );
		}

		@Override
		public int getCount()
		{
			return mFragments.size();
		}

		@Override
		public CharSequence getPageTitle( int position )
		{
			return mFragmentTitles.get( position );
		}
	}

}
