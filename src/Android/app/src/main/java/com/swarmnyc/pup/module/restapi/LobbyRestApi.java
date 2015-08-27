package com.swarmnyc.pup.module.restapi;

import com.swarmnyc.pup.module.models.*;

import java.util.List;
import java.util.Map;

import com.swarmnyc.pup.module.viewmodels.LobbySearchResult;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

public interface LobbyRestApi {
    @GET("/Lobby")
    void getLobbies(@QueryMap Map<String, Object> filter,
                    @Query("Platforms") Iterable<GamePlatform> platforms,
                    @Query("SkillLevels") Iterable<SkillLevel> levels,
                    @Query("PlayStyles") Iterable<PlayStyle> styles,
                    Callback<LobbySearchResult> callback);

    @GET("/Lobby/My")
    void getMyLobbies(@QueryMap Map<String, Object> filter,
                    @Query("Platforms") Iterable<GamePlatform> platforms,
                    @Query("SkillLevels") Iterable<SkillLevel> levels,
                    @Query("PlayStyles") Iterable<PlayStyle> styles,
                    Callback<List<Lobby>> callback);

    @GET("/Lobby/{LobbyId}")
    void get(@Path("LobbyId") String lobbyId, Callback<Lobby> Lobby);

    @GET("/Lobby/Message/{LobbyId}")
    void message(@Path("LobbyId") String lobbyId, Callback<List<QBChatMessage2>> Lobby);

    @POST("/Lobby")
    void create(@Body Lobby lobby, Callback<Lobby> callback);

    @PUT("/Lobby")
    void update(@Body Lobby lobby, EmptyRestApiCallback callback);

    @POST("/Lobby/Join/{LobbyId}")
    void join(@Path("LobbyId") String lobbyId, EmptyRestApiCallback callback);

    @POST("/Lobby/Leave/{LobbyId}")
    void leave(@Path("LobbyId") String lobbyId, EmptyRestApiCallback callback);

    @POST("/Lobby/Invite/{LobbyId}")
    void invite(
        @Path( "LobbyId" )
        String lobbyId, @Query("LocalTime")String localTime,  @Query("Types") Iterable<String> types, EmptyRestApiCallback callback
    );
}
