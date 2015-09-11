package com.swarmnyc.pup.module.models;

import com.google.gson.annotations.SerializedName;

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

	@SerializedName( "media" )
	private Set<String>  socialMedia;

	public CurrentUserInfo()
	{
		tags = new ArrayList<>();
		socialMedia = new HashSet<>();
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
		return getTagValue(key, null);
	}

	public String getTagValue( String key, String defaultValue )
	{
		for ( PuPTag tag : tags )
		{
			if ( tag.key.equals( key ) )
			{
				return tag.value;
			}
		}

		return defaultValue;
	}

	public Set<String> getSocialMedia()
	{
		return socialMedia;
	}

	public void setSocialMedia(Set<String> socialMedia)
	{
		this.socialMedia = socialMedia;
	}

	public boolean hasSocialMedium(String type)
	{
		for ( String m : socialMedia)
		{
			if ( type.equals( m ) )
			{ return true; }
		}

		return false;
	}
}
