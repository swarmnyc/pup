package com.swarmnyc.pup.module.chat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.swarmnyc.pup.Consts;
import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.ui.helpers.DialogHelper;
import com.swarmnyc.pup.ui.events.ChatMessageReceiveEvent;
import com.swarmnyc.pup.module.models.Lobby;
import com.swarmnyc.pup.module.models.LobbyUserInfo;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomService
{
	private final String   m_jid;
	private       Lobby    m_lobby;
	private       Activity m_activity;

	public ChatRoomService(
		final Activity activity, final Lobby lobby
	)
	{
		m_activity = activity;
		m_lobby = lobby;

		m_jid = MessageService.generateJId( lobby.getId() );
	}

	public void SendMessage( String message )
	{
		try
		{
			QBChatMessage chatMessage = new QBChatMessage();
			chatMessage.setBody( message );
			chatMessage.setProperty( "userId", User.current.getId() );
			//chatMessage.setDateSent( new Date().getTime() / 1000 );
			chatMessage.setSaveToHistory( true );
			QBChatService.getInstance().getGroupChatManager().getGroupChat( m_jid ).sendMessage( chatMessage );
		}
		catch ( Exception e )
		{
			Log.e( "QuickbloxChat", "SendMessage Failed", e );
			Toast.makeText(m_activity, m_activity.getString(R.string.message_operation_failed), Toast.LENGTH_LONG).show();
		}
	}

	public void loadChatHistory( final long date )
	{
		EventBus.getBus().post(
			new EnsureChatConnectRequest(
				m_lobby.getId(), new Runnable()
			{
				@Override
				public void run()
				{
					QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
					customObjectRequestBuilder.setPagesLimit( Consts.PAGE_SIZE );
					customObjectRequestBuilder.sortDesc( "date_sent" );
					if ( date > 0 )
					{
						customObjectRequestBuilder.lte( "date_sent", date );
					}

					QBDialog dialog = new QBDialog( m_lobby.getId() );
					dialog.setRoomJid( m_jid );

					QBChatService.getDialogMessages(
						dialog, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBChatMessage>>()
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

												cms.add(
													new ChatMessage(
														user, message.getId(), message.getBody(), message.getDateSent()
													)
												);
											}

											EventBus.getBus().post(
												new ChatMessageReceiveEvent(
													m_lobby.getId(), false, cms
												)
											);
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
											DialogHelper.showError(
												m_activity, m_activity.getString(R.string.message_operation_failed)
											);

										}
									}
								);
							}
						}
					);
				}
			}
			)
		);


	}

	private LobbyUserInfo getLobbyUserInfo( final QBChatMessage chatMessage )
	{
		String userId = (String) chatMessage.getProperty( "userId" );
		if ( userId == null )
		{
			return null;
		}
		else
		{
			LobbyUserInfo user = new LobbyUserInfo( userId );

			return user;
		}
	}
}
