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
import com.swarmnyc.pup.Config;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.components.PlayServicesHelper;
import com.swarmnyc.pup.models.Lobby;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class QuickbloxChatService extends ChatService {
    private Hashtable<String, QBDialog> dialogs = new Hashtable<>();

    private QBChatService qbChatService;
    private Activity activity;

    @Override
    public void login(final Activity activity) {
        if (qbChatService != null && qbChatService.isLoggedIn()) {
            try {
                qbChatService.logout();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
        this.activity = activity;

        //QBChatService.setDebugEnabled(true);
        QBSettings.getInstance().fastConfigInit(Config.getConfigString(R.string.QB_APP_ID), Config.getConfigString(R.string.QB_APP_KEY), Config.getConfigString(R.string.QB_APP_SECRET));
        if (!QBChatService.isInitialized()) {
            QBChatService.init(activity);
        }

        qbChatService = QBChatService.getInstance();

        final QBUser user = new QBUser();
        if (Config.isLoggedIn()) {
            user.setLogin(Config.getUserId());
            user.setPassword(Config.getConfigString(R.string.QB_APP_PW));
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
    public ChatRoomService getChatRoom(Activity activity, Lobby lobby) {
        return new QuickbloxChatRoomService(activity, dialogs.get(lobby.getTagValue("QBChatRoomId")));
    }

    private void loginChat(final Activity context, final QBUser user) {
        qbChatService.login(user, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                try {
                    qbChatService.startAutoSendPresence(30);
                } catch (SmackException.NotLoggedInException e) {
                    e.printStackTrace();
                }

                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new PlayServicesHelper(context);
                        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
                        customObjectRequestBuilder.setPagesLimit(100);

                        QBChatService.getChatDialogs(null, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBDialog>>() {
                            @Override
                            public void onSuccess(ArrayList<QBDialog> result, Bundle params) {
                                for (QBDialog dialog : result) {
                                    dialogs.put(dialog.getDialogId(), dialog);
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(List list) {
                Log.e("ChatService", list.get(0).toString());
            }
        });
    }
}
