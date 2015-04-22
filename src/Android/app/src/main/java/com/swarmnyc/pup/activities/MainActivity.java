package com.swarmnyc.pup.activities;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;

import com.swarmnyc.pup.*;
import com.squareup.otto.Subscribe;
import com.swarmnyc.pup.components.*;
import com.swarmnyc.pup.components.Consts;
import com.swarmnyc.pup.events.UserChangedEvent;
import com.swarmnyc.pup.chat.ChatService;
import com.swarmnyc.pup.fragments.MainDrawerFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity {
    private static MainActivity instance;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    public MainActivity() {
        instance = this;
    }

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        ButterKnife.inject( this );
        PuPApplication.getInstance().getComponent().inject(this);
        Navigator.init(this);
        EventBus.getBus().register( this );

    }

    public void retrieveMessage(final String message) {

    }

    public void hideToolbar() {
        toolbar.setVisibility(View.GONE);
    }

    public void showToolbar() {
        toolbar.setVisibility(View.VISIBLE);
    }

    public Toolbar getToolbar()
    {
        return toolbar;
    }
}
