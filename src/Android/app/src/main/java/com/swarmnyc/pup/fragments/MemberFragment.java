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
import com.swarmnyc.pup.models.Lobby;

public class MemberFragment extends Fragment
{
	@Override
	public View onCreateView(
		LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
	)
	{
		return inflater.inflate( R.layout.fragment_member, container, false );
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

	@Override
	public String toString()
	{
		return "Settings";
	}
}
