package com.swarmnyc.pup.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.events.AfterLeaveLobbyEvent;
import com.swarmnyc.pup.events.LobbyUserChangeEvent;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.models.LobbyUserInfo;

import java.util.ArrayList;
import java.util.List;

public class MemberFragment extends Fragment
{
	private LobbyService m_lobbyService;

	@Bind( R.id.list_members ) RecyclerView  m_memberList;
	@Bind( R.id.btn_leave )    View          m_leaveButton;
	private                          Lobby         m_lobby;
	private                          MemberAdapter m_memberAdapter;

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
		m_lobbyService = PuPApplication.getInstance().getModule().provideLobbyService();
		ButterKnife.bind( this, view );

		m_memberAdapter = new MemberAdapter( this.getActivity(), new ArrayList<LobbyUserInfo>() );
		m_memberList.setAdapter( m_memberAdapter );
		m_memberList.setLayoutManager( new LinearLayoutManager( this.getActivity() ) );
	}

	public void setLobby( Lobby lobby )
	{
		m_lobby = lobby;

		refresh();
	}

	@OnClick( R.id.btn_leave )
	public void leaveLobby()
	{
		m_lobbyService.leave(
			m_lobby.getId(), new ServiceCallback<String>()
			{
				@Override
				public void success( final String value )
				{
					//showUndo();
					m_lobby.getUser( User.current.getId() ).setIsLeave( true );
					EventBus.getBus().post( new AfterLeaveLobbyEvent(m_lobby.getRoomId()) );
				}
			}
		);
	}

	@Override
	public void onStart()
	{
		super.onStart();
		EventBus.getBus().register( this );
	}

	@Override
	public void onStop()
	{
		super.onStop();
		EventBus.getBus().unregister( this );
	}

	@Subscribe
	public void postUserChanged( LobbyUserChangeEvent changed )
	{
		refresh();
	}

	private void refresh()
	{
		if ( this.isAdded() )
		{
			if ( !m_lobby.isAliveUser( User.current.getId() ) || m_lobby.isOwner( User.current.getId() ) )
			{
				m_leaveButton.setVisibility( View.GONE );
			} else {
				m_leaveButton.setVisibility( View.VISIBLE );
			}

			m_memberAdapter.setUsers( m_lobby.getUsers() );
		}
	}

	private class MemberViewHolder extends RecyclerView.ViewHolder
	{
		private LobbyUserInfo m_user;
		private Activity      m_activity;
		private ImageView     m_portraitView;
		private TextView      m_nameText;

		public MemberViewHolder( final Activity activity, final View itemView )
		{
			super( itemView );
			m_activity = activity;

			m_portraitView = (ImageView) itemView.findViewById( R.id.img_portrait );
			m_nameText = (TextView) itemView.findViewById( R.id.text_name );
		}

		public void setUser( final LobbyUserInfo user )
		{
			m_user = user;

			if ( StringUtils.isEmpty( m_user.getPortraitUrl() ) )
			{
				m_portraitView.setImageResource( R.drawable.default_portrait );
			}
			else
			{
				Picasso.with( m_activity ).load( m_user.getPortraitUrl() ).into( m_portraitView );
			}

			m_nameText.setText( user.getUserName() );
		}
	}


	private class MemberAdapter extends RecyclerView.Adapter<MemberViewHolder>
	{
		private final LayoutInflater m_inflater;
		private       Activity       m_activity;
		private List<LobbyUserInfo> m_users = new ArrayList<>();

		public MemberAdapter( final Activity activity, final List<LobbyUserInfo> users )
		{
			m_activity = activity;
			m_inflater = activity.getLayoutInflater();

			setUsers( users );

		}

		public void setUsers( final List<LobbyUserInfo> users )
		{
			m_users.clear();
			for ( LobbyUserInfo user : users )
			{
				if ( !user.getIsLeave() )
				{ m_users.add( user ); }
			}
			notifyDataSetChanged();
		}

		@Override
		public MemberViewHolder onCreateViewHolder( final ViewGroup parent, final int viewType )
		{
			return new MemberViewHolder( m_activity, m_inflater.inflate( R.layout.item_member, null ) );
		}

		@Override
		public void onBindViewHolder( final MemberViewHolder holder, final int position )
		{
			holder.setUser( m_users.get( position ) );
		}

		@Override
		public int getItemCount()
		{
			return m_users.size();
		}
	}
}
