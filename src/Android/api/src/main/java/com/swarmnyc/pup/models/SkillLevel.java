package com.swarmnyc.pup.models;

public enum SkillLevel
{
	Newbie,
	Easy,
	Medium,
	Hard,
	Nightmare;

	public static SkillLevel get( final int value )
	{
		switch ( value )
		{
			case 0:
				return Newbie;
			case 1:
				return Easy;
			case 2:
				return Medium;
			case 3:
				return Hard;
			case 4:
				return Nightmare;
			default:
				return Newbie;
		}
	}
}
