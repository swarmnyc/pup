package com.swarmnyc.pup.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.swarmnyc.pup.components.*;
import com.swarmnyc.pup.components.ViewAnimationUtils;
import com.swarmnyc.pup.events.LobbyUserChangeEvent;
import com.swarmnyc.pup.events.UserChangedEvent;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.models.LobbyUserInfo;
import com.swarmnyc.pup.models.UserInfo;
import com.swarmnyc.pup.view.DividerItemDecoration;
import com.swarmnyc.pup.view.ShareView;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class LobbyFragment extends Fragment implements Screen
{
	@Inject LobbyService m_lobbyService;

	@Inject ChatService m_chatService;

	ChatRoomService m_chatRoomService;

	@InjectView( R.id.text_panel ) ViewGroup m_textPanel;

	@InjectView( R.id.btn_join ) TextView m_joinButton;

	@InjectView( R.id.edit_message ) EditText m_messageText;

	@InjectView( R.id.btn_send ) View m_sendButton;

	@InjectView( R.id.list_chat ) RecyclerView m_chatList;

	@InjectView( R.id.share_panel ) ShareView m_sharePanel;

	@InjectView( R.id.img_loading ) ImageView m_loadingImage;

	private Lobby               m_lobby;
	private String              m_lobbyName;
	private String              m_lobbyId;
	private MemberFragment      m_memberFragment;
	private LobbyChatAdapter    m_lobbyChatAdapter;
	private LinearLayoutManager m_chatListLayoutManager;
	private boolean m_first = true;
	private long m_lastListScrollingTime;
	float m_touchDownX;
	float m_touchDownY;

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
		MainDrawerFragment.getInstance().highLight( null );

		m_first = true;
		m_lobby = null;
		m_lobbyId = getArguments().getString( Consts.KEY_LOBBY_ID );
		m_lobbyName = getArguments().getString( Consts.KEY_LOBBY_NAME );
		m_memberFragment = null;
		m_lobbyChatAdapter = null;
		m_chatListLayoutManager = null;

		MainActivity.getInstance().getToolbar().setTitle( m_lobbyName );

		// Scroll down when Keyboard up
		SoftKeyboardHelper.setSoftKeyboardCallback(
			new Action()
			{
				@Override
				public void call( final Object value )
				{
					if ( System.currentTimeMillis() - m_lastListScrollingTime < 1000 )
					{
						return;
					}

					m_lastListScrollingTime = System.currentTimeMillis();

					Log.d( "Scrolling", "After Keyboard up" );
					m_chatList.scrollToPosition( m_lobbyChatAdapter.getItemCount() - 1 );
				}
			}
		);

		//scrolling to end when has focus
		m_messageText.setOnFocusChangeListener( new HideKeyboardFocusChangedListener( getActivity() ) );

		//Tap to Hide SoftKeyboard
		m_chatList.setOnTouchListener(
			new View.OnTouchListener()
			{
				@Override
				public boolean onTouch( View v, MotionEvent event )
				{
					if ( event.getAction() == MotionEvent.ACTION_DOWN )
					{
						m_touchDownX = event.getX();
						m_touchDownY = event.getY();
					}
					else if ( event.getAction() == MotionEvent.ACTION_UP )
					{
						Log.d( "Touch", "Event:" + event );
						if ( Math.abs( m_touchDownX - event.getX() ) < Consts.TOUCH_SLOP && Math.abs(
							m_touchDownY
							- event.getY()
						) < Consts.TOUCH_SLOP )
						{
							Log.d( "Touch", "Tap" );
							SoftKeyboardHelper.hideSoftKeyboard();
						}
					}

					return false;
				}
			}
		);
	}

	@Override
	public void onResume()
	{
		super.onStart();
		EventBus.getBus().register( this );

		if ( m_lobby == null )
		{
			m_lobbyService.getLobby(
				m_lobbyId, new ServiceCallback<Lobby>()
				{
					@Override
					public void success( final Lobby value )
					{
						if ( isDetached() )
						{ return; }

						m_lobby = value;

						initialize();
					}
				}
			);
		}

		if ( m_chatRoomService != null )
		{
			m_chatRoomService.login( false );
		}

	}

	@Override
	public void onPause()
	{
		super.onStop();
		EventBus.getBus().unregister( this );

		if ( m_chatRoomService != null )
		{
			m_chatRoomService.leave();
		}
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		if ( m_memberFragment != null )
		{
			MainDrawerFragment.getInstance().removeRightDrawer( m_memberFragment );
		}

		SoftKeyboardHelper.removeSoftKeyboardCallback();
		ButterKnife.reset( this );
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

	private void initialize()
	{
		m_chatList.setVisibility( View.VISIBLE );

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
				"<small>%s: %s</small>",
				GamePlatformUtils.labelForPlatform( getActivity(), m_lobby.getPlatform() ),
				time
			)
		);

		MainActivity.getInstance().getToolbar().setSubtitle( subtitle );

		m_chatRoomService = m_chatService.getChatRoomService( getActivity(), m_lobby );

		m_chatListLayoutManager = new LinearLayoutManager( getActivity() );
		m_lobbyChatAdapter = new LobbyChatAdapter( getActivity(), m_lobby );
		m_chatList.setAdapter( m_lobbyChatAdapter );
		m_chatList.setLayoutManager( m_chatListLayoutManager );
		m_chatList.addItemDecoration(
			new DividerItemDecoration(
				getActivity(), DividerItemDecoration.VERTICAL_LIST
			)
		);

		m_memberFragment = new MemberFragment();
		m_memberFragment.setLobby( m_lobby );
		MainDrawerFragment.getInstance().setRightDrawer( m_memberFragment );

		//Join chat
		m_chatRoomService.login( true );
	}

	private void switchButton()
	{
		//Show or Hide joinLobby button
		if ( User.isLoggedIn() )
		{
			if ( m_lobby.isAliveUser( User.current.getId()) )
			{
				m_joinButton.setVisibility( View.GONE );
				m_textPanel.setVisibility( View.VISIBLE );
			}
			else
			{
				m_joinButton.setVisibility( View.VISIBLE );
				m_textPanel.setVisibility( View.GONE );
			}
		}
		else
		{
			m_joinButton.setVisibility( View.VISIBLE );
			m_textPanel.setVisibility( View.GONE );
		}
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
	public void postLobbyUserChanged( LobbyUserChangeEvent event )
	{
		//Once user join the lobby, refresh UI
		if ( event.isCurrentUser() )
		{
			m_chatRoomService.login( false );
			switchButton();
		}
	}

	@Subscribe
	public void receiveMessage( ChatMessageReceiveEvent event )
	{
		if ( !event.getLobbyId().equals( m_lobbyId ) )
		{ return; }

		//After receive history
		if ( m_first )
		{
			m_first = false;
			switchButton();
			ViewAnimationUtils.hideWithAnimation( getActivity(), m_loadingImage );
		}

		List<ChatMessage> messages = event.getMessages();
		if ( messages.size() == 1 )
		{
			ChatMessage cm = messages.get( 0 );
			if ( cm.isNewMessage() && cm.isSystemMessage() && cm.getCode() != null )
			{
				boolean isCurrentUser = false;
				//Join and Left System messages.
				if ( cm.getCode().equals( "Join" ) && cm.getCodeBody() != null )
				{
					UserInfo[] users = Utility.fromJson( cm.getCodeBody(), UserInfo[].class );
					for ( UserInfo u : users )
					{
						isCurrentUser = u.getId().equals( User.current.getId() );
						LobbyUserInfo user = m_lobby.getUser( u.getId() );
						if ( user == null )
						{
							user = new LobbyUserInfo();
							user.setId( u.getId() );
							user.setUserName( u.getUserName() );
							user.setPortraitUrl( u.getPortraitUrl() );
							m_lobby.getUsers().add( user );
						}
						else
						{
							//rejoin
							user.setIsLeave( false );
						}
					}

					EventBus.getBus().post( new LobbyUserChangeEvent( isCurrentUser ) );
				}
				else if ( cm.getCode().equals( "Leave" ) && cm.getCodeBody() != null )
				{
					UserInfo[] users = Utility.fromJson( cm.getCodeBody(), UserInfo[].class );
					for ( UserInfo u : users )
					{
						LobbyUserInfo user = m_lobby.getUser( u.getId() );
						if ( user != null )
						{
							//rejoin
							user.setIsLeave( true );
						}
					}

					EventBus.getBus().post( new LobbyUserChangeEvent( false ) );
				}
			}
		}

		// Scrolling
		m_lastListScrollingTime = System.currentTimeMillis();
		int oldSize = m_lobbyChatAdapter.getItemCount();

		m_lobbyChatAdapter.addMessages( messages );

		int size = m_lobbyChatAdapter.getItemCount();
		if ( size == LobbyChatAdapter.FixedItem && m_lobby.isAliveUser( User.current.getId() ) )
		{
			m_sharePanel.setVisibility( View.VISIBLE );
			m_sharePanel.setLobbyService( m_lobbyService );
			m_sharePanel.setLobby( m_lobby );
		}
		else
		{
			m_sharePanel.setVisibility( View.GONE );
			int lastPosition = m_chatListLayoutManager.findLastVisibleItemPosition();
			if ( Math.abs( oldSize - lastPosition ) < 3 ) //it almost is on the end, scrolling
			{
				Log.d( "Scrolling", "After Receive Message" );
				m_chatList.post(
					new Runnable()
					{
						@Override
						public void run()
						{
							m_chatList.scrollToPosition( m_lobbyChatAdapter.getItemCount() - 1 );
						}
					}
				);
			}
		}

	}

}