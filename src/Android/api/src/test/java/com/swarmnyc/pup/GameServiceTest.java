package com.swarmnyc.pup;

import com.google.common.collect.ImmutableMap;
import com.swarmnyc.pup.models.Game;
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

public class GameServiceTest {
    @Test
    public void getGamesTest() throws Throwable {
        GameService lobbyService = TestHelper.getService(GameService.class);
        final CountDownLatch signal = new CountDownLatch(1);

        lobbyService.getList(ImmutableMap.of("PageIndex", "0"), new PuPCallback<List<Game>>() {
            @Override
            public void success(List<Game> games, Response response) {
                signal.countDown();
            }
        });

        signal.await(3, TimeUnit.SECONDS);
        Assert.assertEquals(0L, signal.getCount());
    }

    @Test
    public void getGameTest() throws Throwable {
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
}
