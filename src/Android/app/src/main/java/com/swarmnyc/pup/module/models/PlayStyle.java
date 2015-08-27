package com.swarmnyc.pup.module.models;

public enum PlayStyle
{
	Casual,
	Normal,
	Hardcore;

	public static PlayStyle get( final int value )
	{
		switch ( value )
		{
			case 0:
				return Casual;
			case 1:
				return Normal;
			case 2:
				return Hardcore;
			default:
				return Normal;
		}
	}
}
