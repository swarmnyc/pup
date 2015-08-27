package com.swarmnyc.pup.module.service;

import com.swarmnyc.pup.module.service.Filter.GameFilter;
import com.swarmnyc.pup.module.models.Game;

import java.util.List;

public interface GameService {
    void getGame(String gameId, ServiceCallback<Game> callback);

    void getGames(GameFilter filter, ServiceCallback<List<Game>> callback);
}
