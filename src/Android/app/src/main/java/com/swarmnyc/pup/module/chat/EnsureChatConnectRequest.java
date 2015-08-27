package com.swarmnyc.pup.module.chat;

public class EnsureChatConnectRequest
{
	private String m_roomId;
	private Runnable m_callback;

	public EnsureChatConnectRequest( String roomId, Runnable callback )
	{
		m_roomId = roomId;
		m_callback = callback;
	}

	public String getRoomId()
	{
		return m_roomId;
	}

	public void setRoomId( final String roomId )
	{
		m_roomId = roomId;
	}

	public Runnable getCallback()
	{
		return m_callback;
	}

	public void setCallback( Runnable callback )
	{
		m_callback = callback;
	}
}
