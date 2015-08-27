package com.swarmnyc.pup.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.ui.listeners.AnimationEndListener;
import com.swarmnyc.pup.ui.listeners.AnimationStartListener;

/**
 * Created by somya on 4/24/15.
 */
public class ViewAnimationUtils
{
	public static void hideWithAnimation( final Context context, final View view )
	{
		final Animation animation = AnimationUtils.loadAnimation(
			context, R.anim.abc_fade_out
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
			context, R.anim.abc_fade_in
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

	@TargetApi( Build.VERSION_CODES.LOLLIPOP )
	public static void enterReveal( final View view ) {
		// make the view visible and start the animation
		view.setVisibility( View.VISIBLE );



		// previously invisible view
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			return;
		}

		// get the center for the clipping circle
		int cx = view.getMeasuredWidth() / 2;
		int cy = view.getMeasuredHeight() / 2;

		// get the final radius for the clipping circle
		int finalRadius = Math.max( view.getWidth(), view.getHeight()) / 2;

		// create the animator for this view (the start radius is zero)
		Animator anim =
			android.view.ViewAnimationUtils.createCircularReveal( view, cx, cy, finalRadius/2, finalRadius );

		anim.setDuration( 100 );
		anim.start();
	}

	@TargetApi( Build.VERSION_CODES.LOLLIPOP )
	public static void exitReveal( final View myView) {
		// previously visible view

		// previously invisible view
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			myView.setVisibility(View.INVISIBLE);
			return;
		}


		// get the center for the clipping circle
		int cx = myView.getMeasuredWidth() / 2;
		int cy = myView.getMeasuredHeight() / 2;

		// get the initial radius for the clipping circle
		int initialRadius = myView.getWidth() / 2;

		// create the animation (the final radius is zero)
		Animator anim =
			android.view.ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

		// make the view invisible when the animation is done
		anim.addListener(new AnimatorListenerAdapter() {
			                 @Override
			                 public void onAnimationEnd(Animator animation) {
				                 super.onAnimationEnd(animation);
				                 myView.setVisibility(View.INVISIBLE);
			                 }
		                 });

		// start the animation
		anim.start();
	}
}
