package com.swarmnyc.pup.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.adapters.MyChatAdapter;
import com.swarmnyc.pup.components.Screen;
import com.swarmnyc.pup.view.DividerItemDecoration;

public class MyChatsFragment extends Fragment implements Screen
{
	@InjectView( R.id.list_chat )
	RecyclerView chatList;

	@Override
	public String toString()
	{
		return "My Lobbies";
	}

	@Override
	public View onCreateView(
		LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
	)
	{
		return inflater.inflate( R.layout.fragment_my_chats, container, false );
	}

	@Override
	public void onViewCreated( final View view, final Bundle savedInstanceState )
	{
		ButterKnife.inject( this, view );
		chatList.setAdapter( new MyChatAdapter( this.getActivity() ) );
		chatList.setLayoutManager( new LinearLayoutManager( this.getActivity() ) );
		chatList.addItemDecoration( new DividerItemDecoration( getActivity(), DividerItemDecoration.VERTICAL_LIST ) );
	}

	@Override
	public void onStart()
	{
		super.onStart();
		MainDrawerFragment.getInstance().highLight( Consts.KEY_MY_LOBBIES );
		MainActivity.getInstance().getToolbar().setTitle( R.string.text_lobbies );
		MainActivity.getInstance().getToolbar().setSubtitle( null );
	}
}
