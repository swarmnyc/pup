package com.swarmnyc.pup.chat;

import android.app.Activity;
import android.os.Bundle;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChat;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.components.DialogHelper;
import com.swarmnyc.pup.events.AfterEnterChatRoomEvent;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.models.LobbyUserInfo;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.List;

public class QuickbloxChatRoomService extends ChatRoomService
{
	private QBGroupChat          m_chat;
	private Lobby                m_lobby;
	private QBDialog             m_dialog;
	private Activity             m_activity;

	public QuickbloxChatRoomService(
		final Activity activity, final Lobby lobby
	)
	{
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
			//chatMessage.setDateSent( new Date().getTime() / 1000 );
			chatMessage.setSaveToHistory( true );
			m_chat.sendMessage( chatMessage );
		}
		catch ( XMPPException | SmackException.NotConnectedException e )
		{
			e.printStackTrace();
		}
	}

	public void login()
	{
		m_dialog = new QBDialog( m_lobby.getRoomId() );
		m_dialog.setRoomJid( Config.getConfigString( R.string.QB_APP_ID ) + "_" + m_dialog.getDialogId() + "@muc.chat.quickblox.com" );
		m_dialog.setUserId( QBChatService.getInstance().getUser().getId() );

		m_chat = QBChatService.getInstance().getGroupChatManager().getGroupChat( m_dialog.getRoomJid() );

		if ( m_chat == null )
		{
			m_chat = QBChatService.getInstance().getGroupChatManager().createGroupChat( m_dialog.getRoomJid() );
		}

		EventBus.getBus().post( new AfterEnterChatRoomEvent(m_chat) );
	}

	private LobbyUserInfo getLobbyUserInfo( final QBChatMessage chatMessage )
	{
		String userId = (String) chatMessage.getProperty( "userId" );
		if ( userId == null)
		{
			return null;
		}
		else
		{
			LobbyUserInfo user = new LobbyUserInfo(userId);

			return user;
		}
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
		catch ( XMPPException | SmackException.NotConnectedException e )
		{
			e.printStackTrace();
		}
	}

	@Override
	public void loadChatHistory(long date)
	{
		QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
		customObjectRequestBuilder.setPagesLimit( Consts.PAGE_SIZE );
		customObjectRequestBuilder.sortDesc( "date_sent" );
		if ( date > 0 ){
			customObjectRequestBuilder.lte( "date_sent", date );
		}

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

									cms.add( new ChatMessage( user,message.getId(), message.getBody(), message.getDateSent() ) );
								}

								EventBus.getBus().post(new ChatMessageReceiveEvent(m_dialog.getDialogId(), false, cms));
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
								DialogHelper.showError(m_activity, "load chat history errors: " + errors );

							}
						}
					);
				}
			}
		);
	}
}
