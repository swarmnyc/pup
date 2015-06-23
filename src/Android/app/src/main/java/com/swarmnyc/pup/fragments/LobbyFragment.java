package com.swarmnyc.pup.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
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
import com.swarmnyc.pup.adapters.LobbyChatAdapter;
import com.swarmnyc.pup.chat.ChatMessage;
import com.swarmnyc.pup.chat.ChatRoomService;
import com.swarmnyc.pup.chat.ChatService;
import com.swarmnyc.pup.components.DialogHelper;
import com.swarmnyc.pup.components.GamePlatformUtils;
import com.swarmnyc.pup.components.Screen;
import com.swarmnyc.pup.components.Utility;
import com.swarmnyc.pup.events.LobbyUserChangeEvent;
import com.swarmnyc.pup.events.UserChangedEvent;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.models.LobbyUserInfo;
import com.swarmnyc.pup.models.UserInfo;
import com.swarmnyc.pup.view.DividerItemDecoration;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class LobbyFragment extends Fragment implements Screen
{
	@Inject
	LobbyService m_lobbyService;

	@Inject
	ChatService m_chatService;

	ChatRoomService m_chatRoomService;

	@InjectView( R.id.container )
	View m_container;

	@InjectView( R.id.panel )
	ViewGroup m_textPanel;

	@InjectView( R.id.btn_join )
	TextView m_joinButton;

	@InjectView( R.id.text_message )
	EditText m_messageText;

	@InjectView( R.id.btn_send )
	View m_sendButton;

	@InjectView( R.id.list_chat )
	RecyclerView m_chatList;

	private Lobby          m_lobby;
	private String         m_lobbyName;
	private MemberFragment m_memberFragment;
	private String         m_lobbyId;
	private LobbyChatAdapter m_Lobby_chatAdapter;
	private boolean m_isloaded;

	private void initialize()
	{
		m_container.setVisibility( View.VISIBLE );

		//title
		MainActivity.getInstance().getToolbar().setTitle( m_lobby.getName() );

		//subtitle
		long offset = m_lobby.getStartTime().getTime() - TimeUtils.todayTimeMillis();
		String time;
		if ( offset < 0 )
		{
			SimpleDateFormat format = new SimpleDateFormat( "MMM dd @ h:mm a", Locale.getDefault() );
			time = "Started " + format.format( m_lobby.getStartTime() );
		}
		else if ( offset < TimeUtils.day_in_millis )
		{
			SimpleDateFormat format = new SimpleDateFormat( "@ h:mm a", Locale.getDefault() );
			time = "Today " + format.format( m_lobby.getStartTime() );
		}
		else if ( offset < TimeUtils.week_in_millis )
		{
			SimpleDateFormat format = new SimpleDateFormat( "EEEE @ h:mm a", Locale.getDefault() );
			time = format.format( m_lobby.getStartTime() );
		}
		else
		{
			SimpleDateFormat format = new SimpleDateFormat( "MMM dd @ h:mm a", Locale.getDefault() );
			time = format.format( m_lobby.getStartTime() );
		}

		Spanned subtitle = Html.fromHtml(
				String.format(
						"<small>%s: %s</small>", GamePlatformUtils.labelForPlatform( getActivity(), m_lobby.getPlatform() ), time
				)
		);

		MainActivity.getInstance().getToolbar().setSubtitle( subtitle );

		m_chatRoomService = m_chatService.getChatRoomService(getActivity(), m_lobby);

		m_Lobby_chatAdapter = new LobbyChatAdapter( getActivity(), m_lobby );
		m_chatList.setAdapter(m_Lobby_chatAdapter);
		m_chatList.setLayoutManager( new LinearLayoutManager( getActivity() ) );
		m_chatList.addItemDecoration(
				new DividerItemDecoration(
						getActivity(), DividerItemDecoration.VERTICAL_LIST
				)
		);

		m_memberFragment = new MemberFragment();
		m_memberFragment.setLobby( m_lobby );
		MainDrawerFragment.getInstance().setRightDrawer( m_memberFragment );

		initialize2( true );
	}

	private void initialize2( boolean loadHistory )
	{
		//joinLobby button and text panel
		if ( User.isLoggedIn() )
		{
			LobbyUserInfo user = m_lobby.getDwellingUser( User.current.getId() );
			if ( user == null )
			{
				m_joinButton.setVisibility( View.VISIBLE );
				m_textPanel.setVisibility( View.GONE );
			}
			else
			{
				m_joinButton.setVisibility( View.GONE );
				m_textPanel.setVisibility( View.VISIBLE );
			}
		}
		else
		{
			m_joinButton.setVisibility( View.VISIBLE );
			m_textPanel.setVisibility( View.GONE );
		}

		m_chatRoomService.login(loadHistory);
	}


	@Override
	public void setArguments( final Bundle args )
	{
		super.setArguments( args );
		m_lobbyId = args.getString( Consts.KEY_LOBBY_ID );
		m_lobbyName = args.getString( Consts.KEY_LOBBY_NAME );
	}

	@Override
	public View onCreateView(
		final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState
	)
	{
		return inflater.inflate( R.layout.fragment_lobby, container, false );
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

		setHasOptionsMenu( true );
		DialogHelper.showProgressDialog( R.string.message_loading );

		m_lobbyService.getLobby(
			m_lobbyId, new ServiceCallback<Lobby>()
			{
				@Override
				public void success( final Lobby value )
				{
					DialogHelper.hide();

					if (isDetached())
						return;

					m_lobby = value;

					initialize();
				}
			}
		);
	}

	@Override
	public void onResume()
	{
		super.onStart();
		EventBus.getBus().register( this );
	}

	@Override
	public void onPause()
	{
		super.onStop();
		EventBus.getBus().unregister( this );
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		MainDrawerFragment.getInstance().highLight( null );
	}

	@Override
	public void onDetach() {
		super.onDetach();

		if (m_memberFragment!=null){
			MainDrawerFragment.getInstance().removeRightDrawer( m_memberFragment );
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
		if ( item.getItemId() == R.id.menu_members )
		{
			MainDrawerFragment.getInstance().showRightDrawer();
			return true;
		}
		else
		{
			return super.onOptionsItemSelected( item );
		}
	}

	@Override
	public String toString()
	{
		return "Lobby: " + m_lobbyName;
	}

	@OnClick( R.id.btn_send )
	void send()
	{
		String message = m_messageText.getText().toString().trim();
		if ( message.length() > 0 )
		{
			m_chatRoomService.SendMessage( message );
		}

		m_messageText.setText( "" );
	}

	@OnClick( R.id.btn_join )
	void joinLobby()
	{
		if ( User.isLoggedIn() )
		{
			DialogHelper.showProgressDialog( R.string.message_processing );
			m_lobbyService.join(
					m_lobby.getId(), new ServiceCallback()
					{
						@Override
						public void success( final Object value )
						{
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

	@Subscribe
	public void postUserChanged( UserChangedEvent event )
	{
		if ( User.isLoggedIn() )
		{
			joinLobby();
		}
	}

	@Subscribe
	public void postLobbyUserChanged(LobbyUserChangeEvent event)
	{
		//Once user join the lobby, refresh UI
		if ( event.isCurrentUser() )
		{
			initialize2( false );
		}
	}

	@Subscribe
	public void receiveMessage(ChatMessageReceiveEvent event) {
		if (!event.getLobbyId().equals(m_lobbyId))
			return;

		//TODO LOAD
		m_isloaded = true;
		List<ChatMessage> messages = event.getMessages();
		if (messages.size() == 1) {
			ChatMessage cm = messages.get(0);
			if (cm.isNewMessage() && cm.isSystemMessage() && cm.getCode() != null) {
				boolean isCurrentUser = false;
				//Join and Left System messages.
				if (cm.getCode().equals("Join") && cm.getCodeBody() != null) {
					UserInfo[] users = Utility.fromJson(cm.getCodeBody(), UserInfo[].class);
					for (UserInfo u : users) {
						isCurrentUser = u.getId().equals(User.current.getId());
						LobbyUserInfo user = m_lobby.getUser(u.getId());
						if (user == null) {
							user = new LobbyUserInfo();
							user.setId(u.getId());
							user.setUserName(u.getUserName());
							user.setPortraitUrl(u.getPortraitUrl());
							m_lobby.getUsers().add(user);
						} else {
							//rejoin
							user.setIsLeave(false);
						}
					}

					EventBus.getBus().post(new LobbyUserChangeEvent(isCurrentUser));
				} else if (cm.getCode().equals("Leave") && cm.getCodeBody() != null) {
					UserInfo[] users = Utility.fromJson(cm.getCodeBody(), UserInfo[].class);
					for (UserInfo u : users) {
						LobbyUserInfo user = m_lobby.getUser(u.getId());
						if (user != null) {
							//rejoin
							user.setIsLeave(true);
						}
					}

					EventBus.getBus().post(new LobbyUserChangeEvent(false));
				}
			}
		}
		m_Lobby_chatAdapter.addMessages(messages);

		//TODO: Better Scrolling
		m_chatList.smoothScrollToPosition(m_Lobby_chatAdapter.getItemCount());
	}
}