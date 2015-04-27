package com.swarmnyc.pup.Services;

import com.swarmnyc.pup.RestApis.EmptyRestApiCallback;
import com.swarmnyc.pup.RestApis.LobbyRestApi;
import com.swarmnyc.pup.RestApis.RestApiCallback;
import com.swarmnyc.pup.Services.Filter.LobbyFilter;
import com.swarmnyc.pup.models.Lobby;

import java.util.List;

import retrofit.client.Response;

public class LobbyServiceImpl implements LobbyService {
    private LobbyRestApi lobbyRestApi;

    public LobbyServiceImpl(LobbyRestApi lobbyRestApi) {
        this.lobbyRestApi = lobbyRestApi;
    }

    @Override
    public void getLobby(String lobbyId, final ServiceCallback<Lobby> callback) {
        assert lobbyId != null;
        assert callback != null;

        lobbyRestApi.get(lobbyId, new RestApiCallback<Lobby>() {
            @Override
            public void success(Lobby lobby, Response response) {
                callback.success(lobby);
            }
        });
    }

    @Override
    public void getLobbies(LobbyFilter filter, final ServiceCallback<List<Lobby>> callback) {
        if (filter == null)
            filter = new LobbyFilter();

        this.lobbyRestApi.getLobbies(filter.toMap(),
                filter.getPlatforms(),
                filter.getLevels(),
                filter.getStyles(),
                new RestApiCallback<List<Lobby>>() {
                    @Override
                    public void success(List<Lobby> list, Response response) {
                        callback.success(list);
                    }
                });
    }

    @Override
    public void join(String id, final ServiceCallback callback) {
        this.lobbyRestApi.join(id, new EmptyRestApiCallback() {
            @Override
            public void success(Response response) {
                callback.success(null);
            }
        });
    }

    @Override
    public void leave(String id, final ServiceCallback callback) {
        this.lobbyRestApi.leave(id, new EmptyRestApiCallback() {
            @Override
            public void success(Response response) {
                callback.success(null);
            }
        });
    }
}
