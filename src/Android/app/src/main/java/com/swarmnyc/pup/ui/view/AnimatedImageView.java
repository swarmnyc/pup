package com.swarmnyc.pup.ui.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AnimatedImageView extends ImageView {

    public AnimatedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //Log.d("AnimatedImageView","onAttachedToWindow");
        AnimationDrawable drawable = (AnimationDrawable) getDrawable();
        if (drawable != null) {
            drawable.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //Log.d("AnimatedImageView", "onDetachedFromWindow");
        AnimationDrawable drawable = (AnimationDrawable) getDrawable();
        if (drawable != null) {
            drawable.stop();
        }
    }
}