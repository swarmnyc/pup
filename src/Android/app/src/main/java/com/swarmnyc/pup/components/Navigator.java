package com.swarmnyc.pup.components;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.fragments.CreateLobbyFragment;
import com.swarmnyc.pup.fragments.LobbyFragment;
import com.swarmnyc.pup.fragments.LobbyListFragment;

public class Navigator
{
	private static FragmentActivity activity;

	public static void init( final FragmentActivity activity )
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
				}
			}
		);
	}

	public static void ToCreateLobby()
	{
		To( CreateLobbyFragment.class, null, true );
	}

	public static void ToLobbyList()
	{
		To( LobbyListFragment.class, null, true );
	}

	public static void ToLobby( final String id ,boolean pop)
	{
		Bundle bundle = new Bundle();
		bundle.putString( LobbyFragment.LOBBY_ID, id );
		if ( pop ){
			popOnce();
		}
		To( LobbyFragment.class, bundle, true );
	}

	private static void popOnce()
	{
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		fragmentManager.popBackStack();
	}

	private static<T> void pop(Class<T> name)
	{
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		fragmentManager.popBackStack( name.getName(), 0 );
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


}
