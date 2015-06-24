package com.swarmnyc.pup.models;

public enum SkillLevel
{
	Newbie,
	Intermediate,
	L337,
	Pro;

	public static SkillLevel get( final int value )
	{
		switch ( value )
		{
			case 0:
				return Newbie;
			case 1:
				return Intermediate;
			case 2:
				return L337;
			case 3:
				return Pro;
			default:
				return Newbie;
		}
	}
}
