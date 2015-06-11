package com.swarmnyc.pup.chat;

import com.swarmnyc.pup.Config;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.models.UserInfo;

public class ChatMessage
{
	private UserInfo m_user;
	private String   m_body;

	public ChatMessage( UserInfo user, String body )
	{
		m_user = user;
		m_body = body;
	}

	public String getBody()
	{
		return m_body;
	}

	public UserInfo getUser()
	{
		return m_user;
	}

	public boolean isSystemMessage()
	{
		return  m_user == null || m_user.getId().equals(Config.getConfigString( R.string.QB_APP_DEFAULT_USER  )) ;
	}
}
