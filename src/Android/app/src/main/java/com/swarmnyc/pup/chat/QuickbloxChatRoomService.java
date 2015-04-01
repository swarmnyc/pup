package com.swarmnyc.pup.chat;

import android.app.Activity;
import android.os.Bundle;

import com.quickblox.chat.QBChat;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChat;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.swarmnyc.pup.Config;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuickbloxChatRoomService extends ChatRoomService {
    private QBGroupChat chat;
    private QBDialog dialog;
    private Activity activity;

    public QuickbloxChatRoomService(Activity activity, QBDialog dialog) {
        this.activity = activity;
        this.dialog = dialog;
    }

    @Override
    public void SendMessage(String message) {
        try {
            QBChatMessage chatMessage = new QBChatMessage();
            chatMessage.setBody(message);
            chatMessage.setProperty("userId", Config.getUserId());
            chatMessage.setDateSent(new Date().getTime() / 1000);
            chatMessage.setSaveToHistory(true);
            chat.sendMessage(chatMessage);
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void login() {
        if (chat != null && chat.isJoined())
            return;

        chat = QBChatService.getInstance().getGroupChatManager().createGroupChat(dialog.getRoomJid());

        DiscussionHistory history = new DiscussionHistory();
        history.setMaxStanzas(0);
        chat.join(history, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                chat.addMessageListener(new QBMessageListener() {
                    @Override
                    public void processMessage(QBChat chat, final QBChatMessage chatMessage) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<ChatMessage> messages = new ArrayList<>();
                                messages.add(new ChatMessage(chatMessage.getProperty("userId"), chatMessage.getBody()));
                                QuickbloxChatRoomService.this.listener.receive(messages);
                            }
                        });
                    }

                    @Override
                    public void processError(QBChat qbChat, QBChatException e, QBChatMessage qbChatMessage) {

                    }

                    @Override
                    public void processMessageDelivered(QBChat qbChat, String s) {

                    }

                    @Override
                    public void processMessageRead(QBChat qbChat, String s) {

                    }
                });
            }

            @Override
            public void onError(List list) {
            }
        });
    }

    @Override
    public void leave() {
        try {
            if (chat != null && chat.isJoined())
                chat.leave();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadChatHistory() {
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setPagesLimit(100);
        //customObjectRequestBuilder.sortDesc("date_sent");

        QBChatService.getDialogMessages(dialog, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(final ArrayList<QBChatMessage> messages, Bundle args) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<ChatMessage> cms = new ArrayList<>();
                        for (QBChatMessage message : messages) {
                            cms.add(new ChatMessage(message.getProperty("userId"), message.getBody()));
                        }

                        QuickbloxChatRoomService.this.listener.receive(cms);
                    }
                });
            }

            @Override
            public void onError(List<String> errors) {
                //AlertDialog.Builder dialog = new AlertDialog.Builder(LobbyActivity.this);
                //dialog.setMessage("load chat history errors: " + errors).create().show();
            }
        });
    }

}
