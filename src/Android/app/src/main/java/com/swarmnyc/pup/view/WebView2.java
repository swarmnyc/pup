package com.swarmnyc.pup.view;

import android.app.Fragment;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * A simple {@link Fragment} subclass.
 */

public class WebView2 extends WebView
{

	public WebView2( final Context context, final AttributeSet attrs )
	{
		super( context, attrs );
	}

	@Override
	public boolean onCheckIsTextEditor()
	{
		return true;
	}
}
