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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class LobbyServiceImpl implements LobbyService {
    private LobbyRestApi lobbyRestApi;
    private AtomicBoolean isRunning = new AtomicBoolean(false);

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
        if (!isRunning.getAndSet(true)) {
            if (filter == null) {
                filter = new LobbyFilter();
            }

            this.lobbyRestApi.getLobbies(
                    filter.toMap(),
                    filter.getPlatforms(),
                    filter.getLevels(),
                    filter.getStyles(),
                    new RestApiCallback<>(isRunning, callback)
            );
        }
    }

    @Override
    public void getMyLobbies(
            LobbyFilter filter, final ServiceCallback<List<Lobby>> callback
    ) {
        if (!isRunning.getAndSet(true)) {
            if (filter == null) {
                filter = new LobbyFilter();
            }

            this.lobbyRestApi.getMyLobbies(
                    filter.toMap(),
                    filter.getPlatforms(),
                    filter.getLevels(),
                    filter.getStyles(),
                    new RestApiCallback<>(isRunning, callback)
            );
        }
    }

    @Override
    public void getMessages(
            final String id, final ServiceCallback<List<QBChatMessage2>> callback
    ) {
        if (!isRunning.getAndSet(true)) {
            this.lobbyRestApi.message(id, new RestApiCallback<>(isRunning, callback));
        }
    }

    @Override
    public void create(
            final Lobby lobby, final ServiceCallback<Lobby> callback
    ) {
        if (!isRunning.getAndSet(true)) {
            this.lobbyRestApi.create(lobby, new RestApiCallback<>(isRunning, callback));
        }
    }

    @Override
    public void join(String id, final ServiceCallback<String> callback) {
        if (!isRunning.getAndSet(true)) {
            this.lobbyRestApi.join(id, new EmptyRestApiCallback(isRunning, callback));
        }
    }

    @Override
    public void leave(String id, final ServiceCallback<String> callback) {
        if (!isRunning.getAndSet(true)) {
            this.lobbyRestApi.leave(id, new EmptyRestApiCallback(isRunning, callback));
        }
    }

    @Override
    public void invite(final Lobby lobby, final List<String> types, final ServiceCallback<String> callback) {
        if (!isRunning.getAndSet(true)) {
            String localTime = new SimpleDateFormat("MMM dd h:mm a '('zzz')'").format(lobby.getStartTime());

            this.lobbyRestApi.invite(lobby.getId(), localTime, types, new EmptyRestApiCallback(isRunning, callback));
        }

    }
}
