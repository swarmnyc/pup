package com.swarmnyc.pup.components;

import android.app.Activity;
import android.content.Context;
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

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ChatService {
    private static Hashtable<String, QBDialog> dialogs = new Hashtable<>();

    public static QBChatService Service;

    public static void login(final Activity context) {
        QBChatService.setDebugEnabled(true);
        QBSettings.getInstance().fastConfigInit(Config.getConfigString(R.string.QB_APP_ID), Config.getConfigString(R.string.QB_APP_KEY), Config.getConfigString(R.string.QB_APP_SECRET));
        if (!QBChatService.isInitialized()) {
            QBChatService.init(context);
        }

        Service = QBChatService.getInstance();

        final QBUser user = new QBUser();
        if (Config.isLoggedIn()){
            user.setLogin(Config.getUserId());
            user.setPassword(Config.getConfigString(R.string.QB_APP_PW));
        }
        else
        {
            user.setLogin(Config.getConfigString(R.string.QB_APP_DEFAULT_USER));
            user.setPassword(Config.getConfigString(R.string.QB_APP_PW));
        }

        QBAuth.createSession(user, new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                user.setId(qbSession.getUserId());

                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loginChat(context, user);
                    }
                });
            }

            @Override
            public void onError(List<String> strings) {
                Log.e("Chat", "Create Session failed");
            }
        });
    }


    public static QBDialog getDialog(String id) {
        return dialogs.get(id);
    }

    private static void loginChat(final Activity context, final QBUser user) {
        Service.login(user, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                try {
                    Service.startAutoSendPresence(30);
                } catch (SmackException.NotLoggedInException e) {
                    e.printStackTrace();
                }

                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
