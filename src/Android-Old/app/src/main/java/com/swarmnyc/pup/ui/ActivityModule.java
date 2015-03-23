package com.swarmnyc.pup.ui;

import android.app.Activity;
import com.swarmnyc.pup.ui.fragment.FragmentLobbyList;
import com.swarmnyc.pup.ui.fragment.FragmentNavigationDrawer;
import com.swarmnyc.pup.ui.util.NavigationManager;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Created by somya on 11/7/14.
 */
@Module( injects = {MainActivity.class, FragmentNavigationDrawer.class, FragmentLobbyList.class}, library = true )
public class ActivityModule
{
	private MainActivity m_mainActivity;

	public ActivityModule( final MainActivity mainActivity ) {m_mainActivity = mainActivity;}

	@Provides @Singleton Activity provideActivty()
	{
		return m_mainActivity;
	}

	@Provides @Singleton NavigationManager provideNavigationManager( Activity activity )
	{
		return new NavigationManager( activity );
	}
}
