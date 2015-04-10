package com.swarmnyc.pup;

import com.google.common.collect.ImmutableMap;
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

public class LobbyServiceTest {
    @Test
    public void getLobbiesTest() throws Throwable {
        LobbyService lobbyService = TestHelper.getService(LobbyService.class);
        final CountDownLatch signal = new CountDownLatch(1);

        lobbyService.getList(ImmutableMap.of("PageIndex", "0"), new PuPCallback<List<Lobby>>() {
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
        LobbyService lobbyService = TestHelper.getService(LobbyService.class);
        final CountDownLatch signal = new CountDownLatch(1);

        lobbyService.get("5527fffe8fdf837834067663", new PuPCallback<Lobby>() {
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
        LobbyService lobbyService = TestHelper.getService(LobbyService.class);
        final CountDownLatch signal = new CountDownLatch(1);

        Lobby lobby = new Lobby();
        lobby.setStartTime(new Date());
        lobby.setPlayStyle(PlayStyle.Casual);
        lobby.setGameId("Game");
        lobby.setDescription("efg");
        lobby.setName("Title");
        lobby.setPlatform(GamePlatform.PS4);
        lobby.setSkillLevel(SkillLevel.Newbie);
        lobbyService.create(lobby, new PuPCallback<Lobby>() {
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
        LobbyService lobbyService = TestHelper.getService(LobbyService.class);
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
        lobbyService.update(lobby, new PuPEmptyCallback() {
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
        LobbyService lobbyService = TestHelper.getService(LobbyService.class);
        final CountDownLatch signal = new CountDownLatch(1);

        lobbyService.join("552800dc8fdf837834067666", new PuPEmptyCallback() {
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
        LobbyService lobbyService = TestHelper.getService(LobbyService.class);
        final CountDownLatch signal = new CountDownLatch(1);

        lobbyService.leave("552824e18fdf84754c326f6f", new PuPEmptyCallback() {
            @Override
            public void success(Response response) {
                signal.countDown();
            }
        });

        signal.await(3, TimeUnit.SECONDS);
        Assert.assertEquals(0L, signal.getCount());
    }
}
