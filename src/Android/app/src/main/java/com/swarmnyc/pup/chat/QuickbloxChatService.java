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
	private AsyncCallback m_callback;

	public QBUser getUser()
	{
		return QBChatService.getInstance().getUser();
	}

	public QuickbloxChatService(  )
	{
		//Login API
		QBChatService.setDebugEnabled( BuildConfig.DEBUG );
		QBSettings.getInstance().fastConfigInit(
			Config.getConfigString( R.string.QB_APP_ID ),
			Config.getConfigString( R.string.QB_APP_KEY ),
			Config.getConfigString( R.string.QB_APP_SECRET )
		);
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

	protected void login( final Activity activity, final QBUser user, AsyncCallback callback )
	{
		if ( !QBChatService.isInitialized() )
		{
			QBChatService.init( activity );
		}

		m_callback = callback;
		if ( getUser() != null)
		{
			if ( getUser().getLogin().equals( user.getLogin() )  ){
				m_callback.success();
				return;
			}else{
				try
				{
					QBChatService.getInstance().logout();
				}
				catch ( SmackException.NotConnectedException e )
				{
					e.printStackTrace();
				}
			}
		}

		QBAuth.createSession(
			user, new QBEntityCallbackImpl<QBSession>()
			{
				@Override
				public void onSuccess( QBSession qbSession, Bundle bundle )
				{
					user.setId( qbSession.getUserId() );

					activity.runOnUiThread(
						new Runnable()
						{
							@Override
							public void run()
							{
								//Login Chat
								loginChat( user );
							}
						}
					);
				}
			}
		);
	}

	private void loginChat( final QBUser user )
	{
		QBChatService.getInstance().login(
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
