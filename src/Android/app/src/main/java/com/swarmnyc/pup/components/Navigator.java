package com.swarmnyc.pup.components;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.fragments.CreateLobbyFragment;
import com.swarmnyc.pup.fragments.LobbyListFragment;

public class Navigator
{
	private static FragmentActivity activity;

	public static void init( FragmentActivity activity )
	{
		Navigator.activity = activity;
	}

	public static void ToCreateLobby()
	{
		To( CreateLobbyFragment.class, null );
	}

	public static void ToLobbyList()
	{
		To( LobbyListFragment.class, null );
	}

	public static <T extends Fragment> void To( Class<T> fragmentClass, @Nullable Bundle bundle )
	{
		try
		{
			FragmentManager fragmentManager = activity.getSupportFragmentManager();

			String tag = fragmentClass.getName();
			boolean fragmentPopped = fragmentManager.popBackStackImmediate( tag, 0 );

			if ( !fragmentPopped )
			{ //fragment not in back stack, create it.
				T fragment = fragmentClass.newInstance();
				fragment.setArguments( bundle );
				fragmentManager.beginTransaction()
				               .replace( R.id.fragment_container, fragment )
				               .addToBackStack( tag )
				               .commit();
			}
		}
		catch ( InstantiationException e )
		{
			Log.e( "Navigator", "Error in To ([fragmentClass, bundle])", e );
		}
		catch ( IllegalAccessException e )
		{
			Log.e( "Navigator", "Error in To ([fragmentClass, bundle])", e );
		}
	}
}
