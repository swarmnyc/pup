package com.swarmnyc.pup.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.Services.LobbyService;
import com.swarmnyc.pup.Services.ServiceCallback;
import com.swarmnyc.pup.adapters.LobbyChatAdapter;
import com.swarmnyc.pup.chat.ChatMessage;
import com.swarmnyc.pup.chat.ChatRoomService;
import com.swarmnyc.pup.chat.QuickbloxChatRoomService;
import com.swarmnyc.pup.components.*;
import com.swarmnyc.pup.events.LobbyUserChangeEvent;
import com.swarmnyc.pup.events.RequireChatHistoryEvent;
import com.swarmnyc.pup.events.UserChangedEvent;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.models.LobbyUserInfo;
import com.swarmnyc.pup.models.QBChatMessage2;
import com.swarmnyc.pup.models.UserInfo;
import com.swarmnyc.pup.view.DividerItemDecoration;
import com.swarmnyc.pup.view.ShareView;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class LobbyFragment extends BaseFragment implements Screen

{
	@Inject LobbyService m_lobbyService;

	ChatRoomService m_chatRoomService;

	@InjectView( R.id.backdrop )           ImageView               m_headerImage;
	@InjectView( R.id.collapsing_toolbar ) CollapsingToolbarLayout m_collapsingToolbarLayout;
	@InjectView( R.id.toolbar )            Toolbar                 m_toolbar;
	@InjectView( R.id.layout_coordinator ) CoordinatorLayout       m_coordinatorLayout;


	@InjectView( R.id.text_panel ) ViewGroup m_textPanel;

	@InjectView( R.id.btn_join ) TextView m_joinButton;

	@InjectView( R.id.edit_message ) EditText m_messageText;

	@InjectView( R.id.btn_send ) View m_sendButton;

	@InjectView( R.id.list_chat ) RecyclerView m_chatList;

	@InjectView( R.id.share_panel ) ShareView m_sharePanel;

	@InjectView( R.id.img_loading ) ImageView m_loadingImage;

	private String m_lobbyImage;

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
	public void onAttach( Activity activity )
	{
		super.onAttach( activity );
		final Bundle bundle = activity.getIntent().getExtras();
		loadFromBundle( bundle );
		//        MainDrawerFragment.getInstance().highLight(null);
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


		if ( StringUtils.isNotEmpty( m_lobbyImage ) )
		{
			Picasso.with( getActivity() ).load( m_lobbyImage ).centerCrop().fit().into(
				m_headerImage, new Callback()
				{
					@Override
					public void onSuccess()
					{
						ViewAnimationUtils.enterReveal( m_headerImage );
					}

					@Override
					public void onError()
					{

					}
				}
			);

		}

		if ( StringUtils.isNotEmpty( m_lobbyName ) )
		{
			m_collapsingToolbarLayout.setTitle( m_lobbyName );
		}

		getAppCompatActivity().setSupportActionBar( m_toolbar );

		m_lobby = null;
		m_memberFragment = null;
		m_lobbyChatAdapter = null;
		m_chatListLayoutManager = null;

		// Scroll down when Keyboard up
		SoftKeyboardHelper.setSoftKeyboardCallback(
			m_coordinatorLayout, new Action()
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
						//Log.d( "Touch", "Event:" + event );
						if ( Math.abs( m_touchDownX - event.getX() ) < Consts.TOUCH_SLOP && Math.abs(
							m_touchDownY - event.getY()
						) < Consts.TOUCH_SLOP )
						{
							//Log.d( "Touch", "Tap" );
							SoftKeyboardHelper.hideSoftKeyboard( getActivity() );
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
	}

	@Override
	public void onPause()
	{
		super.onStop();
		EventBus.getBus().unregister( this );
	}

	public void onDestroyView()
	{
		super.onDestroyView();

		SoftKeyboardHelper.removeSoftKeyboardCallback( m_coordinatorLayout );
		ButterKnife.reset( this );
	}

	@Override
	public String toString()
	{
		return "Lobby: " + m_lobbyName;
	}

	public void loadFromBundle( final Bundle args )
	{
		m_lobbyId = args.getString( Consts.KEY_LOBBY_ID );
		m_lobbyName = args.getString( Consts.KEY_LOBBY_NAME );
		m_lobbyImage = args.getString( Consts.KEY_LOBBY_IMAGE );
	}

	public void setLobby( final Lobby value )
	{
		m_lobby = value;

		m_chatList.setVisibility( View.VISIBLE );

		//title
		setTitle( m_lobby.getName() );

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

		setSubtitle( subtitle );

		initChatRoom();

		UnreadCounter.reset( m_lobby.getRoomId() );
	}

	private void initChatRoom()
	{
		ViewAnimationUtils.showWithAnimation( getActivity(), m_loadingImage );
		m_first = true;
		m_chatListLayoutManager = new LinearLayoutManager( getActivity() );
		m_lobbyChatAdapter = new LobbyChatAdapter( getActivity(), m_lobby );
		m_chatList.setAdapter( m_lobbyChatAdapter );
		m_chatList.setLayoutManager( m_chatListLayoutManager );
		m_chatList.addItemDecoration(
			new DividerItemDecoration(
				getActivity(), DividerItemDecoration.VERTICAL_LIST
			)
		);

		if ( m_lobby.isAliveUser( User.current.getId() ) )
		{
			// Get Data
			m_chatRoomService = new QuickbloxChatRoomService( getActivity(), m_lobby );
			m_chatRoomService.login();
			m_chatRoomService.loadChatHistory( 0 );
		}
		else
		{
			// Get DATA by Rest API
			m_lobbyService.getMessages(
				m_lobbyId, new ServiceCallback<List<QBChatMessage2>>()
				{
					@Override
					public void success( final List<QBChatMessage2> result )
					{
						List<ChatMessage> list = new ArrayList<ChatMessage>();

						for ( QBChatMessage2 message : result )
						{
							list.add(
								new ChatMessage(
									message.getUserId() == null ? null : new LobbyUserInfo( message.getUserId() ),
									message.get_id(),
									message.getMessage(),
									message.getDate_sent()
								)
							);
						}

						receiveMessage( new ChatMessageReceiveEvent( m_lobby.getRoomId(), false, list ) );
					}
				}
			);
		}
	}

	private void switchButton()
	{
		//Show or Hide joinLobby button
		if ( User.isLoggedIn() )
		{
			if ( m_lobby.isAliveUser( User.current.getId() ) )
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
			DialogHelper.showProgressDialog(getActivity(), R.string.message_processing );
			m_lobbyService.join(
				m_lobby.getId(), new ServiceCallback()
				{
					@Override
					public void success( final Object value )
					{
						addUserIntoLobby( User.current );
						EventBus.getBus().post( new LobbyUserChangeEvent() );
						initChatRoom();
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
	public void handleChatHistoryRequire( RequireChatHistoryEvent event )
	{
		m_chatRoomService.loadChatHistory( m_lobbyChatAdapter.getFirstChatMessage().getSentAt() );
	}

	@Subscribe
	public void receiveMessage( final ChatMessageReceiveEvent event )
	{
		if ( event.getRoomId().equals( m_lobby.getRoomId()))
		{
			//After receive history
			if ( m_first )
			{
				switchButton();
				ViewAnimationUtils.hideWithAnimation( getActivity(), m_loadingImage );
			}

			ArrayList<ChatMessage> messages = processSystemMessages( event );

			m_lastListScrollingTime = System.currentTimeMillis();
			int oldSize = m_lobbyChatAdapter.getItemCount();

			if ( event.isNewMessage() )
			{
				m_lobbyChatAdapter.addMessages( messages );

			}
			else
			{
				Collections.reverse( messages );
				m_lobbyChatAdapter.showLoadMore( messages.size() == Consts.PAGE_SIZE );
				m_lobbyChatAdapter.addMessages( 0, messages );
			}

			// Scrolling
			if ( event.isNewMessage() || m_first )
			{
				int size = m_lobbyChatAdapter.getItemCount();
				if ( size == 0 && m_lobby.isAliveUser( User.current.getId() ) )
				{
					//no message, show share item
					m_sharePanel.setVisibility( View.VISIBLE );
					m_sharePanel.setLobbyService( m_lobbyService );
					m_sharePanel.setLobby( m_lobby );
					m_sharePanel.setActivity( this.getActivity() );
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

			m_first = false;
		}
	}

	private ArrayList<ChatMessage> processSystemMessages( final ChatMessageReceiveEvent event )
	{
		ArrayList<ChatMessage> messages = (ArrayList<ChatMessage>) event.getMessages();
		if ( event.isNewMessage() && messages.size() == 1 )
		{
			// New System Message
			ChatMessage cm = messages.get( 0 );
			if ( cm.isSystemMessage() && cm.getCode() != null )
			{
				//Join and Left System messages.
				if ( cm.getCode().equals( "Join" ) && cm.getCodeBody() != null )
				{
					UserInfo[] users = Utility.fromJson( cm.getCodeBody(), UserInfo[].class );
					for ( UserInfo u : users )
					{
						addUserIntoLobby( u );
					}

					EventBus.getBus().post( new LobbyUserChangeEvent() );
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

					EventBus.getBus().post( new LobbyUserChangeEvent() );
				}
			}
		}

		return messages;
	}

	private void addUserIntoLobby( final UserInfo u )
	{
		LobbyUserInfo user = m_lobby.getUser( u.getId() );
		if ( user == null )
		{
			user = new LobbyUserInfo( u.getId() );
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

}