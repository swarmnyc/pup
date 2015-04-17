package com.swarmnyc.pup;

/**
 * Created by somya on 4/17/15.
 */
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
