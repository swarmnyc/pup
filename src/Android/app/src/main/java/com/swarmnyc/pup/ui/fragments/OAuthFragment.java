package com.swarmnyc.pup.ui.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import com.swarmnyc.pup.utils.AsyncCallback;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.utils.Utility;

public class OAuthFragment extends DialogFragment
{
	@Bind( R.id.webview )
	WebView m_webview;

	ProgressBar m_progressBar;

	private String        m_type;
	private AsyncCallback m_callback;

	@Override
	public Dialog onCreateDialog( final Bundle savedInstanceState )
	{
		LayoutInflater lf = this.getActivity().getLayoutInflater();
		View view = lf.inflate( R.layout.dialog_connection, null );
		View title = lf.inflate( R.layout.title_connection, null );

		AlertDialog alertDialog = new AlertDialog.Builder( this.getActivity() ).setView( view ).setCustomTitle( title ).create();

		ButterKnife.bind(this, view);
		m_progressBar =(ProgressBar)title.findViewById(R.id.progress_bar);

		m_webview.loadUrl(Utility.urlContent("~/oauth/" + m_type + "?user_token=" + User.current.getAccessToken()));

		WebSettings settings = m_webview.getSettings();
		settings.setJavaScriptEnabled(true);
		m_webview.setWebChromeClient( new WebChromeClient() );

		m_webview.setWebViewClient(
			new WebViewClient()
			{
				@Override
				public boolean shouldOverrideUrlLoading( final WebView view, final String url )
				{
					if ( url.toLowerCase().contains( "/oauth/done" ) )
					{
						if ( m_callback != null )
						{ m_callback.success(); }
						dismiss();
						return true;
					}

					return super.shouldOverrideUrlLoading( view, url );
				}

				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					m_progressBar.setVisibility(View.VISIBLE);
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					m_progressBar.setVisibility(View.GONE);
				}
			}
		);

		return alertDialog;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		ButterKnife.unbind( this );
	}

	public void initialize( String type, AsyncCallback callback )
	{

		m_type = type;
		m_callback = callback;
	}

	@OnClick( R.id.btn_cancel )
	void cancel()
	{
		dismiss();
	}
}
