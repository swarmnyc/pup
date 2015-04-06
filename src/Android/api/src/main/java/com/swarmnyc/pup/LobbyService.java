package com.swarmnyc.pup;

import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.viewmodels.CreateLobbyResult;
import com.swarmnyc.pup.viewmodels.LobbyFilter;

import java.util.List;


import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

public interface LobbyService {
    @GET("/Lobby")
    void getList(LobbyFilter filter, Callback<List<Lobby>> callback);

    @GET("/Lobby/{LobbyId}")
    void get(@Path("LobbyId")String lobbyId, Callback<Lobby> Lobby);

    @POST("/Lobby")
    void create(Lobby lobby, Callback<CreateLobbyResult> callback);

    @PUT("/Lobby/{LobbyId}")
    void update(@Path("LobbyId") String lobbyId, Lobby lobby, Callback callback);

    @PUT("/Lobby/Join/{LobbyId}")
    void join(@Path("LobbyId") String lobbyId, Callback callback);

    @PUT("/Lobby/Leave/{LobbyId}")
    void leave(@Path("LobbyId") String lobbyId, Callback callback);
}
