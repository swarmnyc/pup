package com.swarmnyc.pup.events;

import com.quickblox.chat.QBGroupChat;

public class AfterEnterChatRoomEvent
{
	private QBGroupChat m_chat;

	public AfterEnterChatRoomEvent( final QBGroupChat chat )
	{
		m_chat = chat;
	}

	public QBGroupChat getChat()
	{
		return m_chat;
	}
}
