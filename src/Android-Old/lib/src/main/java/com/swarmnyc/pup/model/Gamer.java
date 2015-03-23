package com.swarmnyc.pup.model;

import org.parse4j.ParseObject;

/**
 * Created by somya on 10/31/14.
 */
public class Gamer
{
	public  String m_name;
	private String m_id;

	public ParseObject toParseObject()

	{
		ParseObject lobby = new ParseObject( this.getClass().getSimpleName() );
		lobby.put( "name", m_name );

		if ( null != m_id )
		{
			lobby.setObjectId( m_id );

		}

		return lobby;
	}
}
