package com.swarmnyc.pup;

import com.swarmnyc.pup.RestApis.LobbyRestApi;
import com.swarmnyc.pup.RestApis.RestApiCallback;
import com.swarmnyc.pup.RestApis.EmptyRestApiCallback;
import com.swarmnyc.pup.Services.Filter.LobbyFilter;
import com.swarmnyc.pup.models.GamePlatform;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.models.PlayStyle;
import com.swarmnyc.pup.models.SkillLevel;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit.client.Response;

public class LobbyRestApiTest {
    @Test
    public void getLobbiesTest() throws Throwable {
        LobbyRestApi lobbyRestApi = TestHelper.getRestApi(LobbyRestApi.class);
        final CountDownLatch signal = new CountDownLatch(1);

        LobbyFilter filter=new LobbyFilter();
        lobbyRestApi.getLobbies(filter.toMap(), filter.getPlatforms(), filter.getLevels(), filter.getStyles(), new RestApiCallback<List<Lobby>>() {
            @Override
            public void success(List<Lobby> lobbies, Response response) {
                signal.countDown();
            }
        });

        signal.await(3, TimeUnit.SECONDS);
        Assert.assertEquals(0L, signal.getCount());
    }

    @Test
    public void getLobbyTest() throws Throwable {
        LobbyRestApi lobbyRestApi = TestHelper.getRestApi(LobbyRestApi.class);
        final CountDownLatch signal = new CountDownLatch(1);

        lobbyRestApi.get("5527fffe8fdf837834067663", new RestApiCallback<Lobby>() {
            @Override
            public void success(Lobby lobby, Response response) {
                Assert.assertNotNull(lobby);
                signal.countDown();
            }
        });

        signal.await(3, TimeUnit.SECONDS);
        Assert.assertEquals(0L, signal.getCount());
    }

    @Test
    public void createLobbyTest() throws Throwable {
        LobbyRestApi lobbyRestApi = TestHelper.getRestApi(LobbyRestApi.class);
        final CountDownLatch signal = new CountDownLatch(1);

        Lobby lobby = new Lobby();
        lobby.setStartTime(new Date());
        lobby.setPlayStyle(PlayStyle.Casual);
        lobby.setGameId("Game");
        lobby.setDescription("efg");
        lobby.setName("Title");
        lobby.setPlatform(GamePlatform.PS4);
        lobby.setSkillLevel(SkillLevel.Newbie);
        lobbyRestApi.create(lobby, new RestApiCallback<Lobby>() {
            @Override
            public void success(Lobby lobby, Response response) {
                Assert.assertNotNull(lobby.getId());
                signal.countDown();
            }
        });

        signal.await(5, TimeUnit.SECONDS);
        Assert.assertEquals(0L, signal.getCount());
    }

    @Test
    public void updateLobbyTest() throws Throwable {
        LobbyRestApi lobbyRestApi = TestHelper.getRestApi(LobbyRestApi.class);
        final CountDownLatch signal = new CountDownLatch(1);

        Lobby lobby = new Lobby();
        lobby.setId("5527fffe8fdf837834067663");
        lobby.setStartTime(new Date());
        lobby.setPlayStyle(PlayStyle.Casual);
        lobby.setGameId("Game");
        lobby.setDescription("Update");
        lobby.setName("Update");
        lobby.setPlatform(GamePlatform.PS4);
        lobby.setSkillLevel(SkillLevel.Newbie);
        lobbyRestApi.update(lobby, new EmptyRestApiCallback() {
            @Override
            public void success(Response response) {
                signal.countDown();
            }
        });

        signal.await(3, TimeUnit.SECONDS);
        Assert.assertEquals(0L, signal.getCount());
    }

    @Test
    public void joinLobbyTest() throws Throwable {
        LobbyRestApi lobbyRestApi = TestHelper.getRestApi(LobbyRestApi.class);
        final CountDownLatch signal = new CountDownLatch(1);

        lobbyRestApi.join("552800dc8fdf837834067666", new EmptyRestApiCallback() {
            @Override
            public void success(Response response) {
                signal.countDown();
            }
        });

        signal.await(3, TimeUnit.SECONDS);
        Assert.assertEquals(0L, signal.getCount());
    }

    @Test
    public void leaveLobbyTest() throws Throwable {
        LobbyRestApi lobbyRestApi = TestHelper.getRestApi(LobbyRestApi.class);
        final CountDownLatch signal = new CountDownLatch(1);

        lobbyRestApi.leave("552824e18fdf84754c326f6f", new EmptyRestApiCallback() {
            @Override
            public void success(Response response) {
                signal.countDown();
            }
        });

        signal.await(3, TimeUnit.SECONDS);
        Assert.assertEquals(0L, signal.getCount());
    }
}
