package com.swarmnyc.pup;

import android.app.Application;
import android.graphics.Typeface;

public class Typefaces
{
	public static Typeface RobotoSlab = init( "RobotoSlab-Regular.ttf" );

	private static Typeface init( final String font )
	{
		return Typeface.createFromAsset( PuPApplication.getInstance().getResources().getAssets(), "fonts/" + font );
	}

}
