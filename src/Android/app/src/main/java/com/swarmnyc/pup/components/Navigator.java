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
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.activities.LobbyActivity;
import com.swarmnyc.pup.activities.LobbyCreateActivity;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.fragments.CreateLobbyFragment;
import com.swarmnyc.pup.fragments.LobbyFragment;
import com.swarmnyc.pup.fragments.LobbyListFragment;
import com.swarmnyc.pup.models.Lobby;

import java.util.List;

public class Navigator
{
	public static void ToCreateLobby( final Context context )
	{
		final Intent intent = new Intent( context, LobbyCreateActivity.class );
		context.startActivity( intent );
	}

	public static void ToMain( final Context context)
	{
		final Intent intent = new Intent( context, MainActivity.class );
		context.startActivity( intent );
	}

	public static void ToLobby( final Context context, final String id, final String name )
	{
		Bundle bundle = new Bundle();
		bundle.putString( Consts.KEY_LOBBY_ID, id );
		bundle.putString( Consts.KEY_LOBBY_NAME, name );

		final Intent intent = new Intent( context, LobbyActivity.class );
		intent.putExtras( bundle );
		context.startActivity( intent );
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
}
