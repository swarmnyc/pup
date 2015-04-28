package com.swarmnyc.pup;

public class StringUtils
{

	public static boolean isNotEmpty( final String s )
	{
		return null != s && s.length() > 0;
	}

	public static boolean isEmpty( final String s )
	{
		return null == s || s.length() == 0;
	}

}
