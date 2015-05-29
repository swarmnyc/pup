package com.swarmnyc.pup.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.R;

public class MyChatsFragment extends Fragment
{

	public MyChatsFragment()
	{
	}

	@Override
	public String toString()
	{
		return "My Lobbies";
	}

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
	}

	@Override
	public View onCreateView(
		LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
	)
	{
		// Inflate the layout for this fragment
		return inflater.inflate( R.layout.fragment_my_chats, container, false );
	}

	@Override
	public void onStart()
	{
		super.onStart();
		MainDrawerFragment.getInstance().highLight( Consts.KEY_MY_LOBBIES );
	}
}
