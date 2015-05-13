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

	private LobbyService m_lobbyService =  new LobbyServiceImpl( TestHelper.getRestApi( LobbyRestApi.class ) );
	GameService service = new GameServiceImpl(TestHelper.getRestApi(GameRestApi.class));

	@Test
	public void filterTest() throws InterruptedException
	{
		final CountDownLatch signal = new CountDownLatch( 1 );

		LobbyFilter filter = new LobbyFilter();
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

		filter.setStartTime( new Date( ) );
		m_lobbyService.getLobbies(
			filter, new ServiceCallback<List<Lobby>>()
			{
				@Override
				public void success( List<Lobby> value )
				{
					Assert.assertTrue( value.size() > 0 );
					signal.countDown();
				}
			}
		);

		signal.await( 5, TimeUnit.SECONDS );
	}

	@Test
	public void createLobbyTest() throws Exception
	{
		TestHelper.ensureLoggedin();
		final CountDownLatch signal = new CountDownLatch( 1 );

		service.getGames(
			new GameFilter(), new ServiceCallback<List<Game>>()
			{
				@Override
				public void success( final List<Game> games )
				{
					Lobby lobby = new Lobby();
					lobby.setGameId( games.get( 0 ).getId());
					lobby.setPlatform( GamePlatform.PS4 );
					lobby.setSkillLevel( SkillLevel.Intermediate );
					lobby.setPlayStyle( PlayStyle.Casual );
					lobby.setDescription( "Random Test" );
					final Calendar instance = Calendar.getInstance();
					instance.add( Calendar.MINUTE, 20 );
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
		);


//		signal.await( 5, TimeUnit.SECONDS );
		signal.await();
	}
}
