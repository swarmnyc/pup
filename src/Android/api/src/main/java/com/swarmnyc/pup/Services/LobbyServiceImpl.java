package com.swarmnyc.pup.Services;

import com.swarmnyc.pup.RestApis.EmptyRestApiCallback;
import com.swarmnyc.pup.RestApis.LobbyRestApi;
import com.swarmnyc.pup.RestApis.RestApiCallback;
import com.swarmnyc.pup.Services.Filter.LobbyFilter;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.models.QBChatMessage2;
import com.swarmnyc.pup.viewmodels.LobbySearchResult;

import retrofit.RetrofitError;
import retrofit.client.Response;

import java.text.SimpleDateFormat;
import java.util.List;

public class LobbyServiceImpl implements LobbyService {
    private LobbyRestApi lobbyRestApi;

    public LobbyServiceImpl(LobbyRestApi lobbyRestApi) {
        this.lobbyRestApi = lobbyRestApi;
    }

    @Override
    public void getLobby(String lobbyId, final ServiceCallback<Lobby> callback) {
        assert lobbyId != null;
        assert callback != null;

        lobbyRestApi.get(lobbyId, new RestApiCallback<>(callback));
    }

    @Override
    public void getLobbies(LobbyFilter filter, final ServiceCallback<LobbySearchResult> callback) {
        if (filter == null) {
            filter = new LobbyFilter();
        }

        this.lobbyRestApi.getLobbies(
                filter.toMap(),
                filter.getPlatforms(),
                filter.getLevels(),
                filter.getStyles(),
                new RestApiCallback<>(callback)
        );
    }

    @Override
    public void getMyLobbies(
            LobbyFilter filter, final ServiceCallback<List<Lobby>> callback
    ) {
        if (filter == null) {
            filter = new LobbyFilter();
        }

        this.lobbyRestApi.getMyLobbies(
                filter.toMap(),
                filter.getPlatforms(),
                filter.getLevels(),
                filter.getStyles(),
                new RestApiCallback<>(callback)
        );
    }

    @Override
    public void getMessages(
            final String id, final ServiceCallback<List<QBChatMessage2>> callback
    ) {
        this.lobbyRestApi.message(id, new RestApiCallback<>(callback));
    }

    @Override
    public void create(
            final Lobby lobby, final ServiceCallback<Lobby> callback
    ) {
        this.lobbyRestApi.create(lobby, new RestApiCallback<>(callback));
    }

    @Override
    public void join(String id, final ServiceCallback<String> callback) {
        this.lobbyRestApi.join(id, new EmptyRestApiCallback(callback));
    }

    @Override
    public void leave(String id, final ServiceCallback<String> callback) {
        this.lobbyRestApi.leave(id, new EmptyRestApiCallback(callback));
    }

    @Override
    public void invite(final Lobby lobby, final List<String> types, final ServiceCallback<String> callback) {
        String localTime = new SimpleDateFormat("MMM dd h:mm a '('zzz')'").format(lobby.getStartTime());

        this.lobbyRestApi.invite(lobby.getId(), localTime, types, new EmptyRestApiCallback(callback));
    }
}
