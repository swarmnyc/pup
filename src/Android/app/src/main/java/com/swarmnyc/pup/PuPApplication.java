package com.swarmnyc.pup;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.swarmnyc.pup.chat.MessageService;
import com.uservoice.uservoicesdk.UserVoice;

public class PuPApplication extends Application
{
	private static PuPApplication instance;
	private Tracker m_tracker;
	private boolean m_messageServiceUp;
	private PuPApiModule module;

	public static PuPApplication getInstance()
	{
		return instance;
	}

	public PuPApiModule getModule()
	{
		return module;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		instance = this;
		Config.init( this );
		User.init();

		this.module = new PuPApiModule();

		ApiSettings.PuPServerPath = Config.getConfigString( R.string.PuP_Url );

		//Google
		GoogleAnalytics m_googleAnalytics = GoogleAnalytics.getInstance( this );
		m_googleAnalytics.setLocalDispatchPeriod( 1800 );

		m_tracker = m_googleAnalytics.newTracker( getString( R.string.google_tracker_key ) );
		m_tracker.enableExceptionReporting( true );

		//User Voice
		com.uservoice.uservoicesdk.Config config = new com.uservoice.uservoicesdk.Config(
			getString(
				R.string.uservoice_id
			)
		);
		config.setForumId( 272754 );
		UserVoice.init( config, this );
	}


	public boolean isMessageServiceUp()
	{
		return m_messageServiceUp;
	}

	public void setMessageServiceUp(boolean messageServiceUp )
	{
		m_messageServiceUp = messageServiceUp;
	}

	public void startMessageService(){
		if ( User.isLoggedIn() )
		{
			startService( new Intent( this, MessageService.class ) );
		}
	}

	public void sendScreenToTracker(String screenName){
		m_tracker.setScreenName( screenName );
		m_tracker.send( new HitBuilders.ScreenViewBuilder().build() );
	}

	public int getAppVersion()
	{
		try
		{
			PackageInfo packageInfo = getPackageManager().getPackageInfo( getPackageName(), 0 );
			return packageInfo.versionCode;
		}
		catch ( PackageManager.NameNotFoundException e )
		{
			// should never happen
			throw new RuntimeException( "Could not get package name: " + e );
		}
	}



}
