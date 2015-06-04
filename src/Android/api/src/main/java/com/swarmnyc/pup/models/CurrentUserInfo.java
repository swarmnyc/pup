package com.swarmnyc.pup.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CurrentUserInfo extends UserInfo
{
	public static final CurrentUserInfo Null = new CurrentUserInfo();
	private String       email;
	private String       accessToken;
	private double       expiresIn;
	private List<PuPTag> tags;
	private Set<String>  media;

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

	public Set<String> getMedia()
	{
		if ( media == null )
		{
			media = new HashSet<>();
		}
		return media;
	}

	public void setMedia( Set<String> media )
	{
		this.media = media;
	}

	public boolean hasMedium( String type )
	{
		for ( String m : media )
		{
			if ( type.equals( m ) )
			{ return true; }
		}

		return false;
	}
}
