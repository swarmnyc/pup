package com.swarmnyc.pup.module.models;

import com.swarmnyc.pup.Config;

public class UserInfo
{
	String id;
	String userName;
	String portraitUrl;

	public UserInfo(  )
	{

	}

	public UserInfo( final String id )
	{
		this.id = id;
	}

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
		if ( portraitUrl != null && portraitUrl.startsWith( "~/" ) )
		{ portraitUrl = portraitUrl.replace( "~/", Config.PuPServerPath ); }

		return portraitUrl;
	}

	public void setPortraitUrl( final String pictureUrl )
	{
		this.portraitUrl = pictureUrl;
	}
}
