package com.swarmnyc.pup;

import android.app.Application;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import java.util.Date;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<PuPApplication> {
    public ApplicationTest() {
        super(PuPApplication.class);
    }

    public void testConfig(){
        /*Config.setUserToken(null, new Date());
        Assert.assertNull(Config.getUserToken());

        Config.setUserToken("Test", new Date(System.currentTimeMillis() + 100000));

        Assert.assertNotNull(Config.getUserToken());

        Assert.assertNotNull(Config.getConfigString(R.string.Login_Url));*/
    }
}