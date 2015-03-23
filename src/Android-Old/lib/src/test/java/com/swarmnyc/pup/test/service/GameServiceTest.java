package com.swarmnyc.pup.test.service;

import com.swarmnyc.pup.model.GamerStyle;
import com.swarmnyc.pup.service.GameService;
import org.junit.Before;
import org.junit.Test;
import org.parse4j.Parse;
import org.parse4j.ParseException;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;
import org.parse4j.callback.SaveCallback;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class GameServiceTest
{

	private GameService m_gameService;

	@Before
	public void setup()
	{
		Parse.initialize( "hq5lLutpqWvuESKQPPaFf26TcvlbZLxYx1gzq39Q", "fl0O8Ap2p7b8NRPJf8YDsLRsB1iDfNjphrYVBLle" );
		m_gameService = new GameService();
	}

	@Test
	public void testCreateGame() throws Exception
	{
		final CountDownLatch latch = new CountDownLatch( 1 );

		m_gameService.createGame(
			TestFactory.createTestGame(), new SaveCallback()
			{
				@Override public void done( final ParseException e )
				{
					System.out.println( "e = " + e );
					latch.countDown();
				}
			}
		);



		latch.await();
	}


	@Test
	public void testCreateGamer() throws Exception
	{
		final CountDownLatch latch = new CountDownLatch( 1 );


		m_gameService.createGamer(
			TestFactory.createTestGamer(), new SaveCallback()
			{
				@Override public void done( final ParseException e )
				{
					System.out.println( "e = " + e );
					latch.countDown();
				}
			}
		);


		latch.await();
	}

	@Test
	public void testCreateLobby() throws Exception
	{
		final CountDownLatch latch = new CountDownLatch( 1 );


		m_gameService.createLobby(
			TestFactory.createTestLobby(), new SaveCallback()
			{
				@Override public void done( final ParseException e )
				{
					System.out.println( "e = " + e );
					latch.countDown();
				}
			}
		);


		latch.await();
	}



	@Test
	public void testSearch() throws Exception
	{
		ParseQuery<ParseObject> query = ParseQuery.getQuery( "Game" );
		final List<ParseObject> parseObjects = query.find();

	}
}