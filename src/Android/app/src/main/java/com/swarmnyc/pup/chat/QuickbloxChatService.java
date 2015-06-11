package com.swarmnyc.pup.chat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.users.model.QBUser;
import com.swarmnyc.pup.*;
import com.swarmnyc.pup.components.DialogHelper;
import com.swarmnyc.pup.models.Lobby;
import org.jivesoftware.smack.SmackException;

import java.util.List;

public class QuickbloxChatService implements ChatService
{
	private QBChatService m_chatService;
	private QBUser        m_user;
	private AsyncCallback m_callback;

	public QBUser getUser()
	{
		return m_user;
	}

	@Override
	public void login( final Activity activity )
	{
		QBUser user = new QBUser();
		if ( User.isLoggedIn() )
		{
			user.setLogin( User.current.getId() );
			user.setPassword( Config.getConfigString( R.string.QB_APP_PW ) );
		}
		else
		{
			user.setLogin( Config.getConfigString( R.string.QB_APP_DEFAULT_USER ) );
			user.setPassword( Config.getConfigString( R.string.QB_APP_PW ) );
		}

		login( activity, user, new AsyncCallback());
	}

	protected void login( final Activity activity, QBUser user, AsyncCallback callback )
	{
		m_callback = callback;
		if ( m_user != null)
		{
			if ( m_user.getLogin().equals( user.getLogin() )  ){
				m_callback.success();
				return;
			}else{
				try
				{
					m_chatService.logout();
				}
				catch ( SmackException.NotConnectedException e )
				{
					e.printStackTrace();
				}
			}
		}

		//Login API
		QBChatService.setDebugEnabled( BuildConfig.DEBUG );
		QBSettings.getInstance().fastConfigInit(
			Config.getConfigString( R.string.QB_APP_ID ),
			Config.getConfigString( R.string.QB_APP_KEY ),
			Config.getConfigString( R.string.QB_APP_SECRET )
		);

		if ( !QBChatService.isInitialized() )
		{
			QBChatService.init( activity );
		}

		m_chatService = QBChatService.getInstance();

		m_user = user;

		QBAuth.createSession(
			m_user, new QBEntityCallbackImpl<QBSession>()
			{
				@Override
				public void onSuccess( QBSession qbSession, Bundle bundle )
				{
					m_user.setId( qbSession.getUserId() );

					activity.runOnUiThread(
						new Runnable()
						{
							@Override
							public void run()
							{
								//Login Chat
								loginChat( m_user );
							}
						}
					);
				}
			}
		);
	}

	private void loginChat( final QBUser user )
	{
		m_chatService.login(
			user, new QBEntityCallbackImpl()
			{
				@Override
				public void onSuccess()
				{
					 m_callback.success();
				}
			}
		);
	}

	@Override
	public ChatRoomService getChatRoomService( Activity activity, Lobby lobby )
	{
		return new QuickbloxChatRoomService( this, activity, lobby );
	}
}
