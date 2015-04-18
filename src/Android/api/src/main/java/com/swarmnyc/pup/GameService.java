package com.swarmnyc.pup;

import com.swarmnyc.pup.models.Game;
import com.swarmnyc.pup.models.Lobby;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.QueryMap;

public interface GameService {
    @GET("/Game")
    void getList(@QueryMap Map<String, String> filter, Callback<List<Game>> callback);

    @GET("/Game/{GameId}")
    void get(@Path("GameId") String gameId, Callback<Game> Lobby);
}
