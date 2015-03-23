package com.swarmnyc.pup.test.service;

import com.swarmnyc.pup.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

/**
 * Created by somya on 11/28/14.
 */
public class TestFactory
{
	static final String AB = "0123456789 ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	static final String[] IMAGE_URLS = {
		"http://t2.gstatic.com/images?q=tbn:ANd9GcTIUSsvRC6K4UQPseJPConRr60jsx7KRW5Wd_uqneszM7yoiIt-",
		"http://naetherwatch.net/wp-content/uploads/2013/06/angry.jpg",
		"http://www.ironmanmode.com/wp-content/uploads/free-game-download.jpg",
		"http://www.presidiacreative.com/wp-content/uploads/2009/08/Halo-3-video-game-1031.jpg"
	};

	static Random rnd = new Random();

	static String randomString( int len )
	{
		StringBuilder sb = new StringBuilder( len );
		for ( int i = 0; i < len; i++ )
		{
			sb.append( AB.charAt( rnd.nextInt( AB.length() ) ) );
		}
		return sb.toString();
	}

	static String randomImgaeUrl()
	{
		return IMAGE_URLS[rnd.nextInt( IMAGE_URLS.length )];
	}

	public static Game createTestGame()
	{
		final Game game = new Game();
		game.m_title = randomString( 10 );
		game.m_shortTitle = randomString( 4 );
		game.m_description = randomString( 256 );
		game.m_imageUrl = randomImgaeUrl();
		game.m_gamingSystem = GamingSystem.values()[rnd.nextInt( GamingSystem.values().length )];
		return game;
	}

	public static Gamer createTestGamer()
	{

		final Gamer gamer = new Gamer();
		gamer.m_name = randomString( 8 );
		return gamer;
	}

	public static Lobby createTestLobby()
	{

		final Lobby lobby = new Lobby();

		lobby.m_game =createTestGame();
		lobby.m_startTime = (new Date()).getTime();
		lobby.m_roomSize = rnd.nextInt( 12 );
		lobby.m_description = randomString( 256 );
		lobby.m_gamerStyle = GamerStyle.Casual;
		lobby.m_owner = new Gamer();
		lobby.m_gamerList = Arrays.asList( createTestGamer(), createTestGamer() );;
		return lobby;
	}
}
