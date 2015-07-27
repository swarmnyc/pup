package com.swarmnyc.pup.events;

public class AfterLeaveLobbyEvent
{
	private String m_roomId;

	public AfterLeaveLobbyEvent( final String roomId )
	{
		m_roomId = roomId;
	}

	public String getRoomId()
	{
		return m_roomId;
	}
}
