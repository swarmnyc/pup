package com.swarmnyc.pup.components;


import android.content.Context;
import android.content.Intent;
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
import com.swarmnyc.pup.activities.LobbyActivity;
import com.swarmnyc.pup.activities.LobbyCreateActivity;
import com.swarmnyc.pup.fragments.CreateLobbyFragment;
import com.swarmnyc.pup.fragments.LobbyFragment;
import com.swarmnyc.pup.fragments.LobbyListFragment;
import com.swarmnyc.pup.models.Lobby;

import java.util.List;

public class Navigator
{
	private static FragmentActivity m_activity;
	private static Tracker          m_tracker;

	public static void init( FragmentActivity activity, Tracker tracker )
	{
		m_activity = activity;
		m_tracker = tracker;

		if ( activity != null )
		{
			// TODO Track pages differently

//			activity.getSupportFragmentManager().addOnBackStackChangedListener(
//				new FragmentManager.OnBackStackChangedListener()
//				{
//					@Override
//					public void onBackStackChanged()
//					{
//						if ( m_activity.getSupportFragmentManager().getBackStackEntryCount() == 0 )
//						{
//							m_activity.finish();
//						}
//						else
//						{
//							List<Fragment> fragments = m_activity.getSupportFragmentManager().getFragments();
//
//							Fragment f = null;
//							int p = fragments.size() - 1;
//							while ( p >= 0 )
//							{
//								if ( Screen.class.isInstance( fragments.get( p ) ) )
//								{
//									f = fragments.get( p );
//									break;
//								}
//
//								p--;
//							}
//
//							if ( f != null )
//							{
//								m_tracker.setScreenName( f.toString() );
//								m_tracker.send( new HitBuilders.ScreenViewBuilder().build() );
//							}
//						}
//					}
//				}
//			);
		}
	}

	public static void ToCreateLobby()
	{
		final Intent intent = new Intent( m_activity, LobbyCreateActivity.class );
		m_activity.startActivity( intent );

//		To( CreateLobbyFragment.class, null, true );
	}

	public static <T extends Fragment> void To(
		Class<T> fragmentClass, Bundle bundle, boolean backable
	)
	{
		try
		{
			FragmentManager fragmentManager = m_activity.getSupportFragmentManager();

			String tag = fragmentClass.getName();
			boolean fragmentPopped = fragmentManager.popBackStackImmediate( tag, 0 );

			if ( !fragmentPopped )
			{
				//fragment not in back stack, create it.
				T fragment = fragmentClass.newInstance();
				fragment.setArguments( bundle );
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//				fragmentTransaction.replace( R.id.fragment_container, fragment, tag );
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

	public static void ToLobby( final String id, final String name, boolean pop )
	{
		Bundle bundle = new Bundle();
		bundle.putString( Consts.KEY_LOBBY_ID, id );
		bundle.putString( Consts.KEY_LOBBY_NAME, name );
//
//		if ( pop )
//		{
//			//pop( CreateLobbyFragment.class );
//			popOnce();
//		}
//
//		To( LobbyFragment.class, bundle, true );

		final Intent intent = new Intent( m_activity, LobbyActivity.class );
		intent.putExtras( bundle );
		m_activity.startActivity( intent );
	}

	public static void ToLobby( final Context context, final Lobby lobby )
	{
		Bundle bundle = new Bundle();
		bundle.putString( Consts.KEY_LOBBY_ID, lobby.getId() );
		bundle.putString( Consts.KEY_LOBBY_NAME, lobby.getName() );
		bundle.putString( Consts.KEY_LOBBY_IMAGE, lobby.getPictureUrl() );

		final Intent intent = new Intent( context, LobbyActivity.class );
		intent.putExtras( bundle );
		context.startActivity( intent );
	}

	private static void popOnce()
	{
		FragmentManager fragmentManager = m_activity.getSupportFragmentManager();
		fragmentManager.popBackStack();
	}

	private static <T> void pop( Class<T> name )
	{
		FragmentManager fragmentManager = m_activity.getSupportFragmentManager();
		fragmentManager.popBackStack( name.getName(), 0 );
	}


}
