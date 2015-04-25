package com.swarmnyc.pup.Services.Filter;

import com.swarmnyc.pup.models.Game;
import com.swarmnyc.pup.models.GamePlatform;

import java.util.HashSet;
import java.util.Map;

public class LobbyFilter extends GameFilter {

	private Game m_game;

	public Game getGame()
	{
		return m_game;
	}

	public void setGame( final Game game )
	{
		m_game = game;
	}
}
