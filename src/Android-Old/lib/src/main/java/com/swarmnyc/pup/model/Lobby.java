package com.swarmnyc.pup.model;

import org.parse4j.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by somya on 10/31/14.
 */
public class Lobby
{

	public  Game        m_game;
	public  long        m_startTime;
	public  int         m_roomSize;
	public  String      m_description;
	public  GamerStyle  m_gamerStyle;
	public  Gamer       m_owner;
	public  List<Gamer> m_gamerList;
	private String      m_id;

	public Lobby()
	{
		m_gamerList = new ArrayList<Gamer>();
	}

	public ParseObject toParseObject()
	{

		ParseObject lobby = new ParseObject( this.getClass().getSimpleName() );
		lobby.put( "game", m_game.toParseObject() );
		lobby.put( "startTime", m_startTime );
		lobby.put( "roomSize", m_roomSize );
		lobby.put( "description", m_description );
		lobby.put( "gamerStyle", m_gamerStyle.toString() );
		lobby.put( "owner", m_owner.toParseObject() );

		List<ParseObject> players = new ArrayList<ParseObject>( m_gamerList.size() );

		for ( Gamer gamer : m_gamerList )
		{
			players.add( gamer.toParseObject() );
		}

		lobby.addAllUnique( "players", players );

		if ( null != m_id )
		{
			lobby.setObjectId( m_id );
		}

		return lobby;
	}
}
