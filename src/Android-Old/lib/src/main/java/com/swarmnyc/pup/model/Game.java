package com.swarmnyc.pup.model;

import org.parse4j.ParseObject;

/**
 * Created by somya on 10/31/14.
 */
public class Game
{
	public String       m_id;
	public String       m_title;
	public String       m_shortTitle;
	public String       m_description;
	public GamingSystem m_gamingSystem;
	public String       m_imageUrl;


	public ParseObject toParseObject()
	{
		ParseObject game = new ParseObject( this.getClass().getSimpleName() );
		game.put( "title", m_title );
		game.put( "shortTitle", m_shortTitle );
		game.put( "description", m_description );
		game.put( "gamingSystem", m_gamingSystem.toString() );
		game.put( "imageUrl", m_imageUrl );

		if ( null != m_id )
		{
			game.setObjectId( m_id );

		}

		return game;
	}

}
