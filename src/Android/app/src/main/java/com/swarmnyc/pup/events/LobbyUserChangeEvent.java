package com.swarmnyc.pup.events;

public class LobbyUserChangeEvent
{
	public LobbyUserChangeEvent(final boolean currentUser)
	{
		m_currentUser = currentUser;
	}

	private boolean m_currentUser;

	public boolean isCurrentUser()
	{
		return m_currentUser;
	}
}
