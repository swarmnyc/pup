package com.swarmnyc.pup.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.swarmnyc.pup.*;
import com.squareup.otto.Subscribe;
import com.swarmnyc.pup.events.UserlogoutEvent;
import com.swarmnyc.pup.chat.ChatService;
import com.swarmnyc.pup.fragments.LobbyListFragment;
import com.swarmnyc.pup.fragments.MyChatsFragment;
import com.swarmnyc.pup.fragments.SettingsFragment;
import com.uservoice.uservoicesdk.UserVoice;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int REQUEST_RESULT_CODE_RELOAD = 735623;
    public static final int REQUEST_CODE_AUTH = 2884;
    private static MainActivity instance;

    @Inject
    ChatService chatService;

    @InjectView(R.id.drawer_list)
    ListView mDrawerListView;

    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.drawer_menu_contrainer)
    View drawerMenuContainer;

    private ActionBarDrawerToggle mDrawerToggle;
    private boolean alreadyInitialize;

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
        PuPApplication.getInstance().getComponent().inject( this );

        EventBus.getBus().register( this );


        setSupportActionBar( toolbar );

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           toolbar.setElevation( 2 );
        }

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        initializeDrawer();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!alreadyInitialize) {
            alreadyInitialize = true;
            drawerMenuContainer.getLayoutParams().width = (int) (this.getWindow().getDecorView().getWidth() * 0.90);
        }
    }

    private void initializeDrawer() {
        List<String> list = new ArrayList<>();
        // TODO:Move to resource, or better implement
        if (User.isLoggedIn()) {
            list.add("My Chats");
            list.add("All Lobbies");
        } else {
            list.add("My Chats");
            list.add("Register");
        }

        list.add("Feedback");
        list.add("Settings");

        mDrawerListView.setAdapter(new ArrayAdapter<String>(
                this,
                R.layout.item_drawer_menu,
                R.id.text_name,
                list));

        int position = User.isLoggedIn() ? 1 : 0;
        selectItem(position); // TODO: The event doesn't launch.
        mDrawerListView.setItemChecked(position, true);
    }

    public void selectItem(int position) {
        Fragment fragment = null;

        switch (position) {
            case 0:
                if (User.isLoggedIn()) {
                    fragment = new MyChatsFragment();
                } else {
                    fragment = new LobbyListFragment();
                }
                break;
            case 1:
                if (User.isLoggedIn()) {
                    fragment = new LobbyListFragment();
                } else {
                    startActivityForResult(new Intent(this, AuthActivity.class), REQUEST_CODE_AUTH);
                }
                break;
            case 2:
                UserVoice.launchUserVoice(this);
                break;
            case 3:
                fragment = new SettingsFragment();
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commitAllowingStateLoss();
        }

        mDrawerLayout.closeDrawers();
    }

    private void reinitialize() {
        initializeDrawer();
        chatService.login(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_AUTH && resultCode == REQUEST_RESULT_CODE_RELOAD) {
            reinitialize();
        } else if (requestCode == CreateLobbyActivity.REQUEST_CODE_CREATE_LOBBY && resultCode == RESULT_OK) {
            //startActivity(new Intent(this));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void retrieveMessage(final String message) {

    }

    @Subscribe
    public void postLogout(UserlogoutEvent event) {
        reinitialize();
    }
}
