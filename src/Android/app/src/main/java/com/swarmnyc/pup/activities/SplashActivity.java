package com.swarmnyc.pup.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.otto.Subscribe;
import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.chat.ChatService;
import com.swarmnyc.pup.components.ChatServiceLoggedInEvent;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SplashActivity extends Activity {

    @InjectView(R.id.pup_logo)
    ImageView logoImage;
    private boolean alreadInitialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);
        EventBus.getBus().register(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!alreadInitialized) {
            alreadInitialized = true;
            int size = (int) (this.getWindow().getDecorView().getMeasuredWidth() * 0.7);
            logoImage.getLayoutParams().width = size;
            logoImage.getLayoutParams().height = size;
            ((RelativeLayout.LayoutParams) logoImage.getLayoutParams()).setMargins(0, (int) (size * 0.1), 0, 0);

            ChatService.getInstance().login(this);
        }
    }

    @Subscribe
    public void postChatServiceLoggedin(ChatServiceLoggedInEvent event) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
