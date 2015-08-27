package com.swarmnyc.pup.utils;

import java.util.Calendar;

public class TimeUtils
{
	public static final long minute_in_millis = 60000;
	public static final long hour_in_millis   = 3600000;
	public static final long day_in_millis    = 86400000;
	public static final long week_in_millis   = 604800000;


	public static long todayTimeMillis()
	{
		return today().getTimeInMillis();
	}

	public static Calendar today()
	{
		Calendar c = Calendar.getInstance();
		c.set( Calendar.HOUR_OF_DAY, 0 );
		c.set( Calendar.MINUTE, 0 );
		c.set( Calendar.SECOND, 0 );
		c.set( Calendar.MILLISECOND, 0 );
		return c;
	}

	public static String format( long l )
	{
		long i;
		String s;
		if ( l > TimeUtils.day_in_millis )
		{
			i = l / day_in_millis;
			s = getUnit( "day", i );
		}
		else if ( l > TimeUtils.hour_in_millis )
		{
			i = l / hour_in_millis;
			s = getUnit( "hour", i );
		}
		else{
			i = l / minute_in_millis;
			s = getUnit( "minute", i );
		}

		return i + " " + s;
	}

	private static String getUnit( String t, long s )
	{
		if ( s == 0 )
		{
			return t;
		}
		else
		{
			return t + "s";
		}
	}
}
