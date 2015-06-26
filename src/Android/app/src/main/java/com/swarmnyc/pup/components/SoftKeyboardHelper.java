package com.swarmnyc.pup.components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import com.swarmnyc.pup.Consts;

public class SoftKeyboardHelper
{
	private static Activity             m_activity;
	private static View                 m_rootView;
	private static InputMethodManager   m_inputMethodManager;
	private static GlobalLayoutListener m_globalLayoutListener;

	public static void init( View rootView, Activity activity )
	{
		m_activity = activity;
		m_rootView = rootView;
		m_inputMethodManager = (InputMethodManager) activity.getSystemService( Context.INPUT_METHOD_SERVICE );
		m_globalLayoutListener = null;
	}

	public static void uninit()
	{
		removeSoftKeyboardCallback();
		m_activity = null;
		m_inputMethodManager = null;
		m_rootView = null;
		m_globalLayoutListener = null;
	}

	public static void setSoftKeyboardCallback( Action callback )
	{
		m_globalLayoutListener = new GlobalLayoutListener(m_rootView, callback );
		m_rootView.getViewTreeObserver().addOnGlobalLayoutListener( m_globalLayoutListener );
	}

	public static void removeSoftKeyboardCallback()
	{
		if ( m_rootView != null && m_globalLayoutListener != null )
		{
			m_rootView.getViewTreeObserver().removeOnGlobalLayoutListener( m_globalLayoutListener );
			m_globalLayoutListener = null;
		}
	}

	public static void hideSoftKeyboard()
	{
		if ( m_activity.getCurrentFocus() == null )
		{
			return;
		}
		if ( m_activity.getCurrentFocus().getWindowToken() == null )
		{
			return;
		}

		m_inputMethodManager.hideSoftInputFromWindow( m_activity.getCurrentFocus().getWindowToken(), 0 );
	}

	private static class GlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener
	{
		private View   m_view;
		private Action m_callback;

		public GlobalLayoutListener( final View view, final Action callback )
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
				m_callback.call( null );
			}
			else
			{
				Log.d( "SoftKeyboard", "Close" );
			}
		}
	}
}
