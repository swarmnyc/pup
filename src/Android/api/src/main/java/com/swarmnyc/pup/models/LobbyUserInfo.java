package com.swarmnyc.pup.models;

public class LobbyUserInfo extends UserInfo
{
	boolean isOwner;
	boolean isLeave;

	public LobbyUserInfo( final String userId ) {
		super(userId);
	}

	public boolean getIsOwner()
	{
		return isOwner;
	}

	public void setIsOwner( final boolean isOwner )
	{
		this.isOwner = isOwner;
	}

	public boolean getIsLeave()
	{
		return isLeave;
	}

	public void setIsLeave( final boolean isLeave )
	{
		this.isLeave = isLeave;
	}
}
