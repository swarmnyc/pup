package com.swarmnyc.pup;


import com.swarmnyc.pup.RestApis.GameRestApi;
import com.swarmnyc.pup.RestApis.LobbyRestApi;
import com.swarmnyc.pup.Services.Filter.GameFilter;
import com.swarmnyc.pup.Services.Filter.LobbyFilter;
import com.swarmnyc.pup.Services.GameService;
import com.swarmnyc.pup.Services.GameServiceImpl;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.LobbyServiceImpl;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.models.Game;
import com.swarmnyc.pup.models.GamePlatform;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.models.PlayStyle;
import com.swarmnyc.pup.models.SkillLevel;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LobbyServiceTest
{

	private LobbyService m_lobbyService = new LobbyServiceImpl( TestHelper.getRestApi( LobbyRestApi.class ) );
	GameService service = new GameServiceImpl( TestHelper.getRestApi( GameRestApi.class ) );

	@Test
	public void filterTest() throws InterruptedException
	{
		final CountDownLatch signal = new CountDownLatch( 1 );

		final LobbyFilter filter = new LobbyFilter();
		filter.setPageIndex( 0 );
		filter.setPageSize( 3 );

		//		filter.setGame(
		//			new Game()
		//			{
		//				@Override
		//				public String getId()
		//				{
		//					return "553bdff9cbf5a82c145f3be7";
		//				}
		//			}
		//		);
		//		filter.addGamePlatform( GamePlatform.Xbox360 );
		//		filter.addPlayStyle( PlayStyle.Casual );
		//		filter.addSkillLevel( SkillLevel.Newbie );
		//		filter.addSkillLevel( SkillLevel.Intermediate );

		filter.setStartTime( new Date() );
		m_lobbyService.getLobbies(
			filter, new ServiceCallback<List<Lobby>>()
			{
				@Override
				public void success( final List<Lobby> page0 )
				{
					Assert.assertTrue( page0.size() > 0 );
					Assert.assertEquals( 3, page0.size() );

					filter.setPageIndex( 1 );
					m_lobbyService.getLobbies(
						filter, new ServiceCallback<List<Lobby>>()
						{
							@Override
							public void success( List<Lobby> page1 )
							{
								Assert.assertTrue( page1.size() > 0 );
								Assert.assertEquals( 3, page1.size() );

								Assert.assertNotEquals( page0.get( 0 ).getId(), page1.get( 0 ).getId() );
								Assert.assertNotEquals( page0.get( 1 ).getId(), page1.get( 1 ).getId() );
								Assert.assertNotEquals( page0.get( 2 ).getId(), page1.get( 2 ).getId() );

								signal.countDown();
							}
						}
					);
				}
			}
		);

		if (! signal.await( 5, TimeUnit.SECONDS ))
		{
			Assert.fail();
		}
	}

	@Test
	public void createLobbyTest() throws Exception
	{
		final int count = 5;
		TestHelper.ensureLoggedin();
		final CountDownLatch signal = new CountDownLatch( count );

		service.getGames(
			new GameFilter(), new ServiceCallback<List<Game>>()
			{
				@Override
				public void success( final List<Game> games )
				{

					for ( int i = 0; i < count; i++ )
					{
						final Game game = games.get( i );

						Lobby lobby = new Lobby();

						lobby.setGameId( game.getId() );
						if (game.getPlatforms().size() > 0)
						{
							lobby.setPlatform(  game.getPlatforms().get( 0 ));
						}
						else
						{
							lobby.setPlatform( GamePlatform.PS4 );
						}
						lobby.setSkillLevel( i%2 ==0 ? SkillLevel.Newbie : SkillLevel.Intermediate );
						lobby.setPlayStyle( i%2 ==0 ? PlayStyle.Casual : PlayStyle.Normal );
						lobby.setDescription( "Random Test: "  + i);
						final Calendar instance = Calendar.getInstance();
						instance.add( Calendar.HOUR, 20 );
						lobby.setStartTime( instance.getTime() );
						m_lobbyService.create(
							lobby, new ServiceCallback<Lobby>()
							{
								@Override
								public void success( final Lobby value )
								{
									signal.countDown();
								}
							}
						);
					}



				}
			}
		);


//		signal.await( 5, TimeUnit.SECONDS );
		signal.await();
	}
}
