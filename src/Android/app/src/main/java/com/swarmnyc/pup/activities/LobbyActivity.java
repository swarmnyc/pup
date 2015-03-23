package com.swarmnyc.pup.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChat;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.listeners.QBMessageListenerImpl;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.components.ChatService;
import com.swarmnyc.pup.models.Lobby;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class LobbyActivity extends ActionBarActivity {

    private Lobby lobby;

    @InjectView(R.id.text_message)
    EditText messageText;

    @InjectView(R.id.list_message)
    ListView messageList;

    @InjectView(R.id.btn_submit)
    Button sendMessageButton;

    ArrayAdapter<String> messageAdapter;
    QBGroupChat chat;
    QBDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        String lobbyId = intent.getExtras().getString("lobbyId");

        lobby = Lobby.Lobbies.get(lobbyId);

        messageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        messageList.setAdapter(messageAdapter);

        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setPagesLimit(100);

        initialChat();
    }

    @OnClick(R.id.btn_submit)
    void SendMessage() {
        if (chat != null) {
            try {
                QBChatMessage chatMessage = new QBChatMessage();

                chatMessage.setBody(messageText.getText().toString());
                //chatMessage.setProperty("save_to_history", "1");
                chatMessage.setDateSent(new Date().getTime()/1000);
                chatMessage.addAttachment(new QBAttachment());
                chatMessage.setSaveToHistory(true);
                chat.sendMessage(chatMessage);
                messageText.setText("");
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    private void initialChat() {
        dialog = ChatService.getDialog(lobby.getChatRoomId());
        chat =  ChatService.Service.getGroupChatManager().createGroupChat(dialog.getRoomJid());

        DiscussionHistory history = new DiscussionHistory();
        history.setMaxStanzas(0);
        chat.join(history, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                chat.addMessageListener(listener);
                LobbyActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LobbyActivity.this, "Ini OK", Toast.LENGTH_LONG).show();
                        sendMessageButton.setVisibility(View.VISIBLE);

                        loadChatHistory();
                    }
                });
            }

            @Override
            public void onError(List list) {

            }
        });
    }

    QBMessageListener listener = new QBMessageListenerImpl<QBGroupChat>() {
        @Override
        public void processMessage(QBGroupChat groupChat,final QBChatMessage chatMessage) {
            LobbyActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.add(chatMessage.getBody());
                    messageAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void processError(QBGroupChat groupChat, QBChatException error, QBChatMessage originMessage){

        }
    };

    private void loadChatHistory(){
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setPagesLimit(100);
        //customObjectRequestBuilder.sortDesc("date_sent");

        QBChatService.getDialogMessages(dialog, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(final ArrayList<QBChatMessage> messages, Bundle args) {
                LobbyActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (QBChatMessage message : messages) {
                            messageAdapter.add(message.getBody());
                        }
                        messageAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(List<String> errors) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(LobbyActivity.this);
                dialog.setMessage("load chat history errors: " + errors).create().show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lobby, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
