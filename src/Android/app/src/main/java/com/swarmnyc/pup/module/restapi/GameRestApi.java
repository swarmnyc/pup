package com.swarmnyc.pup.module.restapi;

import com.swarmnyc.pup.module.models.Game;
import com.swarmnyc.pup.module.models.GamePlatform;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

public interface GameRestApi {
    @GET("/Game")
    void getGames(@QueryMap Map<String, Object> filter, @Query("Platforms") Iterable<GamePlatform> platforms, Callback<List<Game>> callback);

    @GET("/Game/{GameId}")
    void get(@Path("GameId") String gameId, Callback<Game> callback);

    @GET("/Game/Popular")
    void getPupularGames(Callback<Game> callback);
}
