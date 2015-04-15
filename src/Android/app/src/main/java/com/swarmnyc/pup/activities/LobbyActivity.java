package com.swarmnyc.pup.activities;

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

import com.swarmnyc.pup.Config;
import com.swarmnyc.pup.LobbyService;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.PuPCallback;
import com.swarmnyc.pup.PuPEmptyCallback;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.chat.ChatMessage;
import com.swarmnyc.pup.chat.ChatMessageListener;
import com.swarmnyc.pup.chat.ChatRoomService;
import com.swarmnyc.pup.chat.ChatService;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.models.LobbyUserInfo;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.client.Response;

public class LobbyActivity extends ActionBarActivity {
    private ArrayAdapter<String> messageAdapter;
    private Lobby lobby;
    private ChatRoomService chatRoom;

    @InjectView(R.id.text_message)
    EditText messageText;

    @InjectView(R.id.list_message)
    ListView messageList;

    @InjectView(R.id.btn_send)
    Button sendMessageButton;

    @InjectView(R.id.btn_login)
    Button loginButton;

    @InjectView(R.id.btn_join)
    Button joinButton;

    @Inject
    LobbyService lobbyService;

    @Inject
    ChatService chatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        ButterKnife.inject(this);
        PuPApplication.getInstance().getComponent().inject(this);

        Intent intent = getIntent();
        String lobbyId = intent.getExtras().getString("lobbyId");

        messageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        messageList.setAdapter(messageAdapter);

        /*
        * Get Lobby data from API
        * Show History
        * If user is guest, show login button
        * If user doesn't join the lobby, show join button
        * Else show message kit.
        * */

        lobbyService.get(lobbyId, new PuPCallback<Lobby>() {
            @Override
            public void success(Lobby lobby, Response response) {
                lobby = lobby;
                initializeLobby();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (chatRoom != null) {
            chatRoom.login();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (chatRoom != null) {
            chatRoom.leave();
        }
    }

    @OnClick(R.id.btn_send)
    void sendMessage() {
        chatRoom.SendMessage(messageText.getText().toString());
        messageText.setText("");
    }

    @OnClick(R.id.btn_login)
    void changeTologinActivity() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent); // todo: change to startActivityForResult for after login
    }

    @OnClick(R.id.btn_join)
    void joinRoom() {
        lobbyService.join(lobby.getId(), new PuPEmptyCallback() {
            @Override
            public void success(Response response) {
                Toast.makeText(LobbyActivity.this, "Join Succeeded", Toast.LENGTH_LONG).show();
                initializeLobby();
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
        if (id == R.id.menu_leave) {
            lobbyService.leave(lobby.getId(), new PuPEmptyCallback() {
                @Override
                public void success(Response response) {
                    Toast.makeText(LobbyActivity.this, "Leave Succeeded", Toast.LENGTH_LONG).show();
                    LobbyActivity.this.finish();
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeLobby() {
        if (chatRoom == null) {
            chatRoom = chatService.getChatRoom(this, lobby);
            chatRoom.setMessageListener(new ChatMessageListener() {
                @Override
                public void receive(List<ChatMessage> messages) {
                    for (ChatMessage message : messages) {
                        messageAdapter.add(message.getBody());
                    }

                    messageAdapter.notifyDataSetChanged();
                }
            });

            chatRoom.loadChatHistory();
        }

        if (User.isLoggedIn()) {
            LobbyUserInfo user = lobby.getUser(User.current.getId());
            if (user == null || user.getIsLeave()) {
                this.joinButton.setVisibility(View.VISIBLE);
            } else {
                this.messageText.setVisibility(View.VISIBLE);
                this.sendMessageButton.setVisibility(View.VISIBLE);
                chatRoom.login();
            }
        } else {
            this.loginButton.setVisibility(View.VISIBLE);
        }
    }
}

