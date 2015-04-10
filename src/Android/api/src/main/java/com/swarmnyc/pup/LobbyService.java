package com.swarmnyc.pup;

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

public interface LobbyService {
    @GET("/Lobby")
    void getList(@QueryMap Map<String, String> filter, Callback<List<Lobby>> callback);

    @GET("/Lobby/{LobbyId}")
    void get(@Path("LobbyId") String lobbyId, Callback<Lobby> Lobby);

    @POST("/Lobby")
    void create(@Body Lobby lobby, Callback<Lobby> callback);

    @PUT("/Lobby")
    void update(@Body Lobby lobby, PuPEmptyCallback callback);

    @POST("/Lobby/Join/{LobbyId}")
    void join(@Path("LobbyId") String lobbyId, PuPEmptyCallback callback);

    @POST("/Lobby/Leave/{LobbyId}")
    void leave(@Path("LobbyId") String lobbyId, PuPEmptyCallback callback);
}
