package com.swarmnyc.pup.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.swarmnyc.pup.PupApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.model.Lobby;
import com.swarmnyc.pup.service.TestLobbyService;
import com.swarmnyc.pup.ui.util.NavigationManager;
import com.swarmnyc.pup.ui.view.LobbyListItemView;
import dagger.ObjectGraph;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class FragmentLobbyList extends Fragment
{
	@Inject NavigationManager m_navigationManager;

	@InjectView( R.id.list_lobby ) RecyclerView        m_lobbyRecyclerView;
	private                        LobbyAdapter        m_lobbyAdapter;
	private                        LinearLayoutManager mLayoutManager;

	public FragmentLobbyList()
	{
		// Required empty public constructor
	}

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		final ObjectGraph objectGraph = PupApplication.instance().getObjectGraph();
		objectGraph.inject( this );

		getActivity().setTitle( "Party UP Player" );
	}

	@Override
	public View onCreateView(
		LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
	)
	{
		// Inflate the layout for this fragment
		final View view = inflater.inflate( R.layout.fragment_lobby_list, container, false );
		ButterKnife.inject( this, view );

		m_lobbyAdapter = new LobbyAdapter( getActivity() );
		m_lobbyRecyclerView.setAdapter( m_lobbyAdapter );

		mLayoutManager = new LinearLayoutManager( getActivity() );
		m_lobbyRecyclerView.setLayoutManager( mLayoutManager );

		m_lobbyRecyclerView.setItemAnimator( new LobbyItemAnimator() );


		return view;
	}

	@Override public void onStart()
	{
		TestLobbyService testLobbyService = new TestLobbyService();

		m_lobbyAdapter.setLobbies( testLobbyService.getLobbies() );
		super.onStart();
	}

	private class LobbyAdapter extends RecyclerView.Adapter<LobbyAdapter.ViewHolder>
	{
		private List<Lobby> m_lobbies = new ArrayList<Lobby>();
		Context m_context;

		private LobbyAdapter( final Context context )
		{
			m_context = context;
		}

		public void setLobbies( final List<Lobby> lobbies )
		{
			m_lobbies = lobbies;
			notifyDataSetChanged();
		}

		public List<Lobby> getLobbies()
		{
			return m_lobbies;
		}

		// Provide a reference to the views for each data item
		// Complex data items may need more than one view per item, and
		// you provide access to all the views for a data item in a view holder
		public class ViewHolder extends RecyclerView.ViewHolder
		{
			// each data item is just a string in this case
			public LobbyListItemView m_lobbyListItemView;

			public ViewHolder( LobbyListItemView v )
			{
				super( v );
				m_lobbyListItemView = v;
				m_lobbyListItemView.setOnClickListener(
					new View.OnClickListener()
					{
						@Override public void onClick( final View v )
						{
							m_navigationManager.showLobby( m_lobbyListItemView.getLobby() );
						}
					}
				);
			}
		}

		@Override public ViewHolder onCreateViewHolder(
			final ViewGroup viewGroup, final int i
		)
		{
			return new ViewHolder( new LobbyListItemView( m_context ) );
		}

		@Override public void onBindViewHolder( final ViewHolder viewHolder, final int i )
		{
			viewHolder.m_lobbyListItemView.setLobby( m_lobbies.get( i ) );
		}

		@Override public long getItemId( final int position )
		{
			return position;
		}

		@Override public int getItemCount()
		{
			return m_lobbies.size();
		}


	}

	private static class LobbyItemAnimator extends RecyclerView.ItemAnimator
	{
		@Override public void runPendingAnimations()
		{

		}

		@Override
		public boolean animateRemove(
			final RecyclerView.ViewHolder viewHolder
		)
		{
			return true;
		}

		@Override
		public boolean animateAdd( final RecyclerView.ViewHolder viewHolder )
		{
			return true;
		}

		@Override public boolean animateMove(
			final RecyclerView.ViewHolder viewHolder, final int i, final int i2, final int i3, final int i4
		)
		{
			return false;
		}

		@Override public boolean animateChange(
			final RecyclerView.ViewHolder viewHolder,
			final RecyclerView.ViewHolder viewHolder2,
			final int i,
			final int i2,
			final int i3,
			final int i4
		)
		{
			return false;
		}

		@Override
		public void endAnimation( final RecyclerView.ViewHolder viewHolder )
		{

		}

		@Override public void endAnimations()
		{

		}

		@Override public boolean isRunning()
		{
			return false;
		}
	}
}
