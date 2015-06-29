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
import butterknife.InjectView;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.events.LobbyUserChangeEvent;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.models.LobbyUserInfo;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class MemberFragment extends Fragment
{
	@Inject                          LobbyService  m_lobbyService;

	@InjectView( R.id.list_members ) RecyclerView  m_memberList;
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
		PuPApplication.getInstance().getComponent().inject( this );
		ButterKnife.inject( this, view );


		m_memberAdapter = new MemberAdapter( this.getActivity(), new ArrayList<LobbyUserInfo>() );
		m_memberList.setAdapter( m_memberAdapter );
		m_memberList.setLayoutManager( new LinearLayoutManager( this.getActivity() ) );

	}

	public void setLobby( Lobby lobby )
	{
		m_lobby = lobby;

		refresh();
	}

	public void refresh()
	{
		if ( this.isAdded() )
		{ m_memberAdapter.setUsers( m_lobby.getUsers() ); }
	}

	@Override
	public void onStart()
	{
		super.onStart();
		EventBus.getBus().register( this );

		final String lobbyId = getActivity().getIntent().getStringExtra( Consts.KEY_LOBBY_ID );

		if (StringUtils.isNotEmpty( lobbyId ))

		m_lobbyService.getLobby(
			lobbyId, new ServiceCallback<Lobby>()
			{
				@Override
				public void success( final Lobby value )
				{
					if ( isAdded() )
					{
						setLobby( value );
					}
				}
			}
		);
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
