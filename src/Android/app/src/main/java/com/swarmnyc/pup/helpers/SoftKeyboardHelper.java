package com.swarmnyc.pup.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.components.Action;

public class SoftKeyboardHelper
{

	public static void setSoftKeyboardCallback( View rootView, Action<Boolean> callback )
	{
		GlobalLayoutListener globalLayoutListener = new GlobalLayoutListener( rootView, callback );
		rootView.setTag( R.id.GlobalLayoutListener, globalLayoutListener );

		rootView.getViewTreeObserver().addOnGlobalLayoutListener( globalLayoutListener );
	}

	public static void removeSoftKeyboardCallback( View rootView )
	{
		GlobalLayoutListener globalLayoutListener = (GlobalLayoutListener) rootView.getTag( R.id.GlobalLayoutListener );
		if ( globalLayoutListener != null )
		{
			rootView.getViewTreeObserver().removeOnGlobalLayoutListener( globalLayoutListener );
			rootView.setTag( R.id.GlobalLayoutListener, null );
		}
	}

	public static void hideSoftKeyboard( Activity activity )
	{
		if ( activity.getCurrentFocus() == null )
		{
			return;
		}
		if ( activity.getCurrentFocus().getWindowToken() == null )
		{
			return;
		}

		( (InputMethodManager) activity.getSystemService( Context.INPUT_METHOD_SERVICE ) ).hideSoftInputFromWindow(
			activity.getCurrentFocus().getWindowToken(),
			0
		);
	}

	private static class GlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener
	{
		private View m_view;
		private Action<Boolean> m_callback;

		public GlobalLayoutListener( final View view, final Action<Boolean> callback )
		{
			m_view = view;
			m_callback = callback;
		}

		@Override
		public void onGlobalLayout()
		{
			Rect r = new Rect();
			m_view.getWindowVisibleDisplayFrame( r );

			int heightDifference = Consts.windowHeight - ( r.bottom - r.top );

			if ( heightDifference > 100 )
			{
				Log.d( "SoftKeyboard", "Open" );
				m_callback.call( true );
			}
			else
			{
				Log.d( "SoftKeyboard", "Close" );
				m_callback.call( false );
			}
		}
	}
}
