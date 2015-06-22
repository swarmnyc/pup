package com.swarmnyc.pup.models;

import com.google.gson.annotations.SerializedName;
import com.swarmnyc.pup.ApiSettings;

import java.util.Date;
import java.util.List;

public class Game extends Taggable implements PicturedModel
{

	private String             name;
	private String             pictureUrl;
	private String             thumbnailPictureUrl;
	private String             description;
	private List<GamePlatform> platforms;
	private List<String>       gameTypes;
	private int                rank;

	@SerializedName( "releaseDateUtc" )
	private Date releaseDate;

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public String getPictureUrl()
	{
		if ( pictureUrl != null && pictureUrl.startsWith( "~/" ) )
		{ pictureUrl = pictureUrl.replace( "~/", ApiSettings.PuPServerPath ); }

		return pictureUrl;
	}

	public void setPictureUrl( String pictureUrl )
	{
		this.pictureUrl = pictureUrl;
	}

	public String getThumbnailPictureUrl()
	{
		if ( thumbnailPictureUrl != null && thumbnailPictureUrl.startsWith( "~/" ) )
		{  thumbnailPictureUrl = thumbnailPictureUrl.replace( "~/", ApiSettings.PuPServerPath ); }

		return thumbnailPictureUrl;
	}

	public void setThumbnailPictureUrl( String thumbnailPictureUrl )
	{
		this.thumbnailPictureUrl = thumbnailPictureUrl;
	}

	public List<GamePlatform> getPlatforms()
	{
		return platforms;
	}

	public void setPlatforms( List<GamePlatform> platforms )
	{
		this.platforms = platforms;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription( String description )
	{
		this.description = description;
	}

	public Date getReleaseDate()
	{
		return releaseDate;
	}

	public void setReleaseDate( Date releaseDate )
	{
		this.releaseDate = releaseDate;
	}

	public List<String> getGameTypes()
	{
		return gameTypes;
	}

	public void setGameTypes( List<String> gameTypes )
	{
		this.gameTypes = gameTypes;
	}

	public int getRank()
	{
		return rank;
	}

	public void setRank( int rank )
	{
		this.rank = rank;
	}
}
