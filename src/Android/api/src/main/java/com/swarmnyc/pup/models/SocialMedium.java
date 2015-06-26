package com.swarmnyc.pup.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class SocialMedium
{
	String type;
	String userId;
	String token;
	String secret;
	@SerializedName( "ExpireAtUtc" ) Date expireAt;

	public String getType()
	{
		return type;
	}

	public void setType( final String type )
	{
		this.type = type;
	}

	public Date getExpireAt()
	{
		return expireAt;
	}

	public void setExpireAt( final Date expireAt )
	{
		this.expireAt = expireAt;
	}

	public String getSecret()
	{
		return secret;
	}

	public void setSecret( final String secret )
	{
		this.secret = secret;
	}

	public String getToken()
	{
		return token;
	}

	public void setToken( final String token )
	{
		this.token = token;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId( final String userId )
	{
		this.userId = userId;
	}
}
