package com.swarmnyc.pup.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.swarmnyc.pup.AsyncCallback;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.adapters.LobbyChatAdapter;
import com.swarmnyc.pup.components.FacebookHelper;
import com.swarmnyc.pup.fragments.OAuthFragment;
import com.swarmnyc.pup.fragments.RedditShareFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ShareFragment {
    private LobbyChatAdapter lobbyChatAdapter;
    @InjectView(R.id.btn_facebook)
    ImageView m_facebookButton;

    @InjectView(R.id.btn_twitter)
    ImageView m_twitterButton;

    @InjectView(R.id.btn_tumblr)
    ImageView m_tumblrButton;

    @InjectView(R.id.btn_reddit)
    ImageView m_redditButton;

    public ShareFragment(LobbyChatAdapter lobbyChatAdapter, final View view) {
        //super(view);
        this.lobbyChatAdapter = lobbyChatAdapter;
        ButterKnife.inject(this, view);

        setButtonState(User.current.hasMedium(Consts.KEY_FACEBOOK), m_facebookButton);
        setButtonState(User.current.hasMedium(Consts.KEY_TWITTER), m_twitterButton);
        setButtonState(User.current.hasMedium(Consts.KEY_TUMBLR), m_tumblrButton);
        setButtonState(User.current.hasMedium(Consts.KEY_REDDIT), m_redditButton);
    }

    private void setButtonState(boolean share, ImageView button) {
        button.setActivated(share);
        //button.setImageResource(m_context.getResources().getIdentifier("ico_" + button.getTag() + (share ? "_select" : ""), "drawable", lobbyChatAdapter.m_context.getPackageName()));
    }

    @OnClick(R.id.btn_facebook)
    void tapOnFacebook() {
        if (m_facebookButton.isActivated()) {
            setButtonState(false, m_facebookButton);
        } else {
            if (User.current.hasMedium(Consts.KEY_FACEBOOK)) {
                setButtonState(true, m_facebookButton);
            } else {
                FacebookHelper.startLoginRequire(
                        new AsyncCallback() {
                            @Override
                            public void success() {
                                setButtonState(true, m_facebookButton);
                            }
                        }
                );
            }
        }
    }

    @OnClick(R.id.btn_twitter)
    void tapOnTwitter() {
        if (m_twitterButton.isActivated()) {
            setButtonState(false, m_twitterButton);
        } else {
            if (User.current.hasMedium(Consts.KEY_TWITTER)) {
                setButtonState(true, m_twitterButton);
            } else {
                OAuthFragment oAuthFragment = new OAuthFragment();
                oAuthFragment.initialize(
                        Consts.KEY_TWITTER, new AsyncCallback() {
                            @Override
                            public void success() {
                                User.addMedium(Consts.KEY_TWITTER);
                                setButtonState(true, m_twitterButton);
                            }
                        }
                );

                oAuthFragment.show(MainActivity.getInstance().getSupportFragmentManager(), null);
            }
        }
    }

    @OnClick(R.id.btn_tumblr)
    void tapOnTumblr() {
        if (m_tumblrButton.isActivated()) {
            setButtonState(false, m_tumblrButton);
        } else {
            if (User.current.hasMedium(Consts.KEY_TUMBLR)) {
                setButtonState(true, m_tumblrButton);
            } else {
                OAuthFragment oAuthFragment = new OAuthFragment();
                oAuthFragment.initialize(
                        Consts.KEY_TUMBLR, new AsyncCallback() {
                            @Override
                            public void success() {
                                User.addMedium(Consts.KEY_TUMBLR);
                                setButtonState(true, m_tumblrButton);
                            }
                        }
                );

                oAuthFragment.show(MainActivity.getInstance().getSupportFragmentManager(), null);
            }
        }
    }

    @OnClick(R.id.btn_reddit)
    void tapOnReddit() {
        if (m_redditButton.isActivated()) {
            setButtonState(false, m_redditButton);
        } else {
            if (User.current.hasMedium(Consts.KEY_REDDIT)) {
                setButtonState(true, m_redditButton);
            } else {
                OAuthFragment oAuthFragment = new OAuthFragment();
                oAuthFragment.initialize(
                        Consts.KEY_REDDIT, new AsyncCallback() {
                            @Override
                            public void success() {
                                User.addMedium(Consts.KEY_REDDIT);
                                setButtonState(true, m_redditButton);
                            }
                        }
                );

                oAuthFragment.show(MainActivity.getInstance().getSupportFragmentManager(), null);
            }
        }
    }

    @OnClick(R.id.btn_invite)
    void invite() {
        List<String> types = new ArrayList<>();
        if (m_facebookButton.isActivated()) {
            types.add(Consts.KEY_FACEBOOK);
        }

        if (m_twitterButton.isActivated()) {
            types.add(Consts.KEY_TWITTER);
        }

        if (m_twitterButton.isActivated()) {
            types.add(Consts.KEY_TUMBLR);
        }

        if (types.size() == 0) {
            if (m_redditButton.isActivated()) {
                ShareToReddit();
            } else {
                //Toast.makeText(lobbyChatAdapter.m_context, "You need to choose at least one social medium", Toast.LENGTH_SHORT).show();
            }

        } else {
           /* lobbyChatAdapter.m_lobbyService.invite(
                    lobbyChatAdapter.m_lobby, types, new ServiceCallback() {
                        @Override
                        public void success(final Object value) {
                            if (m_redditButton.isActivated()) {
                                ShareToReddit();
                            } else {
                                Toast.makeText(lobbyChatAdapter.m_context, "Share Succeeded", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );*/
        }
    }

    private void ShareToReddit() {
       /* RedditShareFragment fragment = new RedditShareFragment();
        fragment.initialize(lobbyChatAdapter.m_lobby, new AsyncCallback() {
            @Override
            public void success() {
                Toast.makeText(lobbyChatAdapter.m_context, "Share Succeeded", Toast.LENGTH_SHORT).show();
            }
        });*/

        //fragment.show(MainActivity.getInstance().getSupportFragmentManager(), "Reddit");
    }
}
