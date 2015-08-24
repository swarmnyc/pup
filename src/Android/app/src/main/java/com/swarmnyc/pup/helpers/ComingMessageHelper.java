package com.swarmnyc.pup.helpers;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.swarmnyc.pup.R;
import com.swarmnyc.pup.chat.ChatMessage;

public class ComingMessageHelper {

    public static void show(Activity activity, ChatMessage message) {
        ViewGroup root = (ViewGroup) activity.findViewById(android.R.id.content);
        ComingMessageLayout layout;
        if (root.getChildAt(root.getChildCount() - 1) instanceof ComingMessageLayout) {
            layout = (ComingMessageLayout) root.getChildAt(root.getChildCount() - 1);
        } else {
            layout = (ComingMessageLayout) activity.getLayoutInflater().inflate(R.layout.view_coming_message, null);
            root.addView(layout);
            layout.getLayoutParams().height = (int) activity.getResources().getDimension(R.dimen.height_coming_message);
            layout.setTranslationY(-layout.getLayoutParams().height);
            layout.setAlpha(0);
        }

        layout.setMessage(message);
        layout.show();
    }

    public static class ComingMessageLayout extends RelativeLayout {
        private boolean isShowing;
        private ChatMessage message;
        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //hide();
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

            //()findViewById(R.id.text_title);
        }

        public void setMessage(ChatMessage message) {
            this.message = message;
        }

        public void show() {
            if (!isShowing) {
                this.animate().translationY(0).alpha(1).start();
            }

            isShowing = true;

            handler.removeMessages(0);
            handler.sendEmptyMessageAtTime(0, 3000);
        }

        public void hide() {
            isShowing = false;
            this.animate().translationY(this.getLayoutParams().height).alpha(0).start();
        }
    }
}
