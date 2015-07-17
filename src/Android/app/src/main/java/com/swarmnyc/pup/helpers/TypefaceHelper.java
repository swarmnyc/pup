package com.swarmnyc.pup.helpers;

import android.graphics.Typeface;
import com.swarmnyc.pup.PuPApplication;

import java.util.HashMap;

public class TypefaceHelper
{
	private static HashMap<String, Typeface> typefaces = new HashMap<>();

	private static Typeface create( final String font )
	{
		return Typeface.createFromAsset(
			PuPApplication.getInstance().getResources().getAssets(), "fonts/" + font
		);
	}

	public static Typeface get( final int id )
	{
		String font = PuPApplication.getInstance().getResources().getString( id );

		return get( font );
	}

	public static Typeface get( final String font )
	{
		if ( font == null ){
			throw new IllegalArgumentException( "font" );
		}

		Typeface typeface = typefaces.get( font );
		if ( typeface == null )
		{
			typeface = create( font );
			typefaces.put( font, typeface );
		}

		return typeface;
	}
}
