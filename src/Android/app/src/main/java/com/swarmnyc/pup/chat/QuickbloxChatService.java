package com.swarmnyc.pup.chat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;
import com.squareup.otto.Subscribe;
import com.swarmnyc.pup.Config;
import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.events.ChatServiceLoggedInEvent;
import com.swarmnyc.pup.components.PlayServicesHelper;
import com.swarmnyc.pup.events.UserChangedEvent;
import com.swarmnyc.pup.models.Lobby;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class QuickbloxChatService implements ChatService {
    private QBChatService qbChatService;
    private String        m_appId;
    private QBUser        user;

    public QuickbloxChatService()
    {
        EventBus.getBus().register( this );
    }

    @Override
    public void login( final Activity activity )
    {
        if ( qbChatService != null && qbChatService.isLoggedIn() )
        {
            try
            {
                qbChatService.logout();
            }
            catch ( SmackException.NotConnectedException e )
            {
                e.printStackTrace();
            }
        }

        //Login API
        QBChatService.setDebugEnabled( true );
        m_appId = Config.getConfigString( R.string.QB_APP_ID );
        QBSettings.getInstance().fastConfigInit(
            m_appId, Config.getConfigString( R.string.QB_APP_KEY ), Config.getConfigString( R.string.QB_APP_SECRET )
        );
        if ( !QBChatService.isInitialized() )
        {
            QBChatService.init( activity );
        }

        qbChatService = QBChatService.getInstance();

        user = new QBUser();
        if ( User.isLoggedIn() )
        {
            user.setLogin( User.current.getId() );
            user.setPassword( Config.getConfigString( R.string.QB_APP_PW));
        } else {
            user.setLogin(Config.getConfigString(R.string.QB_APP_DEFAULT_USER));
            user.setPassword(Config.getConfigString(R.string.QB_APP_PW));
        }

        QBAuth.createSession(user, new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                user.setId(qbSession.getUserId());

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Login Chat
                        loginChat(activity, user);
                    }
                });
            }

            @Override
            public void onError(List<String> strings) {
                Log.e("Chat", "Create Session failed");
            }
        });
    }

    @Override
    public ChatRoomService getChatRoomService( Activity activity, Lobby lobby ) {
        QBDialog dialog = new QBDialog( lobby.getTagValue( "QBChatRoomId" ) );
        dialog.setRoomJid( m_appId + "_" + dialog.getDialogId() + "@muc.chat.quickblox.com" );
        dialog.setUserId( user.getId());
        return new QuickbloxChatRoomService(activity, dialog);
    }

    private void loginChat(final Activity context, final QBUser user) {
        qbChatService.login(user, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                try {
                    qbChatService.startAutoSendPresence(60);
                } catch (SmackException.NotLoggedInException e) {
                    e.printStackTrace();
                }

                EventBus.getBus().post( new ChatServiceLoggedInEvent() );
            }

            @Override
            public void onError(List list) {
                Log.e("ChatService", list.get(0).toString());
            }
        });
    }

    @Subscribe
    public void postUserChanged(UserChangedEvent event) {
        //Relog in if user change
        login(MainActivity.getInstance());
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        EventBus.getBus().unregister(this);
    }
}
