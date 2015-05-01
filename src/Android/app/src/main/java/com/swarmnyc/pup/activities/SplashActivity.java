package com.swarmnyc.pup.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.otto.Subscribe;
import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.chat.ChatService;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.events.ChatServiceLoggedInEvent;
import com.uservoice.uservoicesdk.UserVoice;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SplashActivity extends Activity {

    @InjectView(R.id.pup_logo)
    ImageView logoImage;

    @Inject
    ChatService chatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);
        PuPApplication.getInstance().getComponent().inject(this);

        EventBus.getBus().register(this);

        Display display = getWindowManager().getDefaultDisplay();
        Point windowSize = new Point();
        display.getSize(windowSize);
        Consts.windowWidth = windowSize.x;
        Consts.windowHeight = windowSize.y;

        int size = (int) (Consts.windowWidth * 0.7);
        logoImage.getLayoutParams().width = size;
        logoImage.getLayoutParams().height = size;
        ((RelativeLayout.LayoutParams) logoImage.getLayoutParams()).setMargins(0, (int) (size * 0.1), 0, 0);

        chatService.login(this);

        com.uservoice.uservoicesdk.Config config = new  com.uservoice.uservoicesdk.Config("swarmnyc.uservoice.com");
        config.setForumId(272754);
        UserVoice.init(config, this);
    }

    @Subscribe
    public void postChatServiceLoggedin(ChatServiceLoggedInEvent event) {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getBus().unregister(this);
    }
}
