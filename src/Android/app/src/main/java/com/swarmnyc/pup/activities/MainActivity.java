package com.swarmnyc.pup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.chat.ChatService;
import com.swarmnyc.pup.fragments.LobbyListFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int REQUEST_RESULT_CODE_RELOAD = 735623;
    public static final int REQUEST_CODE_AUTH = 2884;
    private static MainActivity instance;
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;

    public MainActivity() {
        instance = this;
    }

    public static MainActivity getInstance() {
        return instance;
    }

    @Inject
    ChatService chatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PuPApplication.getInstance().getComponent().inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerListView = (ListView) this.findViewById(R.id.drawer_list);

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

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
        if (User.isLoggedIn()){
            list.add("My Chats");
            list.add("Register");
        }else{
            list.add("All Lobbies");
            list.add("My Chats");
        }

        list.add("Feedback");
        list.add("Settings");

        mDrawerListView.setAdapter(new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                list));

        selectItem(0);
    }

    public void selectItem(int position) {
        Fragment fragment = new LobbyListFragment();

        switch (position) {

        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (User.isLoggedIn()) {
            getMenuInflater().inflate(R.menu.menu_main_user, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main_guest, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logoff:
                User.Logout();
                reload();
                return true;
            case R.id.menu_login:
                this.startActivityForResult(new Intent(this, AuthActivity.class), REQUEST_CODE_AUTH);
                return true;
            case R.id.menu_profile:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commitAllowingStateLoss();
    }

    private void reload() {
        changeFragment(new LobbyListFragment());
        invalidateOptionsMenu();
        chatService.login(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_AUTH && resultCode == REQUEST_RESULT_CODE_RELOAD) {
            reload();
        } else if (requestCode == CreateLobbyActivity.REQUEST_CODE_CREATE_LOBBY && resultCode == RESULT_OK) {
            //startActivity(new Intent(this));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void retrieveMessage(final String message) {

    }
}
