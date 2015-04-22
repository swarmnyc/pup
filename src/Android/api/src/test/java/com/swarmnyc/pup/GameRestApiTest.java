package com.swarmnyc.pup;

import com.google.common.collect.ImmutableMap;
import com.swarmnyc.pup.RestApis.GameRestApi;
import com.swarmnyc.pup.RestApis.LobbyRestApi;
import com.swarmnyc.pup.RestApis.RestApiCallback;
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
        GameRestApi restApi = TestHelper.getRestApi(GameRestApi.class);
        final CountDownLatch signal = new CountDownLatch(1);

        restApi.getGames(null, null, new RestApiCallback<List<Game>>() {
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
        LobbyRestApi restApi = TestHelper.getRestApi(LobbyRestApi.class);
        final CountDownLatch signal = new CountDownLatch(1);

        restApi.get("5527fffe8fdf837834067663", new RestApiCallback<Lobby>() {
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
