package com.swarmnyc.pup.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.Services.Filter.LobbyFilter;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.adapters.MyChatAdapter;
import com.swarmnyc.pup.components.Action;
import com.swarmnyc.pup.components.Screen;
import com.swarmnyc.pup.components.UnreadCounter;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.view.DividerItemDecoration;

import javax.inject.Inject;
import java.util.List;

public class MyChatsFragment extends BaseFragment implements Screen
{
	@Inject LobbyService m_lobbyService;

	@InjectView( R.id.list_chat ) RecyclerView m_chatList;

	private boolean       m_noMoreData;
	private int           pageIndex;
	private MyChatAdapter m_myChatAdapter;
	private Lobby         m_removedLobby;

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
		PuPApplication.getInstance().getComponent().inject( this );
		pageIndex = 0;
		m_noMoreData = false;
		m_myChatAdapter = new MyChatAdapter( this.getActivity() );
		m_myChatAdapter.setReachEndAction(
			new Action()
			{
				@Override
				public void call( Object value )
				{
					fetchMoreData();
				}
			}
		);
		m_chatList.setAdapter( m_myChatAdapter );
		m_chatList.setLayoutManager( new LinearLayoutManager( this.getActivity() ) );
		m_chatList.addItemDecoration( new DividerItemDecoration( getActivity(), DividerItemDecoration.VERTICAL_LIST ) );
	}

	@Override
	public void onStart()
	{
		super.onStart();

		fetchMoreData();
	}


	@Override
	public void onResume()
	{
		super.onResume();
		EventBus.getBus().register( this );
	}

	@Override
	public void onPause()
	{
		super.onPause();
		EventBus.getBus().unregister( this );
	}

	public void updateTitle()
	{
		setTitle( getString( R.string.text_lobbies) + " (" + UnreadCounter.total() + ")" );
		setSubtitle( null );
	}

	private void fetchMoreData()
	{
		if ( m_noMoreData )
		{
			return;
		}

		LobbyFilter filter = new LobbyFilter();
		filter.setPageIndex( pageIndex++ );
		m_lobbyService.getMyLobbies(
			filter, new ServiceCallback<List<Lobby>>()
			{
				@Override
				public void success( final List<Lobby> value )
				{
					if ( !isAdded() )
					{ return; }

					if ( value.size() == 0 )
					{
						m_noMoreData = true;
					}
					else
					{
						if ( value.size() < Consts.PAGE_SIZE )
						{
							m_noMoreData = true;
						}

						m_myChatAdapter.AddLobbies( value );
					}
				}
			}
		);
	}

	private void showUndo()
	{
		Snackbar snackbar = Snackbar.make( m_chatList, R.string.message_leave_room, Snackbar.LENGTH_LONG );
		snackbar.setAction(
			R.string.text_undo, new View.OnClickListener()
			{
				@Override
				public void onClick( final View v )
				{
					m_lobbyService.join(
						m_removedLobby.getId(), new ServiceCallback()
						{
							@Override
							public void success( final Object value )
							{
								m_myChatAdapter.AddLobby( m_removedLobby );
							}
						}
					);
				}
			}
		);
		snackbar.getView().setPadding( 0, 30, 0, 30 );
		snackbar.setActionTextColor( getResources().getColor( R.color.pup_white ) );
		snackbar.getView().setBackgroundResource( R.color.pup_orange );
		snackbar.show();
	}

	@Subscribe
	public void receiveMessage( final ChatMessageReceiveEvent event )
	{
		if ( !event.isNewMessage() )
			return;

		updateTitle();
		m_myChatAdapter.updateLastMessage(event);
	}
}
