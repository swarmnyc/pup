package com.swarmnyc.pup;


import com.swarmnyc.pup.RestApis.GameRestApi;
import com.swarmnyc.pup.RestApis.LobbyRestApi;
import com.swarmnyc.pup.Services.Filter.GameFilter;
import com.swarmnyc.pup.Services.Filter.LobbyFilter;
import com.swarmnyc.pup.Services.GameService;
import com.swarmnyc.pup.Services.GameServiceImpl;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.LobbyServiceImpl;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.models.Game;
import com.swarmnyc.pup.models.GamePlatform;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.models.PlayStyle;
import com.swarmnyc.pup.models.SkillLevel;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LobbyServiceTest {

    @Test
    public void filterTest() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);

        LobbyService service = new LobbyServiceImpl(TestHelper.getRestApi(LobbyRestApi.class));
        LobbyFilter filter = new LobbyFilter();
        filter.setPageSize(3);

        filter.setGame(new Game(){
            @Override
            public String getId() {
                return "553bdff9cbf5a82c145f3be7";
            }
        });
        filter.addGamePlatform(GamePlatform.Xbox360);
        filter.addPlayStyle(PlayStyle.Casual);
        filter.addSkillLevel(SkillLevel.Newbie);
        filter.addSkillLevel(SkillLevel.Intermediate);

        service.getLobbies(filter, new ServiceCallback<List<Lobby>>() {
            @Override
            public void success(List<Lobby> value) {
                Assert.assertEquals(3, value.size());
                signal.countDown();
            }
        });

        signal.await(5, TimeUnit.SECONDS);
    }
}
