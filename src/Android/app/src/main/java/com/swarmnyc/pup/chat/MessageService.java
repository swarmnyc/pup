package com.swarmnyc.pup.chat;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
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
import com.swarmnyc.pup.chat.ChatMessage;
import com.swarmnyc.pup.events.EnterChatRoomEvent;
import com.swarmnyc.pup.models.LobbyUserInfo;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;
import java.util.List;

public class MessageService extends Service
{
	private static final String TAG = MessageService.class.getSimpleName();
	private MessageListener m_listener;

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
	public void afterEnterRoom( EnterChatRoomEvent event )
	{
		startListener( event.getChat() );
	}

	private void processChatRooms()
	{
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>()
		{
			@Override
			protected Void doInBackground( final Void[] params )
			{
				try
				{
					QBUser user = new QBUser();
					user.setLogin( User.current.getId() );
					user.setPassword( "swarmnyc" );

					QBSession session = QBAuth.createSession( user );
					user.setId( session.getUserId() );
					QBChatService.getInstance().login( user );

					QBRequestGetBuilder request = new QBRequestGetBuilder();
					request.setPagesLimit( 10 );
					request.sortDesc( "date_sent" );

					final ArrayList<QBDialog> result = QBChatService.getChatDialogs(
						QBDialogType.GROUP, request, (Bundle) null
					);
					Log.d( TAG, "finishGetChatDialogs" );

					for ( QBDialog qbDialog : result )
					{
						QBGroupChat chat = getQbGroupChat( qbDialog.getRoomJid() );

						startListener( chat );
					}
				}
				catch ( Exception e )
				{
					e.printStackTrace();
				}

				return null;
			}
		};

		task.execute( null, null, null );
	}

	private QBGroupChat getQbGroupChat( final String roomId )
	{
		QBGroupChat chat = QBChatService.getInstance().getGroupChatManager().getGroupChat( roomId );

		if ( chat == null )
		{
			chat = QBChatService.getInstance().getGroupChatManager().createGroupChat( roomId );
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

			List<ChatMessage> messages = new ArrayList<>();
			LobbyUserInfo user = null;
			String userId = (String) message.getProperty( "userId" );
			if ( userId != null )
			{
				user = new LobbyUserInfo();
				user.setId( userId );
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

			EventBus.getBus().post(new ChatMessageReceiveEvent(message.getDialogId(), true, messages));
		}
	}
}
