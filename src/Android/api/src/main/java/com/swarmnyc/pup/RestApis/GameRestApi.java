package com.swarmnyc.pup.RestApis;

import com.swarmnyc.pup.models.Game;
import com.swarmnyc.pup.models.GamePlatform;
import com.swarmnyc.pup.models.Lobby;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
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
