package com.swarmnyc.pup.Services;

import com.swarmnyc.pup.Services.Filter.GameFilter;
import com.swarmnyc.pup.models.Game;

import java.util.List;

public interface GameService {
    void getGame(String gameId, ServiceCallback<Game> callback);

    void getGames(GameFilter filter, ServiceCallback<List<Game>> callback);
}
