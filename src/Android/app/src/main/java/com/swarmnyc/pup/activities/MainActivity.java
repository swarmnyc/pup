package com.swarmnyc.pup.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import butterknife.OnClick;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.squareup.otto.Subscribe;
import com.swarmnyc.pup.Config;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.components.DialogHelper;
import com.swarmnyc.pup.components.FacebookHelper;
import com.swarmnyc.pup.components.Navigator;
import com.swarmnyc.pup.fragments.LobbyListFragment;
import com.swarmnyc.pup.components.SoftKeyboardHelper;
import com.swarmnyc.pup.fragments.MainDrawerFragment;
import com.swarmnyc.pup.fragments.MyChatsFragment;
import com.swarmnyc.pup.fragments.SettingsFragment;
import com.uservoice.uservoicesdk.UserVoice;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {
    private static MainActivity m_instance;
    private boolean launchDefault;

    @InjectView( R.id.toolbar )   Toolbar   m_toolbar;
    @InjectView( R.id.viewpager ) ViewPager m_viewPager;
    @InjectView( R.id.tabs )  TabLayout m_tabLayout;

        @InjectView(R.id.main_content)
        ViewGroup m_root;

    public MainActivity()
    {
        m_instance = this;
    }

    // TODO @wade this method should not exist, either use getActivity from a fragment or pass the context.
    public static MainActivity getInstance()
    {
        return m_instance;
    }

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        ButterKnife.inject( this );
        PuPApplication.getInstance().getComponent().inject( this );
        m_toolbar.setSubtitleTextColor( getResources().getColor( R.color.pup_grey ) );
        setSupportActionBar( m_toolbar );

        Display display = getWindowManager().getDefaultDisplay();
        Point windowSize = new Point();
        display.getSize( windowSize );
        Consts.windowWidth = windowSize.x;
        Consts.windowHeight = windowSize.y;
        ViewConfiguration vc = ViewConfiguration.get( this );
        Consts.TOUCH_SLOP = vc.getScaledTouchSlop();

        //Google
        GoogleAnalytics m_googleAnalytics = GoogleAnalytics.getInstance( this );
        m_googleAnalytics.setLocalDispatchPeriod( 1800 );

        Tracker m_tracker = m_googleAnalytics.newTracker(getString(R.string.google_tracker_key));
        m_tracker.enableExceptionReporting(true);

        //User Voice
        com.uservoice.uservoicesdk.Config config = new com.uservoice.uservoicesdk.Config(
            getString(
                R.string.uservoice_id
            )
        );
        config.setForumId( 272754 );
        UserVoice.init( config, this );

        Navigator.init( this, m_tracker );


        //Show Splash or not
        if (!Config.getBool("ShowedSplash")) {
            Config.setBool("ShowedSplash", true);
            startActivity(new Intent(this, SplashActivity.class));
        }

        //Redirect to Lobby
        Intent intent = getIntent();
        Uri data = intent.getData();
        launchDefault = true;
        if (data != null) {
            List<String> p = data.getPathSegments();
            if (p.size() == 2 && p.get(0).equals("lobby")) {
                launchDefault = false;
                Navigator.ToLobby(p.get(1), "From Intent", false);
            }
        }

        if ( m_viewPager != null) {
            setupViewPager( m_viewPager );
        }

        m_tabLayout.setupWithViewPager( m_viewPager );
       /* m_softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {

            }

            @Override
            public void onSoftKeyboardShow() {
                if (m_scrollToEndView != null) {
                    scrollToEnd();
                }
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getBus().register(this);
        SoftKeyboardHelper.init(m_root, this );
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getBus().unregister(this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
//        SoftKeyboardHelper.uninit();
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new LobbyListFragment(), "FIND A GAME");
        adapter.addFragment(new MyChatsFragment(), "MY GAMES");
        adapter.addFragment(new SettingsFragment(), "PROFILE");
        viewPager.setAdapter(adapter);
    }

    @OnClick( R.id.fab_create_lobby )
    public void onCreateLobbyButtonClicked()
    {
        Navigator.ToCreateLobby();
    }



    public Toolbar getToolbar() {
        return m_toolbar;
    }

    public boolean isLaunchDefaultFragment() {
        return launchDefault;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FacebookHelper.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (MainDrawerFragment.getInstance().isDrawOpens()) {
            MainDrawerFragment.getInstance().closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    public void retrieveMessage(final String message) {
        //TODO: Push Notification
    }

    public void hideToolbar() {
        m_toolbar.setVisibility(View.GONE);
    }

    public void showToolbar() {
        m_toolbar.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void runtimeError(final RuntimeException exception) {
        this.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        // TODO: Better Message content
                        DialogHelper.showError(exception.getMessage());
                    }
                }
        );
    }





    static class Adapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragments      = new ArrayList<>();
        private final List<String>   mFragmentTitles = new ArrayList<>();

        public Adapter( FragmentManager fm )
        {
            super( fm );
        }

        public void addFragment( Fragment fragment, String title )
        {
            mFragments.add( fragment );
            mFragmentTitles.add( title );
        }

        @Override
        public Fragment getItem( int position )
        {
            return mFragments.get( position );
        }

        @Override
        public int getCount()
        {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle( int position )
        {
            return mFragmentTitles.get( position );
        }
    }

}
