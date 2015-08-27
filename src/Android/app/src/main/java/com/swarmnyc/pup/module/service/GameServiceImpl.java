package com.swarmnyc.pup.module.service;

import com.swarmnyc.pup.module.restapi.GameRestApi;
import com.swarmnyc.pup.module.restapi.RestApiCallback;
import com.swarmnyc.pup.module.service.Filter.GameFilter;
import com.swarmnyc.pup.module.models.Game;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameServiceImpl implements GameService {
    private GameRestApi gameRestApi;
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    public GameServiceImpl(GameRestApi gameRestApi) {
        this.gameRestApi = gameRestApi;
    }

    @Override
    public void getGame(String gameId, final ServiceCallback<Game> callback) {
        if (!isRunning.getAndSet(true)) {
            assert gameId != null;
            assert callback != null;

            gameRestApi.get(
                    gameId, new RestApiCallback<>(isRunning, callback));
        }
    }

    @Override
    public void getGames(GameFilter filter, final ServiceCallback<List<Game>> callback) {
        if (!isRunning.getAndSet(true)) {
            if (filter == null) {
                filter = new GameFilter();
            }

            this.gameRestApi.getGames(filter.toMap(), filter.getPlatforms(), new RestApiCallback<>(isRunning, callback));
        }
    }
}
