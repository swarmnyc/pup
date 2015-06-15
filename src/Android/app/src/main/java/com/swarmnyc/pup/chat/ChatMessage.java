package com.swarmnyc.pup.chat;

import com.swarmnyc.pup.Config;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.models.LobbyUserInfo;
import com.swarmnyc.pup.models.UserInfo;

public class ChatMessage
{
	private UserInfo m_user;
	private String   m_body;
	private boolean  m_newMessage;
	private String   m_code;
	private String m_codeBody;

	public ChatMessage( final UserInfo user, final String body )
	{
		m_user = user;
		m_body = body;
	}


	public ChatMessage( UserInfo user, String body, boolean newMessage, String code, String codeBody )
	{
		m_user = user;
		m_body = body;
		m_newMessage = newMessage;
		m_code = code;
		m_codeBody = codeBody;
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

	public boolean isNewMessage()
	{
		return m_newMessage;
	}

	public String getCode()
	{
		return m_code;
	}

	public String getCodeBody()
	{
		return m_codeBody;
	}
}
