package com.swarmnyc.pup.utils;

import android.content.Context;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.module.models.GamePlatform;

/**
 * Created by somya on 4/28/15.
 */
public class GamePlatformUtils
{
	public static int labelResIdForPlatform( final GamePlatform gamePlatform )
	{
		switch ( gamePlatform )
		{
			case PC:
				return R.string.label_pc;
			case Steam:
				return R.string.label_steam;

			case Xbox360:
				return R.string.label_xbox_360;
			case XboxOne:
				return R.string.label_xbox_one;

			case PS3:
				return R.string.label_ps3;
			case PS4:
				return R.string.label_ps4;
			default:
				return R.string.label_other;
		}
	}

	public static String labelForPlatform( final Context context, final GamePlatform gamePlatform )
	{
		return context.getResources().getString( labelResIdForPlatform( gamePlatform ) );
	}
}
