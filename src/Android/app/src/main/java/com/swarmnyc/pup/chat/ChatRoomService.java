package com.swarmnyc.pup.chat;

public abstract class ChatRoomService
{
	protected ChatMessageListener listener;

	public abstract void SendMessage( String message );

	public abstract void login();

	public abstract void leave();

	public void setMessageListener( ChatMessageListener listener )
	{
		this.listener = listener;
	}

	public abstract void loadChatHistory();
}
