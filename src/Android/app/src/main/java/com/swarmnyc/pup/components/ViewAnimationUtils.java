package com.swarmnyc.pup.components;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.swarmnyc.pup.R;

/**
 * Created by somya on 4/24/15.
 */
public class ViewAnimationUtils
{
	public static void hideWithAnimation( final Context context, final View view )
	{
		final Animation animation = AnimationUtils.loadAnimation(
			context, R.anim.abc_shrink_fade_out_from_bottom
		);
		view.startAnimation(animation);
		animation.setAnimationListener(
			new AnimationEndListener() {

				@Override
				public void onAnimationEnd(final Animation animation) {
					view.setVisibility( View.GONE);
				}
			}
		);
	}

	public static void showWithAnimation( final Context context, final View view )
	{
		final Animation animation = AnimationUtils.loadAnimation(
			context, R.anim.abc_grow_fade_in_from_bottom
		);
		view.startAnimation( animation );
		animation.setAnimationListener(
			new AnimationStartListener() {
				@Override
				public void onAnimationStart(final Animation animation) {
					view.setVisibility(View.VISIBLE);
				}
			}
		);
	}
}
