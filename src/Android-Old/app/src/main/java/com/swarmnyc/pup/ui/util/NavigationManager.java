package com.swarmnyc.pup.ui.util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.model.Lobby;
import com.swarmnyc.pup.ui.fragment.FragmentCreateLobby;
import com.swarmnyc.pup.ui.fragment.FragmentLobbyList;
import com.swarmnyc.pup.ui.fragment.FragmentNavigationDrawer;
import com.swarmnyc.pup.ui.fragment.FragmentViewLobby;
import com.uservoice.uservoicesdk.UserVoice;
import dagger.Module;
import dagger.Provides;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by somya on 11/7/14.
 */
public class NavigationManager
{
	private Activity m_activity;

	public NavigationManager( final Activity activity )
	{
		m_activity = activity;
	}

	public void createLobby()
	{
		m_activity.getFragmentManager()
		          .beginTransaction()
					.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN )
		          .replace( R.id.container, new FragmentCreateLobby() )
		          .addToBackStack( "CREATE" )
		          .commit();
	}

	public void showLobby( final Lobby item )
	{
		final FragmentViewLobby fragment =  FragmentViewLobby.newInstance( item );

		m_activity.getFragmentManager()
		          .beginTransaction()
		          .setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN )
		          .replace( R.id.container, fragment )
		          .addToBackStack( "VIEW" )
		          .commit();
	}

	public void navToHome()
	{
		Log.d( "NavigationManager", "navToHome ([])" );

		m_activity.getFragmentManager()
		          .beginTransaction()
		          .setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN )
		          .replace( R.id.container, new FragmentLobbyList() )
		          .addToBackStack( "VIEW_ALL" )
		          .commit();


	}



	public void navToFeedback()
	{

//		UserVoice.launchUserVoice(this);    // Show the UserVoice portal
//		UserVoice.launchForum(this);        // Show the feedback forum
//		UserVoice.launchContactUs(this);    // Show the contact form
//		UserVoice.launchPostIdea(this);     // Show the idea form
		UserVoice.launchUserVoice( m_activity );
	}

	private void navToFragment( final Fragment fragment, final String name )
	{

		m_activity.getFragmentManager().beginTransaction().replace( R.id.container, fragment )
		          .addToBackStack( name ).commit();
	}
}
