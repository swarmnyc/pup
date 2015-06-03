package com.swarmnyc.pup.chat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.quickblox.chat.QBChat;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChat;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;
import com.swarmnyc.pup.Config;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.components.DialogHelper;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.models.LobbyUserInfo;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuickbloxChatRoomService extends ChatRoomService
{
	private QBGroupChat          m_chat;
	private Lobby                m_lobby;
	private QBDialog             m_dialog;
	private QuickbloxChatService m_quickbloxChatService;
	private Activity             m_activity;

	public QuickbloxChatRoomService(
		final QuickbloxChatService quickbloxChatService, Activity activity, final Lobby lobby
	)
	{
		m_quickbloxChatService = quickbloxChatService;
		m_activity = activity;
		m_lobby = lobby;
	}

	@Override
	public void SendMessage( String message )
	{
		try
		{
			QBChatMessage chatMessage = new QBChatMessage();
			chatMessage.setBody( message );
			chatMessage.setProperty( "userId", User.current.getId() );
			chatMessage.setProperty( "userName", User.current.getUserName() );
			chatMessage.setDateSent( new Date().getTime() / 1000 );
			chatMessage.setSaveToHistory( true );
			m_chat.sendMessage( chatMessage );
		}
		catch ( XMPPException e )
		{
			e.printStackTrace();
		}
		catch ( SmackException.NotConnectedException e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public void login()
	{
		QBUser user = new QBUser();
		if ( User.isLoggedIn() && m_lobby.getUser( User.current.getId() ) != null )
		{
			user.setLogin( User.current.getId() );
			user.setPassword( Config.getConfigString( R.string.QB_APP_PW ) );
		}
		else
		{
			user.setLogin( Config.getConfigString( R.string.QB_APP_DEFAULT_USER ) );
			user.setPassword( Config.getConfigString( R.string.QB_APP_PW ) );
		}

		m_quickbloxChatService.login(
			m_activity, user, new Handler.Callback()
			{
				@Override
				public boolean handleMessage( final Message msg )
				{
					joinRoom();
					return true;
				}
			}
		);
	}

	private void joinRoom()
	{
		if ( m_chat != null && m_chat.isJoined() )
		{ return; }

		m_dialog = new QBDialog( m_lobby.getTagValue( "QBChatRoomId" ) );
		m_dialog.setRoomJid( Config.getConfigString( R.string.QB_APP_ID ) + "_" + m_dialog.getDialogId() + "@muc.chat.quickblox.com" );
		m_dialog.setUserId( m_quickbloxChatService.getUser().getId() );

		m_chat = QBChatService.getInstance().getGroupChatManager().createGroupChat( m_dialog.getRoomJid() );

		DiscussionHistory history = new DiscussionHistory();
		history.setMaxStanzas( 100 );
		m_chat.join(
			history, new QBEntityCallbackImpl()
			{
				@Override
				public void onSuccess()
				{
					m_chat.addMessageListener(
						new QBMessageListener()
						{
							@Override
							public void processMessage( QBChat chat, final QBChatMessage chatMessage )
							{
								m_activity.runOnUiThread(
									new Runnable()
									{
										@Override
										public void run()
										{
											List<ChatMessage> messages = new ArrayList<>();
											LobbyUserInfo user = getLobbyUserInfo( chatMessage );

											messages.add( new ChatMessage( user, chatMessage.getBody() ) );
											QuickbloxChatRoomService.this.listener.receive( messages );
										}
									}
								);
							}

							@Override
							public void processError(
								QBChat qbChat, final QBChatException e, QBChatMessage qbChatMessage
							)
							{
								m_activity.runOnUiThread(
									new Runnable()
									{
										@Override
										public void run()
										{
											DialogHelper.showError( "chat error: " + e.toString() );
										}
									}
								);
							}

							@Override
							public void processMessageDelivered( QBChat qbChat, String s )
							{

							}

							@Override
							public void processMessageRead( QBChat qbChat, String s )
							{

							}
						}
					);
				}

				@Override
				public void onError( final List errors )
				{
					m_activity.runOnUiThread(
						new Runnable()
						{
							@Override
							public void run()
							{
								DialogHelper.showError( "login chat errors: " + errors );
							}
						}
					);
				}
			}
		);
	}

	private LobbyUserInfo getLobbyUserInfo( final QBChatMessage chatMessage )
	{
		String userId = (String)chatMessage.getProperty( "userId" );
		LobbyUserInfo user = m_lobby.getUser( userId );

		if ( user == null )
		{
			user = new LobbyUserInfo();
			user.setId( userId );
			user.setUserName( (String)chatMessage.getProperty( "userName" ) );
		}
		return user;
	}

	@Override
	public void leave()
	{
		try
		{
			if ( m_chat != null && m_chat.isJoined() )
			{
				m_chat.leave();
			}
		}
		catch ( XMPPException e )
		{
			e.printStackTrace();
		}
		catch ( SmackException.NotConnectedException e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public void loadChatHistory()
	{
		QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
		customObjectRequestBuilder.setPagesLimit( 1000 );

		QBChatService.getDialogMessages(
			m_dialog, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBChatMessage>>()
			{
				@Override
				public void onSuccess( final ArrayList<QBChatMessage> messages, Bundle args )
				{
					m_activity.runOnUiThread(
						new Runnable()
						{
							@Override
							public void run()
							{
								List<ChatMessage> cms = new ArrayList<>();
								for ( QBChatMessage message : messages )
								{
									LobbyUserInfo user = getLobbyUserInfo( message );

									cms.add( new ChatMessage( user, message.getBody() ) );
								}

								QuickbloxChatRoomService.this.listener.receive( cms );
							}
						}
					);
				}

				@Override
				public void onError( final List<String> errors )
				{
					m_activity.runOnUiThread(
						new Runnable()
						{
							@Override
							public void run()
							{
								DialogHelper.showError( "load chat history errors: " + errors );

							}
						}
					);
				}
			}
		);
	}

}
