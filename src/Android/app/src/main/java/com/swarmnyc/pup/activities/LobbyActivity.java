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
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.swarmnyc.pup.Config;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.chat.ChatMessage;
import com.swarmnyc.pup.chat.ChatMessageListener;
import com.swarmnyc.pup.chat.ChatRoomService;
import com.swarmnyc.pup.chat.ChatService;
import com.swarmnyc.pup.models.Lobby;

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
    private ArrayAdapter<String> messageAdapter;
    private Lobby lobby;
    private ChatRoomService chatRoom;

    @InjectView(R.id.text_message)
    EditText messageText;

    @InjectView(R.id.list_message)
    ListView messageList;

    @InjectView(R.id.btn_submit)
    Button sendMessageButton;

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

        chatRoom = ChatService.getInstance().getChatRoom(lobby);
        chatRoom.setMessageListener(new ChatMessageListener() {
            @Override
            public void receive(List<ChatMessage> messages) {
                for (ChatMessage message : messages) {
                    messageAdapter.add(message.getBody());
                }

                messageAdapter.notifyDataSetChanged();
            }
        });

        if (Config.isLoggedIn()){
            this.messageText.setVisibility(View.VISIBLE);
            this.sendMessageButton.setVisibility(View.VISIBLE);
        }else {
            this.messageText.setVisibility(View.GONE);
            this.sendMessageButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        chatRoom.login(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        chatRoom.leave();
    }

    @OnClick(R.id.btn_submit)
    void SendMessage() {
        chatRoom.SendMessage(messageText.getText().toString());
        messageText.setText("");
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

