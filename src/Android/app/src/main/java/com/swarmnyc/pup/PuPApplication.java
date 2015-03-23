package com.swarmnyc.pup;

import android.app.Application;
import android.content.SharedPreferences;

import com.swarmnyc.pup.components.ChatService;

public class PuPApplication extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();

        Config.init(this);
    }
}
