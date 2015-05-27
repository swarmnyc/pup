package com.swarmnyc.pup.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.adapters.ChatAdapter;
import com.swarmnyc.pup.chat.ChatRoomService;
import com.swarmnyc.pup.chat.ChatService;
import com.swarmnyc.pup.components.DialogHelper;
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

	@Inject
	ChatService chatService;

	ChatRoomService chatRoomService;

	@InjectView( R.id.container )
	View container;

	@InjectView( R.id.panel )
	ViewGroup textPanel;

	@InjectView( R.id.btn_join )
	TextView joinButton;

	@InjectView( R.id.text_message )
	EditText messageText;

	@InjectView( R.id.btn_send )
	View sendButton;

	@InjectView( R.id.list_chat )
	RecyclerView chatList;

	Lobby lobby;

	@Override
	public View onCreateView(
		final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState
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

		DialogHelper.showProgressDialog( R.string.text_loding );
		lobbyService.getLobby(
			this.getArguments().getString( LOBBY_ID ), new ServiceCallback<Lobby>()
			{
				@Override
				public void success( final Lobby value )
				{
					lobby = value;
					initialize();
					DialogHelper.hide();
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

		//joinLobby button and text panel
		if ( User.isLoggedIn() )
		{
			LobbyUserInfo user = lobby.getUser( User.current.getId() );
			if ( user == null )
			{
				joinButton.setVisibility( View.VISIBLE );
				textPanel.setVisibility( View.GONE );
			}
			else
			{
				joinButton.setVisibility( View.GONE );
				textPanel.setVisibility( View.VISIBLE );
			}
		}
		else
		{
			joinButton.setVisibility( View.VISIBLE );
			textPanel.setVisibility( View.GONE );
		}

		//chat list
		if ( chatList.getAdapter() == null )
		{
			chatRoomService = chatService.getChatRoomService( getActivity(), lobby );

			chatList.setAdapter( new ChatAdapter( getActivity(), chatRoomService , lobby ) );
			LinearLayoutManager llm = new LinearLayoutManager( getActivity() );
			//llm.setStackFromEnd( true );
			chatList.setLayoutManager(  llm );
		}
	}

	@OnClick(R.id.btn_send)
	void send(){
		String message = messageText.getText().toString().trim();
		if ( message.length()>0 ){
			chatRoomService.SendMessage( message );
			messageText.setText("");
		}
	}

	@OnClick( R.id.btn_join )
	void joinLobby()
	{
		if ( User.isLoggedIn() )
		{
			DialogHelper.showProgressDialog( R.string.text_processing );
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
						DialogHelper.hide();
					}
				}
			);
		}
		else
		{
			RegisterFragment registerFragment = new RegisterFragment();
			registerFragment.setGoHomeAfterLogin( false );
			registerFragment.show( this.getFragmentManager(), null );
		}
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