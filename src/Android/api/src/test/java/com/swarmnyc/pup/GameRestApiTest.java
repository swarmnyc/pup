package com.swarmnyc.pup;

import com.google.common.collect.ImmutableMap;
import com.swarmnyc.pup.RestApis.GameRestApi;
import com.swarmnyc.pup.RestApis.LobbyRestApi;
import com.swarmnyc.pup.RestApis.PuPRestApiCallback;
import com.swarmnyc.pup.models.Game;
import com.swarmnyc.pup.models.Lobby;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit.client.Response;

public class GameRestApiTest {
    @Test
    public void getGamesTest() throws Throwable {
        GameRestApi restApi = TestHelper.getService(GameRestApi.class);
        final CountDownLatch signal = new CountDownLatch(1);

        restApi.getList(ImmutableMap.of("PageIndex", "0"), new PuPRestApiCallback<List<Game>>() {
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
        LobbyRestApi restApi = TestHelper.getService(LobbyRestApi.class);
        final CountDownLatch signal = new CountDownLatch(1);

        restApi.get("5527fffe8fdf837834067663", new PuPRestApiCallback<Lobby>() {
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
