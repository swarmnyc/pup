package com.swarmnyc.pup.chat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;
import com.swarmnyc.pup.components.PlayServicesHelper;
import com.swarmnyc.pup.models.Lobby;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.List;

public interface ChatService {
    public void login(Activity activity);

    public ChatRoomService getChatRoom(Activity activity, Lobby lobby);
}
