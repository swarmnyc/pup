package com.swarmnyc.pup.chat;

public abstract class ChatRoomService
{
	public abstract void SendMessage( String message );

	public abstract void login();

	public abstract void leave();

	public abstract void loadChatHistory(long date);
}
