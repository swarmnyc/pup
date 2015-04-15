package com.swarmnyc.pup.chat;

import android.app.Activity;

import com.swarmnyc.pup.BuildConfig;
import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.components.ChatServiceLoggedInEvent;
import com.swarmnyc.pup.models.Lobby;

public class MockChatService implements ChatService {
    @Override
    public void login(Activity activity) {
        EventBus.getBus().post(new ChatServiceLoggedInEvent());
    }

    @Override
    public ChatRoomService getChatRoom(Activity activity, Lobby lobby) {
        return null;
    }
}
