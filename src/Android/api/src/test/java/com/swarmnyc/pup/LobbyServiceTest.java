package com.swarmnyc.pup;

import com.google.common.collect.ImmutableMap;
import com.swarmnyc.pup.models.Lobby;

import org.junit.Test;

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

        signal.await(30, TimeUnit.SECONDS);
    }

    @Test
    public void getLobbyTest() throws Throwable {
        LobbyService lobbyService = TestHelper.getService(LobbyService.class);
        final CountDownLatch signal = new CountDownLatch(1);

        lobbyService.get("551c50db44064a8b3cf06b19", new PuPCallback<Lobby>() {
            @Override
            public void success(Lobby lobbies, Response response) {
                signal.countDown();
            }
        });

        signal.await(30, TimeUnit.SECONDS);
    }
}
