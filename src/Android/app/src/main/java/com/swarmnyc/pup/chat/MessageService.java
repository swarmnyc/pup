package com.swarmnyc.pup.chat;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.support.annotation.NonNull;
import android.util.Log;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChat;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChat;
import com.quickblox.chat.listeners.QBMessageListenerImpl;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBSettings;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;
import com.squareup.otto.Subscribe;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.components.UnreadCounter;
import com.swarmnyc.pup.events.AfterLeaveLobbyEvent;
import com.swarmnyc.pup.events.ChatMessageReceiveEvent;
import com.swarmnyc.pup.models.LobbyUserInfo;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessageService extends Service
{
	private static final String TAG = MessageService.class.getSimpleName();
	private MessageListener m_listener;
	private AtomicBoolean                   m_trying         = new AtomicBoolean( false );
	private Queue<EnsureChatConnectRequest> m_ensureRequests = new ArrayBlockingQueue<EnsureChatConnectRequest>( 10 );
	private long                            m_expiredAt      = 0;
	private Handler                         m_handler        = new Handler( Looper.getMainLooper() );

	@Override
	public void onCreate()
	{
		Log.d( TAG, "onCreate" );

		EventBus.getBus().register( this );

		QBChatService.setDebugEnabled( BuildConfig.ENABLE_LOG_CHAT );
		QBSettings.getInstance().fastConfigInit(
			Config.getConfigString( R.string.QB_APP_ID ),
			Config.getConfigString( R.string.QB_APP_KEY ),
			Config.getConfigString( R.string.QB_APP_SECRET )
		);

		if ( !QBChatService.isInitialized() )
		{
			QBChatService.init( this );

			QBChatService.getInstance().addConnectionListener(
				new ConnectionListener()
				{

					@Override
					public void connected(
						final XMPPConnection xmppConnection
					)
					{
						Log.d( TAG, "connected" );
					}

					@Override
					public void authenticated( final XMPPConnection xmppConnection )
					{
						Log.d( TAG, "authenticated:" + xmppConnection );
					}

					@Override
					public void connectionClosed()
					{
						Log.d( TAG, "connectionClosed" );
					}

					@Override
					public void connectionClosedOnError( final Exception e )
					{
						Log.d( TAG, "connectionClosedOnError:" + e );
					}

					@Override
					public void reconnectingIn( final int i )
					{
						Log.d( TAG, "reconnectingIn:" + i );
					}

					@Override
					public void reconnectionSuccessful()
					{
						Log.d( TAG, "reconnectionSuccessful" );
					}

					@Override
					public void reconnectionFailed( final Exception e )
					{
						Log.d( TAG, "reconnectionFailed:" + e );
					}
				}
			);
		}

		m_listener = new MessageListener();
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		Log.d( TAG, "onStartCommand:" + intent + ", Flags:" + flags + ", startId:" + startId );
		processChatRooms();
		PuPApplication.getInstance().setMessageServiceUp( true );

		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind( Intent intent )
	{
		Log.d( TAG, "onBind:" + intent );
		return null;
	}

	@Override
	public void onDestroy()
	{
		Log.d( TAG, "onDestroy" );
	}

	@Subscribe
	public void handleLeaveRoom( AfterLeaveLobbyEvent event )
	{
		try
		{
			String jId = generateJId( event.getRoomId() );

			QBGroupChat chat = QBChatService.getInstance().getGroupChatManager().getGroupChat( jId );
			if ( chat.isJoined() )
			{
				chat.leave();
			}
		}
		catch ( Exception e )
		{
			Log.e( TAG, "Leave failed", e );
		}
	}

	@Subscribe
	public void addEnsureRequest( EnsureChatConnectRequest request )
	{
		m_ensureRequests.add( request );
		processChatRooms();
	}

	public void processEnsureRequests()
	{
		EnsureChatConnectRequest request;
		if ( QBChatService.getInstance().getGroupChatManager() == null )
		{
			Log.e(TAG, "GroupChatManager is null");
			return;
		}

		while ( ( request = m_ensureRequests.poll() ) != null )
		{
			String jid = generateJId( request.getRoomId() );

			QBGroupChat chat = QBChatService.getInstance().getGroupChatManager().getGroupChat( jid );

			if ( chat == null )
			{
				chat = QBChatService.getInstance().getGroupChatManager().createGroupChat( jid );
			}

			startListener( chat );

			m_handler.post( request.getCallback() );
		}
	}

	@NonNull
	public static String generateJId( final String roomId )
	{
		return Config.getConfigString( R.string.QB_APP_ID ) +
		       "_" +
		       roomId +
		       "@muc.chat.quickblox.com";
	}

	private void processChatRooms()
	{
		boolean live = m_expiredAt > System.currentTimeMillis();

		if ( m_trying.get() && live  )
		{
			Log.d( TAG, "Still connected so skip" );
			processEnsureRequests();
			return;
		}

		m_trying.set( true );
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected Void doInBackground( final Void[] params )
			{
				try
				{
					if ( QBChatService.getInstance().isLoggedIn() )
					{
						QBChatService.getInstance().logout();
					}

					QBUser user = new QBUser();
					user.setLogin( User.current.getId() );
					user.setPassword( "swarmnyc" );

					QBSession session = QBAuth.createSession( user );
					user.setId( session.getUserId() );
					QBChatService.getInstance().login( user );
					m_expiredAt = System.currentTimeMillis() + ( TimeUtils.minute_in_millis * 19 );

					QBRequestGetBuilder request = new QBRequestGetBuilder();
					request.setPagesLimit( 10 );

					final ArrayList<QBDialog> result = QBChatService.getChatDialogs(
						QBDialogType.GROUP, request, (Bundle) null
					);
					Log.d( TAG, "finishGetChatDialogs" );

					for ( QBDialog qbDialog : result )
					{
						QBGroupChat chat = getQbGroupChat( qbDialog.getRoomJid() );

						startListener( chat );
					}

					processEnsureRequests();
				}
				catch ( Exception e )
				{
					Log.e( TAG, "GetChatDialogs failed", e );
				}
				m_trying.set( false );
				return null;
			}
		};

		task.execute( null, null, null );
	}

	private QBGroupChat getQbGroupChat( final String jId )
	{
		QBGroupChat chat = QBChatService.getInstance().getGroupChatManager().getGroupChat( jId );

		if ( chat == null )
		{
			chat = QBChatService.getInstance().getGroupChatManager().createGroupChat( jId );
		}

		return chat;
	}

	private void startListener( final QBGroupChat chat )
	{
		try
		{
			if ( !chat.isJoined() )
			{
				chat.addMessageListener( m_listener );

				DiscussionHistory history = new DiscussionHistory();
				history.setMaxStanzas( 0 );

				chat.join( history );
			}
		}
		catch ( Exception e )
		{
			Log.e( TAG, "StartListener failed", e );
		}
	}

	private class MessageListener extends QBMessageListenerImpl
	{

		@Override
		public void processMessage( final QBChat chat, final QBChatMessage message )
		{
			Log.d( TAG, "processMessage: chat=" + chat + "," + message );

			final List<ChatMessage> messages = new ArrayList<>();
			LobbyUserInfo user = null;
			String userId = (String) message.getProperty( "userId" );
			if ( userId != null )
			{
				user = new LobbyUserInfo( userId );
			}

			messages.add(
				new ChatMessage(
					user,
					message.getId(),
					message.getBody(),
					message.getDateSent(),
					String.valueOf( message.getProperty( "code" ) ),
					String.valueOf( message.getProperty( "codeBody" ) )
				)
			);

			UnreadCounter.Add( message.getDialogId(), 1 );

			m_handler.post(
				new Runnable()
				{
					@Override
					public void run()
					{
						//No sure Otto is good for Service. but see first.
						EventBus.getBus().post( new ChatMessageReceiveEvent( message.getDialogId(), true, messages ) );
					}
				}
			);
		}
	}
}
