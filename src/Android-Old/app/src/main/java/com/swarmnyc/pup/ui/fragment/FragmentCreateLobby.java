package com.swarmnyc.pup.ui.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.*;

import com.google.gson.Gson;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.model.Lobby;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCreateLobby extends Fragment
{



	@Override
	public View onCreateView(
		LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
	)
	{
		// Inflate the layout for this fragment
		return inflater.inflate( R.layout.fragment_create_lobby, container, false );
	}



	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		getActivity().setTitle( "Create a Lobby" );


		setHasOptionsMenu( true );
	}

	@Override public void onCreateOptionsMenu( final Menu menu, final MenuInflater inflater )
	{
		//		if (!m_mFragmentNavigationDrawer.isDrawerOpen()) {
		// Only show items in the action bar relevant to this screen
		// if the drawer is not showing. Otherwise, let the drawer
		// decide what to show in the action bar.
		inflater.inflate( R.menu.create, menu );
		//		}
		super.onCreateOptionsMenu( menu, inflater );
	}

	@Override public boolean onOptionsItemSelected( final MenuItem item )
	{
		return super.onOptionsItemSelected( item );
	}

}
