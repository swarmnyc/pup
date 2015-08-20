package com.swarmnyc.pup.Services;

import com.swarmnyc.pup.RestApis.GameRestApi;
import com.swarmnyc.pup.RestApis.RestApiCallback;
import com.swarmnyc.pup.Services.Filter.GameFilter;
import com.swarmnyc.pup.models.Game;

import retrofit.client.Response;

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
                    gameId, new RestApiCallback<Game>(isRunning, callback) {
                        @Override
                        public void success(Game game, Response response) {
                            callback.success(game);
                        }
                    }
            );
        }
    }

    @Override
    public void getGames(GameFilter filter, final ServiceCallback<List<Game>> callback) {
        if (!isRunning.getAndSet(true)) {
            if (filter == null) {
                filter = new GameFilter();
            }

            this.gameRestApi.getGames(
                    filter.toMap(), filter.getPlatforms(), new RestApiCallback<List<Game>>(isRunning, callback) {
                        @Override
                        public void success(List<Game> games, Response response) {
                            if (callback != null) {
                                callback.success(games);
                            }
                        }
                    }
            );
        }
    }
}
