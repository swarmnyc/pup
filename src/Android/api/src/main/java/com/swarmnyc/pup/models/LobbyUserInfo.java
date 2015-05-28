package com.swarmnyc.pup.models;

public class LobbyUserInfo extends UserInfo
{
	Boolean isOwner;
	Boolean isLeave;

	public Boolean getIsOwner()
	{
		return isOwner;
	}

	public void setIsOwner( final Boolean isOwner )
	{
		this.isOwner = isOwner;
	}

	public Boolean getIsLeave()
	{
		return isLeave;
	}

	public void setIsLeave( final Boolean isLeave )
	{
		this.isLeave = isLeave;
	}
}
