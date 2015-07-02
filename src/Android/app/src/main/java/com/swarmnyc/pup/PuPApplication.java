package com.swarmnyc.pup;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.swarmnyc.pup.chat.MessageService;

public class PuPApplication extends Application
{
	private static PuPApplication instance;
	private        PuPComponent   component;

	public static PuPApplication getInstance()
	{
		return instance;
	}

	public PuPComponent getComponent()
	{
		return component;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		instance = this;
		Config.init( this );
		User.init();

		this.component = DaggerPuPComponent.builder().build();

		ApiSettings.PuPServerPath = Config.getConfigString( R.string.PuP_Url );
	}


	public void startMessageService(){
		if ( User.isLoggedIn() )
		{
			startService( new Intent( this, MessageService.class ) );
		}
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
