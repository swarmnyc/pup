package com.swarmnyc.pup.chat;

import android.app.Activity;
import android.os.Bundle;
import com.quickblox.chat.QBChat;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChat;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.models.LobbyUserInfo;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockChatRoomService extends ChatRoomService
{
	@Override
	public void SendMessage( String message )
	{
		List<ChatMessage> list = new ArrayList<>();
		LobbyUserInfo user = new LobbyUserInfo();
		user.setId( "0" );
		user.setId( "Name" );
		list.add( new ChatMessage( user, message ) );
		listener.receive( list );
	}

	@Override
	public void login()
	{
		List<ChatMessage> list = new ArrayList<>();

		for ( int i = 0; i < 20; i++ )
		{
			LobbyUserInfo user = new LobbyUserInfo();
			user.setId( "0" );
			user.setId( "Name" );
			list.add( new ChatMessage( user, "Test " + i ) );
		}

		listener.receive( list );
	}

	@Override
	public void leave()
	{
	}

	@Override
	public void loadChatHistory()
	{
	}
}
