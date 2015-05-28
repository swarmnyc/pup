package com.swarmnyc.pup.models;

import java.util.ArrayList;
import java.util.List;

public class CurrentUserInfo extends UserInfo
{
	public static final CurrentUserInfo Null = new CurrentUserInfo();
	private String       email;
	private String       accessToken;
	private double       expiresIn;
	private List<PuPTag> tags;
	public CurrentUserInfo()
	{
		tags = new ArrayList<>();
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail( String email )
	{
		this.email = email;
	}

	public String getAccessToken()
	{
		return accessToken;
	}

	public void setAccessToken( final String accessToken )
	{
		this.accessToken = accessToken;
	}

	public double getExpiresIn()
	{
		return expiresIn;
	}

	public void setExpiresIn( double expiresIn )
	{
		this.expiresIn = expiresIn;
	}

	public List<PuPTag> getTags()
	{
		return tags;
	}

	public void setTags( List<PuPTag> tags )
	{
		this.tags = tags;
	}

	public String getTagValue( String key )
	{
		for ( PuPTag tag : tags )
		{
			if ( tag.key.equals( key ) )
			{
				return tag.value;
			}
		}

		return null;
	}
}
