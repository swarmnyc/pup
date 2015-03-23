package com.swarmnyc.pup.service;

import com.swarmnyc.pup.model.Lobby;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by somya on 10/31/14.
 */
public class LobbyService implements ILobbyService
{
	@Override public List<Lobby> getLobbies()
	{
		List<Lobby> lobbies = new ArrayList<Lobby>(  );

		for ( int i = 0; i < 10; i++ )
		{
			lobbies.add( new Lobby() );
		}

		return lobbies;
	}

	public void createLobby(Lobby aLobby)
	{

	}

}
