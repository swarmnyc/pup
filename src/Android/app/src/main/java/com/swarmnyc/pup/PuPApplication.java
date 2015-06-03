package com.swarmnyc.pup;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.support.multidex.MultiDexApplication;
import android.view.Display;

import com.swarmnyc.pup.components.*;
import com.uservoice.uservoicesdk.UserVoice;

public class PuPApplication extends MultiDexApplication
{
    private static PuPApplication instance;
    private PuPComponent component;

    public static PuPApplication getInstance() {
        return instance;
    }

    public PuPComponent getComponent() {
        return component;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Config.init(this);
        User.init();

        this.component = DaggerPuPComponent.builder().build();
    }

    public int getAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
