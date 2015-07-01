package com.swarmnyc.pup.models;

public class QBChatMessage2
{
	String chat_dialog_id;
	int date_sent;
	int send_to_chat;
	String message;
	String userId;

	public String getChat_dialog_id()
	{
		return chat_dialog_id;
	}

	public int getDate_sent()
	{
		return date_sent;
	}

	public String getMessage()
	{
		return message;
	}

	public int getSend_to_chat()
	{
		return send_to_chat;
	}

	public String getUserId()
	{
		return userId;
	}
}
