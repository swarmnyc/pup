package com.swarmnyc.pup.Services;

import com.swarmnyc.pup.Services.Filter.LobbyFilter;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.models.QBChatMessage2;

import java.util.List;

public interface LobbyService
{
	void getLobby( String gameId, ServiceCallback<Lobby> callback );

	void getLobbies( LobbyFilter filter, ServiceCallback<List<Lobby>> callback );

	void getMyLobbies( LobbyFilter filter, ServiceCallback<List<Lobby>> callback );

	// TODO: Only and just for Quickblox
	void getMessages( String id, ServiceCallback<List<QBChatMessage2>> callback );

	void create( Lobby lobby, ServiceCallback<Lobby> callback );

	void join( String id, ServiceCallback callback );

	void leave( String id, ServiceCallback callback );

	void invite( Lobby lobby, List<String> types, ServiceCallback callback );
}
