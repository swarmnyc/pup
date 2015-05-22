package com.swarmnyc.pup.events;

public class UserChangedEvent {
	private boolean m_goHome;

	public UserChangedEvent( final boolean goHome )
	{

		m_goHome = goHome;
	}

	public boolean isGoHome()
	{
		return m_goHome;
	}
}
