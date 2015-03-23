package com.swarmnyc.pup.service;

import com.swarmnyc.pup.model.Game;
import com.swarmnyc.pup.model.Gamer;
import com.swarmnyc.pup.model.Lobby;
import org.parse4j.ParseObject;
import org.parse4j.callback.SaveCallback;

/**
 * Created by somya on 11/9/14.
 */
public class GameService
{



	public void createGame( final Game game, SaveCallback s )
	{
		final ParseObject parseObject = game.toParseObject();
		parseObject.saveInBackground( s );
	}

	public void createLobby( final Lobby lobby, SaveCallback s )
	{
		final ParseObject parseObject = lobby.toParseObject();
		parseObject.saveInBackground( s );
	}


	public void createGamer( final Gamer gamer, SaveCallback s )
	{
		final ParseObject parseObject = gamer.toParseObject();
		parseObject.saveInBackground( s );
	}


	public void search(final String filter)
	{

	}
}
