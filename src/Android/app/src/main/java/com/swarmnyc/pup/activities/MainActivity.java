package com.swarmnyc.pup.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

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
import com.swarmnyc.pup.fragments.MainDrawerFragment;
import com.uservoice.uservoicesdk.UserVoice;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {
    private static MainActivity m_instance;
    private boolean launchDefault;
    private View m_scrollToEndView;

    @InjectView(R.id.toolbar)
    Toolbar m_toolbar;

    @InjectView(R.id.drawer_layout)
    ViewGroup m_root;

    public MainActivity() {
        m_instance = this;
    }

    public static MainActivity getInstance() {
        return m_instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        PuPApplication.getInstance().getComponent().inject(this);
        m_toolbar.setSubtitleTextColor(getResources().getColor(R.color.pup_grey));

        Display display = getWindowManager().getDefaultDisplay();
        Point windowSize = new Point();
        display.getSize(windowSize);
        Consts.windowWidth = windowSize.x;
        Consts.windowHeight = windowSize.y;

        //Google
        GoogleAnalytics m_googleAnalytics = GoogleAnalytics.getInstance(this);
        m_googleAnalytics.setLocalDispatchPeriod(1800);

        Tracker m_tracker = m_googleAnalytics.newTracker(getString(R.string.google_tracker_key));
        m_tracker.enableExceptionReporting(false);

        //User Voice
        com.uservoice.uservoicesdk.Config config = new com.uservoice.uservoicesdk.Config(getString(R.string
                        .uservoice_id
        ));
        config.setForumId(272754);
        UserVoice.init(config, this);

        Navigator.init(this, m_tracker);

        if (!Config.getBool("ShowedSplash")) {
            Config.setBool("ShowedSplash", true);
            startActivity(new Intent(this, SplashActivity.class));
        }

        Intent intent = getIntent();
        Uri data = intent.getData();
        launchDefault = true;
        if (data != null) {
            List<String> p = data.getPathSegments();
            if (p.size() == 2 && p.get(0).equals("lobby")) {
                launchDefault = false;
                Navigator.ToLobby(p.get(1), "From Intend", false);
            }
        }


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
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getBus().unregister(this);
    }



    public void setViewToScrollToEndWhenKeyboardUp(View view) {
        m_scrollToEndView = view;
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

    public void hideSoftKeyboard() {
        if ( getCurrentFocus() == null )
        {
            return;
        }
        if ( getCurrentFocus().getWindowToken() == null )
        {
            return;
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE
        );

        imm.hideSoftInputFromWindow( getCurrentFocus().getWindowToken(), 0 );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_instance = null;
    }

    private void scrollToEnd() {
        Log.d("Keyboard", "On");
        final RecyclerView recyclerView = (RecyclerView) m_scrollToEndView;
        if (recyclerView != null) {
            Log.d("Keyboard", "On");
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                }
            },100);
        }
    }
}
