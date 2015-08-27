package com.swarmnyc.pup.module.chat;

import com.swarmnyc.pup.Config;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.module.models.UserInfo;

public class ChatMessage
{
	private UserInfo m_user;

	private String m_body;
	private long   m_sentAt;
	private String m_code;
	private String m_codeBody;
	private String m_id;

	public ChatMessage( UserInfo user,String id , String body, long sentAt )
	{
		m_id = id;
		m_user = user;
		m_body = body;
		m_sentAt = sentAt;
	}

	public String getBody()
	{
		return m_body;
	}

	public void setBody(String body) {
		this.m_body = body;
	}

	public UserInfo getUser()
	{
		return m_user;
	}

	public void setUser( final UserInfo user )
	{
		m_user = user;
	}

	public boolean isSystemMessage()
	{
		return  m_user == null || m_user.getId().equals(Config.getConfigString( R.string.QB_APP_DEFAULT_USER  )) ;
	}

	public long getSentAt()
	{
		return m_sentAt;
	}

	public String getId()
	{
		return m_id;
	}
}
