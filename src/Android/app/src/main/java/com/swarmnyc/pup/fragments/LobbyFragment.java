package com.swarmnyc.pup.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.*;
import android.view.*;
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

	private Lobby          m_lobby;
	private String         m_source;
	private String         m_lobbyName;
	private MemberFragment m_memberFragment;

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
		m_lobbyName = this.getArguments().getString( Consts.KEY_LOBBY_NAME );
		m_source = this.getArguments().getString( Consts.KEY_LOBBY_SOURCE );
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

		setHasOptionsMenu( true );
		DialogHelper.showProgressDialog( R.string.message_loading );
		lobbyService.getLobby(
			this.getArguments().getString( Consts.KEY_LOBBY_ID ), new ServiceCallback<Lobby>()
			{
				@Override
				public void success( final Lobby value )
				{
					m_lobby = value;
					m_memberFragment = new MemberFragment();
					m_memberFragment.setArguments( getArguments() );
					MainDrawerFragment.getInstance().setRightDrawer( m_memberFragment );
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
		MainActivity.getInstance().getToolbar().setTitle( m_lobby.getName() );

		//subtitle
		long offset = m_lobby.getStartTime().getTime() - TimeUtils.todayTimeMillis();
		String time;
		if ( offset < 0 )
		{
			SimpleDateFormat format = new SimpleDateFormat( "MMM dd @ h:m a", Locale.getDefault() );
			time = "Started " + format.format( m_lobby.getStartTime() );
		}
		else if ( offset < TimeUtils.day_in_millis )
		{
			SimpleDateFormat format = new SimpleDateFormat( "@ h:m a", Locale.getDefault() );
			time = "Today " + format.format( m_lobby.getStartTime() );
		}
		else if ( offset < TimeUtils.week_in_millis )
		{
			SimpleDateFormat format = new SimpleDateFormat( "EEEE @ h:m a", Locale.getDefault() );
			time = format.format( m_lobby.getStartTime() );
		}
		else
		{
			SimpleDateFormat format = new SimpleDateFormat( "MMM dd @ h:m a", Locale.getDefault() );
			time = format.format( m_lobby.getStartTime() );
		}

		Spanned subtitle = Html.fromHtml(
			String.format(
				"<small>%s: %s</small>", m_lobby.getPlatform(), time
			)
		);

		MainActivity.getInstance().getToolbar().setSubtitle( subtitle );
		MainActivity.getInstance()
		            .getToolbar()
		            .setSubtitleTextColor( getResources().getColor( R.color.pup_teal_light ) );

		//joinLobby button and text panel
		if ( User.isLoggedIn() )
		{
			LobbyUserInfo user = m_lobby.getUser( User.current.getId() );
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
		chatRoomService = chatService.getChatRoomService( getActivity(), m_lobby );
		chatList.setAdapter( new ChatAdapter( getActivity(), chatRoomService, m_lobby ) );
		LinearLayoutManager llm = new LinearLayoutManager( getActivity() );
		chatList.setLayoutManager( llm );
	}

	@OnClick( R.id.btn_send )
	void send()
	{
		String message = messageText.getText().toString().trim();
		if ( message.length() > 0 )
		{
			chatRoomService.SendMessage( message );
			messageText.setText( "" );
		}
	}

	@OnClick( R.id.btn_join )
	void joinLobby()
	{
		if ( User.isLoggedIn() )
		{
			DialogHelper.showProgressDialog( R.string.message_processing );
			lobbyService.join(
				m_lobby.getId(), new ServiceCallback()
				{
					@Override
					public void success( final Object value )
					{
						LobbyUserInfo user = new LobbyUserInfo();
						user.setId( User.current.getId() );
						user.setUserName( User.current.getUserName() );
						user.setPortraitUrl( User.current.getPortraitUrl() );

						m_lobby.getUsers().add( user );
						initialize();
						DialogHelper.hide();
					}
				}
			);
		}
		else
		{
			RegisterDialogFragment registerDialogFragment = new RegisterDialogFragment();
			registerDialogFragment.setGoHomeAfterLogin( false );
			registerDialogFragment.show( this.getFragmentManager(), null );
		}
	}

	@Override
	public void onCreateOptionsMenu( final Menu menu, final MenuInflater inflater )
	{
		inflater.inflate( R.menu.menu_lobby, menu );
	}

	@Override
	public boolean onOptionsItemSelected( final MenuItem item )
	{
		if ( item.getItemId()== R.id.menu_members ){
			MainDrawerFragment.getInstance().showRightDrawer();
			return true;
		}else {
			return super.onOptionsItemSelected( item );
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
	public void onStart()
	{
		super.onStart();
		MainDrawerFragment.getInstance().highLight( m_source );
	}

	@Override
	public void onStop()
	{
		super.onStop();
		EventBus.getBus().unregister( this );
		MainDrawerFragment.getInstance().removeRightDrawer(m_memberFragment);
	}

	@Override
	public String toString()
	{
		return "Lobby: " + m_lobbyName;
	}
}