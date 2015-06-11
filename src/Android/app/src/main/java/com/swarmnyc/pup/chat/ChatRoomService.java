package com.swarmnyc.pup.chat;

import android.app.Activity;
import com.swarmnyc.pup.components.Action;

public abstract class ChatRoomService {
    protected ChatMessageListener listener;

    public abstract void SendMessage(String message);

    public abstract void login( Action callback );

    public abstract void leave();

    public void setMessageListener(ChatMessageListener listener){
        this.listener = listener;
    }

    public abstract void loadChatHistory();
}
