package com.swarmnyc.pup.ui;

import android.app.Activity;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import com.parse.ParseAnalytics;
import com.swarmnyc.pup.PupApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.ui.fragment.FragmentLobbyList;
import com.swarmnyc.pup.ui.fragment.FragmentNavigationDrawer;
import dagger.ObjectGraph;


public class MainActivity extends Activity
        implements FragmentNavigationDrawer.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private FragmentNavigationDrawer m_mFragmentNavigationDrawer;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		final ObjectGraph plus = PupApplication.instance().plus( new ActivityModule( this ) );
		plus.inject( this );

		super.onCreate( savedInstanceState );

		ParseAnalytics.trackAppOpened( getIntent() );

		setContentView( R.layout.activity_main );

		m_mFragmentNavigationDrawer = (FragmentNavigationDrawer) getFragmentManager().findFragmentById( R.id
			                                                                                                .navigation_drawer );
		mTitle = getTitle();

		// Set up the drawer.
		m_mFragmentNavigationDrawer.setUp(
			R.id.navigation_drawer, (DrawerLayout) findViewById( R.id.drawer_layout )
		);

		if (null == savedInstanceState)
		{
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
			               .replace(R.id.container, new FragmentLobbyList())
			               .commit();
		}
	}

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!m_mFragmentNavigationDrawer.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void setTitle( final CharSequence mTitle )
	{
		this.mTitle = mTitle;
	}
}
