package com.swarmnyc.pup.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.swarmnyc.pup.Config;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.module.models.Lobby;
import com.swarmnyc.pup.module.service.LobbyService;
import com.swarmnyc.pup.module.service.ServiceCallback;
import com.swarmnyc.pup.ui.Navigator;
import com.swarmnyc.pup.ui.events.UnhandledChatMessageReceiveEvent;
import com.swarmnyc.pup.ui.events.UserChangedEvent;
import com.swarmnyc.pup.ui.fragments.BaseFragment;
import com.swarmnyc.pup.ui.fragments.LobbyListFragment;
import com.swarmnyc.pup.ui.fragments.MyChatsFragment;
import com.swarmnyc.pup.ui.fragments.SettingsFragment;
import com.swarmnyc.pup.ui.helpers.ComingMessageHelper;
import com.swarmnyc.pup.ui.helpers.DialogHelper;
import com.swarmnyc.pup.ui.helpers.FacebookHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar m_toolbar;
    @Bind(R.id.viewpager)
    ViewPager m_viewPager;
    @Bind(R.id.tabs)
    TabLayout m_tabLayout;
    @Bind(R.id.appbar)
    AppBarLayout m_appBarLayout;

    @Bind(R.id.fab_create_lobby)
    FloatingActionButton m_floatingActionButton;

    @Bind(R.id.layout_coordinator)
    CoordinatorLayout m_coordinatorLayout;
    private TabPagerAdapter m_tabPagerAdapter;
    private Boolean isLoggedIn = null;
    private Fragment m_currentFragment;
    private LobbyService m_lobbyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        m_toolbar.setTitle("");
        setSupportActionBar(m_toolbar);

        Display display = getWindowManager().getDefaultDisplay();
        Point windowSize = new Point();
        display.getSize(windowSize);
        Consts.windowWidth = windowSize.x;
        Consts.windowHeight = windowSize.y;
        ViewConfiguration vc = ViewConfiguration.get(this);
        Consts.TOUCH_SLOP = vc.getScaledTouchSlop();

        //Show Splash or not
        if (!Config.getBool("ShowedSplash")) {
            Config.setBool("ShowedSplash", true);
            startActivity(new Intent(this, SplashActivity.class));
        }

        //Redirect to Lobby
        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            List<String> p = data.getPathSegments();
            if (p.size() == 2 && p.get(0).equals("lobby")) {
                Navigator.ToLobby(this, p.get(1), "From Intent");
            }
        }

        //Update Facebook token;
        long fbExpiredAt = Config.getLong(Consts.KEY_FACEBOOK_EXPIRED_DATE);
        if (fbExpiredAt != 0 && System.currentTimeMillis() > fbExpiredAt) {
            Log.d("PuP", "Refresh Facebook token");
            FacebookHelper.startLoginRequire(this, null);
        }

        m_lobbyService = PuPApplication.getInstance().getModule().provideLobbyService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getBus().register(this);
        PuPApplication.getInstance().startMessageService();
        showTabsByUser(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getBus().unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setupViewPager() {
        if (m_tabPagerAdapter == null) {
            m_tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
            m_tabPagerAdapter.addFragment(m_currentFragment = new LobbyListFragment(), "ALL GAMES");
        }

        if (User.isLoggedIn()) {
            m_tabPagerAdapter.addFragment(new MyChatsFragment(), "JOINED GAMES");
            m_tabPagerAdapter.addFragment(new SettingsFragment(), "PROFILE");
            m_tabPagerAdapter.notifyDataSetChanged();
        }

        if (m_viewPager.getAdapter() == null) {
            m_viewPager.setAdapter(m_tabPagerAdapter);

            ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(final int position) {
                    Consts.currentPage = position;
                    m_currentFragment = m_tabPagerAdapter.getItem(position);
                    if (m_currentFragment instanceof BaseFragment) {
                        BaseFragment bf = (BaseFragment) m_currentFragment;
                        bf.updateTitle();

                        PuPApplication.getInstance().sendScreenToTracker(bf.getScreenName());
                    }
                }

                @Override
                public void onPageScrollStateChanged(final int state) {

                }
            };

            m_viewPager.addOnPageChangeListener(listener);
            m_tabLayout.setupWithViewPager(m_viewPager);
        }
    }

    @OnClick(R.id.fab_create_lobby)
    public void onCreateLobbyButtonClicked() {
        Navigator.ToCreateLobby(this);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FacebookHelper.handleActivityResult(requestCode, resultCode, data);
    }

    @Subscribe
    public void runtimeError(final Exception exception) {
        this.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = (TextView) m_currentFragment.getView().findViewById(R.id.text_empty_results);
                        if (textView != null) {
                            ConnectivityManager cm =
                                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                            if (activeNetwork == null || !activeNetwork.isConnectedOrConnecting()) {
                                textView.setText(R.string.message_no_internet);
                            }
                        }

                        DialogHelper.showError(MainActivity.this, exception);
                    }
                }
        );
    }

    @Subscribe
    public void showTabsByUser(UserChangedEvent event) {
        if (isLoggedIn == null || isLoggedIn != User.isLoggedIn()) {
            if (User.isLoggedIn()) {
                m_tabLayout.setVisibility(View.VISIBLE);
            } else {
                m_tabLayout.setVisibility(View.GONE);
            }

            setupViewPager();

            if (isLoggedIn != null) {
                // TabLayout has a bug, adapter update, but tablayout doesn't
                m_tabLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        m_tabLayout.removeAllTabs();
                        m_tabLayout.setTabsFromPagerAdapter(m_tabPagerAdapter);
                    }
                }, 100);
            }

            isLoggedIn = User.isLoggedIn();
        }
    }

    @Subscribe
    public void receiveMessage(final UnhandledChatMessageReceiveEvent event) {
        m_lobbyService.getLobby(event.getLobbyId(), new ServiceCallback<Lobby>() {
            @Override
            public void success(Lobby lobby) {

                ComingMessageHelper.show(MainActivity.this, lobby, event.getMessages().get(event.getMessages().size() - 1));
            }
        });
    }


    static class TabPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            for (int i = 0; i < mFragments.size(); i++) {
                if (mFragments.get(i) == object) {
                    return i;
                }
            }

            return super.getItemPosition(object);
        }
    }

}
