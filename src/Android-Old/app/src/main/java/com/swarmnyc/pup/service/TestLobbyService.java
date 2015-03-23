package com.swarmnyc.pup.service;

import com.swarmnyc.pup.PupApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by somya on 11/1/14.
 */
public class TestLobbyService implements ILobbyService
{
	@Override public List<Lobby> getLobbies()
	{
		List<Lobby> lobbies = new ArrayList<Lobby>();

		for ( int i = 0; i < 10; i++ )
		{
			final Lobby lobby = new Lobby();
			lobbies.add( lobby );

			lobby.m_game = getGame();
			lobby.m_startTime = ( new Date() ).getTime();
			lobby.m_roomSize = 10;
			lobby.m_description = "This is a test game";
			lobby.m_gamerStyle = GamerStyle.Hardcore;
			lobby.m_owner = new Gamer();
		}

		return lobbies;
	}

	private Game getGame()
	{
		final Game game = new Game();
		game.m_title = "Test";
		game.m_gamingSystem = GamingSystem.values()[RandomInt( GamingSystem.values().length )];

		final String[] stringArray = PupApplication.instance().getResources().getStringArray( R.array.image_urls );

		game.m_imageUrl = stringArray[RandomInt( 10 ) % stringArray.length];

		return game;
	}

	public int RandomInt( final int n )
	{
		return ( new Random() ).nextInt( n );
	}
}
