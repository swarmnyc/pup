package com.swarmnyc.pup.ui.helpers;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.module.chat.ChatMessage;
import com.swarmnyc.pup.module.models.Lobby;
import com.swarmnyc.pup.module.models.UserInfo;
import com.swarmnyc.pup.ui.Navigator;

public class ComingMessageHelper {

    public static void show(Activity activity, Lobby lobby, ChatMessage message) {
        ViewGroup root = (ViewGroup) activity.findViewById(android.R.id.content);
        ComingMessageLayout layout;
        if (root.getChildAt(root.getChildCount() - 1) instanceof ComingMessageLayout) {
            layout = (ComingMessageLayout) root.getChildAt(root.getChildCount() - 1);
        } else {
            layout = (ComingMessageLayout) activity.getLayoutInflater().inflate(R.layout.view_coming_message, null);
            root.addView(layout);
            layout.setClickable(true);
            layout.getLayoutParams().height = (int) activity.getResources().getDimension(R.dimen.height_coming_message);
            layout.setTranslationY(-layout.getLayoutParams().height);
            layout.setAlpha(0);

        }

        layout.bringToFront();
        layout.setMessage(lobby, message);
        layout.show();
    }

    public static class ComingMessageLayout extends RelativeLayout {
        private boolean isShowing;
        private Lobby lobby;
        private ChatMessage message;
        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                hide();
            }
        };

        public ComingMessageLayout(Context context) {
            super(context);
        }

        public ComingMessageLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ComingMessageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public boolean performClick() {
            Navigator.ToLobby(this.getContext(), lobby);
            return true;
        }

        public void setMessage(Lobby lobby, ChatMessage message) {
            this.lobby = lobby;
            this.message = message;
            Picasso.with(getContext())
                    .load(lobby.getThumbnailPictureUrl())
                    .centerCrop()
                    .fit()
                    .into((ImageView) findViewById(R.id.img_game));

            ((TextView) findViewById(R.id.text_title))
                    .setText(lobby.getOwner().getUserName() + "'s " + lobby.getName());

            if (message.isSystemMessage()){
                ((TextView) findViewById(R.id.text_message)).setText(message.getBody());
            }else {
                UserInfo user = lobby.getUser(message.getUser().getId());
                ((TextView) findViewById(R.id.text_message)).setText(user.getUserName() + " : " + message.getBody());
            }
        }

        public void show() {
            if (!isShowing) {
                this.animate().translationY(0).alpha(1).start();
            }

            isShowing = true;

            handler.removeMessages(0);
            handler.sendEmptyMessageDelayed(0, 3000);
        }

        public void hide() {
            isShowing = false;
            this.animate().translationY(-this.getLayoutParams().height).alpha(0).start();
        }
    }
}
