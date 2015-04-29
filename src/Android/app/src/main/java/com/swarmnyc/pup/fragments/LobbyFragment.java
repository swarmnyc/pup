package com.swarmnyc.pup.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.models.Lobby;

import javax.inject.Inject;
import java.nio.Buffer;

public class LobbyFragment extends Fragment
{
	public static String LOBBY_ID = "LobbyId";

	@Inject
	LobbyService lobbyService;

	@InjectView( R.id.text_name )
	TextView nameView;

	Lobby lobby;

	@Override
	public View onCreateView(
		final LayoutInflater inflater,
		@Nullable
		final ViewGroup container,
		@Nullable
		final Bundle savedInstanceState
	)
	{
		return inflater.inflate( R.layout.fragment_lobby, container, false );
	}

	@Override
	public void setArguments( final Bundle args )
	{
		super.setArguments( args );

	}

	@Override
	public void onViewCreated(
		final View view,
		@Nullable
		final Bundle savedInstanceState
	)
	{
		super.onViewCreated( view, savedInstanceState );
		PuPApplication.getInstance().getComponent().inject( this );
		ButterKnife.inject( this, view );

		lobbyService.getLobby(
			this.getArguments().getString( LOBBY_ID ), new ServiceCallback<Lobby>()
			{
				@Override
				public void success( final Lobby value )
				{
					lobby = value;
					nameView.setText( lobby.getName() );
				}
			}
		);
	}
}

