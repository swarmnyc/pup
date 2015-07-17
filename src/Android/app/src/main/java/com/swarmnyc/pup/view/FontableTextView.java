package com.swarmnyc.pup.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.swarmnyc.pup.R;
import com.swarmnyc.pup.helpers.TypefaceHelper;

public class FontableTextView extends TextView
{
	public FontableTextView( final Context context )
	{
		super( context );
	}

	public FontableTextView( final Context context, final AttributeSet attrs )
	{
		super( context, attrs );
		if ( isInEditMode() )
			return;

		TypedArray array = context.obtainStyledAttributes( attrs, R.styleable.FontableTextView );
		String font;
		font = array.getString( R.styleable.FontableTextView_font );

		if ( font != null )
		{
			Typeface typeface = TypefaceHelper.get(font);
			if ( typeface != null )
			{
				this.setTypeface( typeface );
			}
		}
	}
}
