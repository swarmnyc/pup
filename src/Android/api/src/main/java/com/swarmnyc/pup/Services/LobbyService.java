package com.swarmnyc.pup.Services;

import com.swarmnyc.pup.Services.Filter.LobbyFilter;
import com.swarmnyc.pup.models.Lobby;

import java.util.List;

public interface LobbyService
{
	void getLobby( String gameId, ServiceCallback<Lobby> callback );

	void getLobbies( LobbyFilter filter, ServiceCallback<List<Lobby>> callback );

	void getMyLobbies( LobbyFilter filter, ServiceCallback<List<Lobby>> callback );

	void create( Lobby lobby, ServiceCallback<Lobby> callback );

	void join( String id, ServiceCallback callback );

	void leave( String id, ServiceCallback callback );

	void invite( String id, List<String> types, ServiceCallback callback );
}
