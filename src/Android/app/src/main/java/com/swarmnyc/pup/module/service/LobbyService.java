package com.swarmnyc.pup.module.service;

import com.swarmnyc.pup.module.service.Filter.LobbyFilter;
import com.swarmnyc.pup.module.models.Lobby;
import com.swarmnyc.pup.module.models.QBChatMessage2;
import com.swarmnyc.pup.module.viewmodels.LobbySearchResult;

import java.util.List;

public interface LobbyService
{
	void getLobby( String gameId, ServiceCallback<Lobby> callback );

	void getLobbies( LobbyFilter filter, ServiceCallback<LobbySearchResult> callback );

	void getMyLobbies( LobbyFilter filter, ServiceCallback<List<Lobby>> callback );

	// TODO: Only and just for Quickblox
	void getMessages( String id, ServiceCallback<List<QBChatMessage2>> callback );

	void create( Lobby lobby, ServiceCallback<Lobby> callback );

	void join( String id, ServiceCallback<String> callback );

	void leave( String id, ServiceCallback<String> callback );

	void invite( Lobby lobby, List<String> types, ServiceCallback<String> callback );
}
