package com.swarmnyc.pup.module.service;

import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.module.models.Lobby;
import com.swarmnyc.pup.module.chat.QBChatMessage;
import com.swarmnyc.pup.module.models.LobbyUserInfo;
import com.swarmnyc.pup.module.models.UserInfo;
import com.swarmnyc.pup.module.restapi.EmptyRestApiCallback;
import com.swarmnyc.pup.module.restapi.LobbyRestApi;
import com.swarmnyc.pup.module.restapi.RestApiCallback;
import com.swarmnyc.pup.module.service.Filter.LobbyFilter;
import com.swarmnyc.pup.module.viewmodels.LobbySearchResult;
import com.swarmnyc.pup.ui.events.LobbyUserChangeEvent;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.paperdb.Paper;
import retrofit.client.Response;

public class LobbyServiceImpl implements LobbyService {

    private LobbyRestApi lobbyRestApi;
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    public LobbyServiceImpl(LobbyRestApi lobbyRestApi) {
        this.lobbyRestApi = lobbyRestApi;
    }

    @Override
    public void getLobby(String lobbyId, ServiceCallback<Lobby> callback) {
        assert lobbyId != null;
        assert callback != null;

        //TODO: Before change to new chat system, use paper for lobby cache.
        Lobby lobby = Paper.get(lobbyId);
        if (lobby == null) {
            lobbyRestApi.get(lobbyId, new RestApiCallback<Lobby>(callback) {
                @Override
                public void success(Lobby lobby, Response response) {
                    super.success(lobby, response);
                    Paper.put(lobby.getId(), lobby);
                }
            });
        } else {
            callback.success(lobby);
        }
    }

    @Override
    public void getLobbies(LobbyFilter filter, ServiceCallback<LobbySearchResult> callback) {
        if (!isRunning.getAndSet(true)) {
            if (filter == null) {
                filter = new LobbyFilter();
            }

            this.lobbyRestApi.getLobbies(
                    filter.toMap(),
                    filter.getPlatforms(),
                    filter.getLevels(),
                    filter.getStyles(),
                    new RestApiCallback<LobbySearchResult>(isRunning, callback) {
                        @Override
                        public void success(LobbySearchResult lobbySearchResult, Response response) {
                            super.success(lobbySearchResult, response);
                            //TODO: Before change to new chat system, use paper for lobby cache.
                            for (Lobby lobby : lobbySearchResult.getResult()) {
                                Paper.put(lobby.getId(), lobby);
                            }
                        }
                    }
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
            final String id, final ServiceCallback<List<QBChatMessage>> callback
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

    @Override
    public void addUser(Lobby lobby, UserInfo u) {
        // Update local db
        LobbyUserInfo user = lobby.getUser(u.getId());
        if (user == null) {
            user = new LobbyUserInfo(u.getId());
            user.setUserName(u.getUserName());
            user.setPortraitUrl(u.getPortraitUrl());
            lobby.getUsers().add(user);
        } else {
            //rejoin
            user.setIsLeave(false);
        }

        Paper.put(lobby.getId(), lobby);

        EventBus.getBus().post(new LobbyUserChangeEvent(lobby.getId()));
    }

    @Override
    public void addUser(String lobbyId, UserInfo u) {
        if (!Paper.exist(lobbyId))
            return;

        Lobby lobby = Paper.get(lobbyId);
        addUser(lobby, u);
    }

    @Override
    public void removeUser(String lobbyId, UserInfo u) {
        if (!Paper.exist(lobbyId))
            return;

        Lobby lobby = Paper.get(lobbyId);
        removeUser(lobby,u);
    }

    @Override
    public void removeUser(Lobby lobby, UserInfo u) {
        LobbyUserInfo user = lobby.getUser(u.getId());
        if (user != null) {
            user.setIsLeave(true);
        }

        Paper.put(lobby.getId(), lobby);
        EventBus.getBus().post(new LobbyUserChangeEvent(lobby.getId()));
    }
}
