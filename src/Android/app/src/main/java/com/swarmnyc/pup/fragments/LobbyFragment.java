package com.swarmnyc.pup.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.events.UserChangedEvent;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.models.LobbyUserInfo;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class LobbyFragment extends Fragment
{
	public static String LOBBY_ID = "LobbyId";

	@Inject
	LobbyService lobbyService;

	@InjectView( R.id.container )
	View container;

	@InjectView( R.id.btn_join )
	TextView joinButton;

	@InjectView( R.id.img_game )
	ImageView gameImageView;

	@InjectView( R.id.text_name )
	TextView lobbyNameText;

	@InjectView( R.id.text_lobby_type )
	TextView lobbyTypeText;

	@InjectView( R.id.text_description )
	TextView lobbyDescriptionText;

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
		EventBus.getBus().register( this );

		//TODO: Loading Message
		lobbyService.getLobby(
			this.getArguments().getString( LOBBY_ID ), new ServiceCallback<Lobby>()
			{
				@Override
				public void success( final Lobby value )
				{
					lobby = value;
					initialize();
				}
			}
		);
	}

	private void initialize()
	{
		container.setVisibility( View.VISIBLE );

		//title
		MainActivity.getInstance().getToolbar().setTitle( lobby.getName() );

		//subtitle
		long offset = lobby.getStartTime().getTime() - TimeUtils.todayTimeMillis();
		String time;
		if ( offset < 0 )
		{
			SimpleDateFormat format = new SimpleDateFormat( "MMM dd @ h:m a", Locale.getDefault() );
			time = "Started " + format.format( lobby.getStartTime() );
		}
		else if ( offset < TimeUtils.day_in_millis )
		{
			SimpleDateFormat format = new SimpleDateFormat( "@ h:m a", Locale.getDefault() );
			time = "Today " + format.format( lobby.getStartTime() );
		}
		else if ( offset < TimeUtils.week_in_millis )
		{
			SimpleDateFormat format = new SimpleDateFormat( "EEEE @ h:m a", Locale.getDefault() );
			time = format.format( lobby.getStartTime() );
		}
		else
		{
			SimpleDateFormat format = new SimpleDateFormat( "MMM dd @ h:m a", Locale.getDefault() );
			time = format.format( lobby.getStartTime() );
		}

		Spanned subtitle = Html.fromHtml(
			String.format(
				"<small>%s: %s</small>", lobby.getPlatform(), time
			)
		);

		MainActivity.getInstance().getToolbar().setSubtitle( subtitle );
		MainActivity.getInstance()
		            .getToolbar()
		            .setSubtitleTextColor( getResources().getColor( R.color.pup_teal_light ) );

		//name
		lobbyNameText.setText( lobby.getOwner().getName() + "'s\n" + lobby.getName() );

		//img
		if ( StringUtils.isNotEmpty( lobby.getPictureUrl() ) )
		{
			Picasso.with( getActivity() ).load( lobby.getPictureUrl() ).centerCrop().fit().into( gameImageView );
		}

		//type
		lobbyTypeText.setText( String.format( "%s,%s", lobby.getPlayStyle(), lobby.getSkillLevel() ) );

		//description
		lobbyDescriptionText.setText( lobby.getDescription() );

		//join button
		if ( User.isLoggedIn() )
		{
			LobbyUserInfo user = lobby.getUser( User.current.getId() );
			if ( user == null )
			{
				joinButton.setVisibility( View.VISIBLE );
			}
			else
			{
				joinButton.setVisibility( View.GONE );
			}
		}
		else
		{
			joinButton.setVisibility( View.VISIBLE );
		}

	}

	@OnClick( R.id.btn_join )
	void join()
	{
		if ( User.isLoggedIn() )
		{
			joinLobby();
		}
		else
		{
			SignupFragment signupFragment = new SignupFragment();
			signupFragment.setGoHomeAfterLogin( false );
			signupFragment.show( this.getFragmentManager(), null );
		}
	}

	private void joinLobby()
	{//TODO: Processing
		lobbyService.join(
			lobby.getId(), new ServiceCallback()
			{
				@Override
				public void success( final Object value )
				{
					LobbyUserInfo user = new LobbyUserInfo();
					user.setId( User.current.getId() );
					user.setName( User.current.getUserName() );
					user.setPictureUrl( User.current.getPictureUrl() );

					lobby.getUsers().add( user );
					initialize();
				}
			}
		);
	}

	@Subscribe
	public void postUserChanged( UserChangedEvent event )
	{
		if ( User.isLoggedIn() )
		{
			joinLobby();
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		EventBus.getBus().unregister( this );
	}
}