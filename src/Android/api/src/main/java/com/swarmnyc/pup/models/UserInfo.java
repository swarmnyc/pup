package com.swarmnyc.pup.models;

public class UserInfo
{
	String id;
	String userName;
	String portraitUrl;

	public String getId()
	{
		return id;
	}

	public void setId( final String id )
	{
		this.id = id;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName( final String userName )
	{
		this.userName = userName;
	}

	public String getPortraitUrl()
	{
		return portraitUrl;
	}

	public void setPortraitUrl( final String pictureUrl )
	{
		this.portraitUrl = pictureUrl;
	}
}