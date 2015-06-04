package com.swarmnyc.pup.Services;

import com.swarmnyc.pup.RestApis.EmptyRestApiCallback;
import com.swarmnyc.pup.RestApis.LobbyRestApi;
import com.swarmnyc.pup.RestApis.RestApiCallback;
import com.swarmnyc.pup.Services.Filter.LobbyFilter;
import com.swarmnyc.pup.models.Lobby;
import retrofit.client.Response;

import java.util.List;

public class LobbyServiceImpl implements LobbyService
{
	private LobbyRestApi lobbyRestApi;

	public LobbyServiceImpl( LobbyRestApi lobbyRestApi )
	{
		this.lobbyRestApi = lobbyRestApi;
	}

	@Override
	public void getLobby( String lobbyId, final ServiceCallback<Lobby> callback )
	{
		assert lobbyId != null;
		assert callback != null;

		lobbyRestApi.get(
			lobbyId, new RestApiCallback<Lobby>()
			{
				@Override
				public void success( Lobby lobby, Response response )
				{
					if ( callback != null )
					{ callback.success( lobby ); }
				}
			}
		);
	}

	@Override
	public void getLobbies( LobbyFilter filter, final ServiceCallback<List<Lobby>> callback )
	{
		if ( filter == null ) { filter = new LobbyFilter(); }

		this.lobbyRestApi.getLobbies(
			filter.toMap(),
			filter.getPlatforms(),
			filter.getLevels(),
			filter.getStyles(),
			new RestApiCallback<List<Lobby>>()
			{
				@Override
				public void success( List<Lobby> list, Response response )
				{
					if ( callback != null )
					{ callback.success( list ); }
				}
			}
		);
	}

	@Override
	public void getMyLobbies(
		LobbyFilter filter, final ServiceCallback<List<Lobby>> callback
	)
	{
		if ( filter == null ) { filter = new LobbyFilter(); }

		this.lobbyRestApi.getMyLobbies(
			filter.toMap(),
			filter.getPlatforms(),
			filter.getLevels(),
			filter.getStyles(),
			new RestApiCallback<List<Lobby>>()
			{
				@Override
				public void success( List<Lobby> list, Response response )
				{
					if ( callback != null )
					{ callback.success( list ); }
				}
			}
		);
	}

	@Override
	public void create(
		final Lobby lobby, final ServiceCallback<Lobby> callback
	)
	{
		this.lobbyRestApi.create(
			lobby, new RestApiCallback<Lobby>()
			{
				@Override
				public void success( final Lobby lobby, final Response response )
				{
					if ( callback != null )
					{ callback.success( lobby ); }
				}
			}
		);
	}

	@Override
	public void join( String id, final ServiceCallback callback )
	{
		this.lobbyRestApi.join(
			id, new EmptyRestApiCallback()
			{
				@Override
				public void success( Response response )
				{
					if ( callback != null )
					{ callback.success( null ); }
				}
			}
		);
	}

	@Override
	public void leave( String id, final ServiceCallback callback )
	{
		this.lobbyRestApi.leave(
			id, new EmptyRestApiCallback()
			{
				@Override
				public void success( Response response )
				{
					if ( callback != null )
					{ callback.success( null ); }
				}
			}
		);
	}

	@Override
	public void invite( final String id, final ServiceCallback callback )
	{
		this.lobbyRestApi.invite(
			id, new EmptyRestApiCallback()
			{
				@Override
				public void success( Response response )
				{
					if ( callback != null )
					{ callback.success( null ); }
				}
			}
		);
	}


}
