package com.swarmnyc.pup;

import com.google.common.collect.ImmutableMap;
import com.swarmnyc.pup.models.GamePlatform;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.models.LoggedInUser;
import com.swarmnyc.pup.models.PlayStyle;
import com.swarmnyc.pup.models.SkillLevel;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit.client.Response;

public class UserServiceTest {
    @Test
    public void loginTest() throws Throwable {
        UserService userService = TestHelper.getService(UserService.class);
        final CountDownLatch signal = new CountDownLatch(1);

        userService.login("hello@swarmnyc.com","Abc1234", new PuPCallback<LoggedInUser>() {
            @Override
            public void success(LoggedInUser loggedInUser, Response response) {
                signal.countDown();
                Assert.assertNotNull(loggedInUser.getId());
            }
        });

        signal.await(3, TimeUnit.SECONDS);
        Assert.assertEquals(0L, signal.getCount());
    }
}
