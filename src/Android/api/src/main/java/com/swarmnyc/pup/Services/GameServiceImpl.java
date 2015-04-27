package com.swarmnyc.pup.Services;

import com.swarmnyc.pup.RestApis.GameRestApi;
import com.swarmnyc.pup.RestApis.RestApiCallback;
import com.swarmnyc.pup.Services.Filter.GameFilter;
import com.swarmnyc.pup.models.Game;

import java.util.List;

import retrofit.client.Response;

public class GameServiceImpl implements GameService {
    private GameRestApi gameRestApi;

    public GameServiceImpl(GameRestApi gameRestApi) {
        this.gameRestApi = gameRestApi;
    }

    @Override
    public void getGame(String gameId, final ServiceCallback callback) {
        assert gameId != null;
        assert callback != null;

        gameRestApi.get(gameId, new RestApiCallback<Game>() {
            @Override
            public void success(Game game, Response response) {
                callback.success(game);
            }
        });
    }

    @Override
    public void getGames(GameFilter filter, final ServiceCallback<List<Game>> callback) {
        if (filter == null)
            filter = new GameFilter();

        this.gameRestApi.getGames(filter.toMap(), filter.getPlatforms(), new RestApiCallback<List<Game>>() {
            @Override
            public void success(List<Game> games, Response response) {
                callback.success(games);
            }
        });
    }
}
