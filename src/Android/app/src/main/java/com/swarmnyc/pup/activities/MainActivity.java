package com.swarmnyc.pup.activities;

import android.content.Intent;
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

import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.User;
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

    @InjectView(R.id.btn_logout)
    View logoutButton;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private ActionBarDrawerToggle mDrawerToggle;


    public MainActivity() {
        instance = this;
    }

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        PuPApplication.getInstance().getComponent().inject(this);

        setSupportActionBar(toolbar);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.Logout();
                reinitialize();
            }
        });

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
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                list));

        //mDrawerListView.setSelection(User.isLoggedIn() ? 1 : 0);

        int position = User.isLoggedIn() ? 1 : 0;
        selectItem(position); // TODO: The event doesn't launch.
        mDrawerListView.setItemChecked(position, true);

        logoutButton.setVisibility(User.isLoggedIn() ? View.VISIBLE : View.GONE);
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

    public void changeFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commitAllowingStateLoss();
    }

    private void reinitialize() {
        changeFragment(new LobbyListFragment());
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
}
