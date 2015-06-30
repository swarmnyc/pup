package com.swarmnyc.pup.events;

import com.quickblox.chat.QBGroupChat;

public class EnterChatRoomEvent
{
	private QBGroupChat m_chat;

	public EnterChatRoomEvent( final QBGroupChat chat )
	{
		m_chat = chat;
	}

	public QBGroupChat getChat()
	{
		return m_chat;
	}
}
