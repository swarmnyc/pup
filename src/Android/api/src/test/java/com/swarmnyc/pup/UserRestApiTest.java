package com.swarmnyc.pup;

import com.swarmnyc.pup.RestApis.RestApiCallback;
import com.swarmnyc.pup.RestApis.UserRestApi;
import com.swarmnyc.pup.models.LoggedInUser;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit.client.Response;

public class UserRestApiTest {
    @Test
    public void loginTest() throws Throwable {
        UserRestApi userRestApi = TestHelper.getRestApi(UserRestApi.class);
        final CountDownLatch signal = new CountDownLatch(1);

        userRestApi.login("hello@swarmnyc.com","Abc1234", new RestApiCallback<LoggedInUser>() {
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
