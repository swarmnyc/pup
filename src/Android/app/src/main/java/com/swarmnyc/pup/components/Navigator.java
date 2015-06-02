package com.swarmnyc.pup.components;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.fragments.CreateLobbyFragment;
import com.swarmnyc.pup.fragments.LobbyFragment;
import com.swarmnyc.pup.fragments.LobbyListFragment;

import java.util.List;

public class Navigator
{
	private static FragmentActivity activity;

	public static void init( final FragmentActivity activity, final Tracker tracker )
	{
		Navigator.activity = activity;

		activity.getSupportFragmentManager().addOnBackStackChangedListener(
			new FragmentManager.OnBackStackChangedListener()
			{
				@Override
				public void onBackStackChanged()
				{
					if ( activity.getSupportFragmentManager().getBackStackEntryCount() == 0 )
					{
						activity.finish();
					}
					else
					{
						List<Fragment> fragments = activity.getSupportFragmentManager().getFragments();

						Fragment f = fragments.get( fragments.size() - 1 );
						if ( f != null )
						{
							tracker.setScreenName( f.toString() );
							tracker.send( new HitBuilders.ScreenViewBuilder().build() );
						}
					}
				}
			}
		);
	}

	public static void ToCreateLobby()
	{
		To( CreateLobbyFragment.class, null, true );
	}

	public static <T extends Fragment> void To(
		Class<T> fragmentClass, Bundle bundle, boolean backable
	)
	{
		try
		{
			FragmentManager fragmentManager = activity.getSupportFragmentManager();

			String tag = fragmentClass.getName();
			boolean fragmentPopped = fragmentManager.popBackStackImmediate( tag, 0 );

			if ( !fragmentPopped )
			{
				//fragment not in back stack, create it.
				T fragment = fragmentClass.newInstance();
				fragment.setArguments( bundle );
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace( R.id.fragment_container, fragment, tag );
				if ( backable )
				{
					fragmentTransaction.addToBackStack( tag );
				}

				fragmentTransaction.commit();
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

	public static void ToLobbyList()
	{
		To( LobbyListFragment.class, null, true );
	}

	public static void ToLobby( final String id,final String name, final String from, boolean pop )
	{
		Bundle bundle = new Bundle();
		bundle.putString( Consts.KEY_LOBBY_ID, id );
		bundle.putString( Consts.KEY_LOBBY_NAME, name );
		bundle.putString( Consts.KEY_LOBBY_SOURCE, from );

		if ( pop )
		{
			//pop( CreateLobbyFragment.class );
			popOnce();
		}

		To( LobbyFragment.class, bundle, true );
	}

	private static <T> void pop( Class<T> name )
	{
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		fragmentManager.popBackStack( name.getName(), 0 );
	}

	private static void popOnce()
	{
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		fragmentManager.popBackStack();
	}
}
