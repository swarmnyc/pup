package com.swarmnyc.pup;


import com.swarmnyc.pup.RestApis.GameRestApi;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.Services.Filter.GameFilter;
import com.swarmnyc.pup.Services.GameService;
import com.swarmnyc.pup.Services.GameServiceImpl;
import com.swarmnyc.pup.models.Game;
import com.swarmnyc.pup.models.GamePlatform;


import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GameServiceTest {

    @Test
    public void filterTest() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);

        GameService service = new GameServiceImpl(TestHelper.getRestApi(GameRestApi.class));
        GameFilter filter = new GameFilter();
        filter.setPageSize(3);
        //filter.addGamePlatform(GamePlatform.PS4);
        //filter.addGamePlatform(GamePlatform.Xbox360);
        service.getGames(filter, new ServiceCallback<List<Game>>() {
            @Override
            public void success(List<Game> value) {
                Assert.assertEquals(3, value.size());
                signal.countDown();
            }
        });

        signal.await(5, TimeUnit.SECONDS);
    }
}
