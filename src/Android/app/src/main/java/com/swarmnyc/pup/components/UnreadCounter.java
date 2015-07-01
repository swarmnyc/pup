package com.swarmnyc.pup.components;

import java.util.Hashtable;

//TODO: Temporary Solution, init values are from my our server, increasing by MessageService
public class UnreadCounter
{
	private static Hashtable<String, Integer> counter = new Hashtable<>();

	public static void Add( String roomId, int i )
	{
		counter.put( roomId, get(roomId) + i );
	}

	public static void reset( String roomId, int i )
	{
		counter.put( roomId, i );
	}

	public static void reset( String roomId )
	{
		counter.put( roomId, 0 );
	}

	public static int total() {
		int t=0;
		for ( Integer i : counter.values() )
		{
			t+= i;
		}

		return t;
	}

	public static int get( final String roomId )
	{
		if ( counter.containsKey( roomId ) )
		{
			return counter.get( roomId );
		}

		return 0;
	}
}
