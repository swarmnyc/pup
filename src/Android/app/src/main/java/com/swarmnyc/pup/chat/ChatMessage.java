package com.swarmnyc.pup.chat;

import com.swarmnyc.pup.Config;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.models.UserInfo;

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


	public ChatMessage(
		UserInfo user, String id , String body, long sentAt, String code, String codeBody
	)
	{
		m_id = id;
		m_user = user;
		m_body = body;
		m_sentAt = sentAt;
		m_code = code;
		m_codeBody = codeBody;
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

	public boolean isSystemMessage()
	{
		return  m_user == null || m_user.getId().equals(Config.getConfigString( R.string.QB_APP_DEFAULT_USER  )) ;
	}

	public String getCode()
	{
		return m_code;
	}

	public String getCodeBody()
	{
		return m_codeBody;
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
