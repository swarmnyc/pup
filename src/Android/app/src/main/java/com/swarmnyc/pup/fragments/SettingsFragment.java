package com.swarmnyc.pup.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.User;

public class SettingsFragment extends Fragment
{
	public SettingsFragment()
	{
	}

	@OnClick( R.id.btn_logout )
	void logout()
	{
		User.Logout();
	}

	@Override
	public View onCreateView(
		LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
	)
	{
		// Inflate the layout for this fragment
		return inflater.inflate( R.layout.fragment_settings, container, false );
	}

	@Override
	public void onViewCreated( View view, Bundle savedInstanceState )
	{
		super.onViewCreated( view, savedInstanceState );
		ButterKnife.inject( this, view );
	}

	@Override
	public void onStart()
	{
		super.onStart();
		MainDrawerFragment.getInstance().highLight( Consts.KEY_SETTINGS );
	}
}
