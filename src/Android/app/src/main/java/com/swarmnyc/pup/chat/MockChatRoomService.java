package com.swarmnyc.pup.chat;

import android.os.Handler;
import android.os.Message;

import com.swarmnyc.pup.ChatMessageReceiveEvent;
import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.StringUtils;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.models.LobbyUserInfo;

import java.util.ArrayList;
import java.util.List;

public class MockChatRoomService extends ChatRoomService
{
	Handler m_handler = new Handler()
	{
		@Override
		public void handleMessage( final Message msg )
		{
			List<ChatMessage> list = new ArrayList<>();

			for ( int i = 0; i < 20; i++ )
			{
				LobbyUserInfo user = new LobbyUserInfo();
				if ( i % 4 == 0 )
				{
					user.setId( User.current.getId() );
					user.setUserName( User.current.getUserName() );
					user.setPortraitUrl( User.current.getPortraitUrl() );
				}
				else
				{
					user.setId( "0" );
					user.setUserName( "Name" );
				}

				list.add( new ChatMessage( user,String.valueOf( i ), "Test " + i,0 ) );
			}

			EventBus.getBus().post( new ChatMessageReceiveEvent( "", false, list ) );
		}
	};

	@Override
	public void SendMessage( String message )
	{
		List<ChatMessage> list = new ArrayList<>();
		LobbyUserInfo user = new LobbyUserInfo();
		user.setId( "0" );
		user.setId( "Name" );
		list.add( new ChatMessage( user, String.valueOf( System.currentTimeMillis() ), message,0 ) );
		EventBus.getBus().post( new ChatMessageReceiveEvent( "", false, list ) );
	}

	@Override
	public void leave()
	{
	}

	@Override
	public void loadChatHistory(long date)
	{
	}

	@Override
	public void login( final boolean loadHistory )
	{
		m_handler.sendEmptyMessageDelayed( 0, 1000 );
	}
}
