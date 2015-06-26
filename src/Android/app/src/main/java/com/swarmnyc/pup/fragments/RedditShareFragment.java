package com.swarmnyc.pup.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.TempFragmentDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.swarmnyc.pup.AsyncCallback;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.StringUtils;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.components.Utility;
import com.swarmnyc.pup.models.Lobby;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

public class RedditShareFragment extends TempFragmentDialog
{
	@InjectView( R.id.webview )
	WebView m_webview;

	private Lobby         m_lobby;
	private AsyncCallback m_callback;

	@Override
	public Dialog onCreateDialog( final Bundle savedInstanceState )
	{
		LayoutInflater lf = this.getActivity().getLayoutInflater();
		View view = lf.inflate( R.layout.dialog_share_to_reddit, null );
		View title = lf.inflate( R.layout.title_share_to_reddit, null );

		AlertDialog alertDialog = new AlertDialog.Builder( this.getActivity() ).setView( view ).setCustomTitle( title ).create();

		ButterKnife.inject( this, view );
		String localTime = null;
		try {
			localTime = URLEncoder.encode(new SimpleDateFormat( "MMM dd h:mm a '('zzz')'" ).format( m_lobby.getStartTime() ),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		m_webview.loadUrl( Utility.urlContent( "~/Reddit/Share?user_token=" + User.current.getAccessToken() + "&lobbyId=" + m_lobby.getId() + "&localTime=" + localTime)  );

		WebSettings settings = m_webview.getSettings();
		settings.setJavaScriptEnabled( true );
		m_webview.setWebChromeClient( new WebChromeClient() );
		m_webview.setWebViewClient(
			new WebViewClient()
			{
				@Override
				public boolean shouldOverrideUrlLoading( final WebView view, final String url )
				{
					if ( url.toLowerCase().contains( "done" ) )
					{
						if ( m_callback != null )
						{ m_callback.success(); }
						dismiss();
						return true;
					}

					return super.shouldOverrideUrlLoading( view, url );
				}
			}
		);

		return alertDialog;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		ButterKnife.reset( this );
	}

	public void initialize( Lobby lobby, AsyncCallback callback )
	{
		m_lobby = lobby;
		m_callback = callback;
	}

	@OnClick( R.id.btn_cancel )
	void cancel()
	{
		dismiss();
	}
}
