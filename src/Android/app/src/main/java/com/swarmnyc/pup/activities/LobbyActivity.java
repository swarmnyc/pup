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

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.swarmnyc.pup.Config;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.chat.ChatMessage;
import com.swarmnyc.pup.chat.ChatMessageListener;
import com.swarmnyc.pup.chat.ChatRoomService;
import com.swarmnyc.pup.chat.ChatService;
import com.swarmnyc.pup.components.PuPRestClient;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.models.LobbyUserInfo;

import org.apache.http.Header;
import org.json.JSONObject;

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

    @InjectView(R.id.btn_send)
    Button sendMessageButton;

    @InjectView(R.id.btn_login)
    Button loginButton;

    @InjectView(R.id.btn_join)
    Button joinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        ButterKnife.inject(this);

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
        RequestParams params = new RequestParams();
        PuPRestClient.get("Lobby/" + lobbyId, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    lobby = new Lobby(response);
                    initializeLobby();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(LobbyActivity.this, "Load Lobby Data Failed", Toast.LENGTH_LONG).show();
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
        PuPRestClient.post("Lobby/Join/" + lobby.getId(), null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(LobbyActivity.this, "Join Succeeded", Toast.LENGTH_LONG).show();
                initializeLobby();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(LobbyActivity.this, "Join Failed", Toast.LENGTH_LONG).show();
                error.printStackTrace();
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
            PuPRestClient.post("Lobby/Leave/" + lobby.getId(), null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(LobbyActivity.this, "Leave Succeeded", Toast.LENGTH_LONG).show();
                    LobbyActivity.this.finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(LobbyActivity.this, "Leave Failed", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeLobby() {
        if (chatRoom == null) {
            chatRoom = ChatService.getInstance().getChatRoom(this, lobby);
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

        if (Config.isLoggedIn()) {
            LobbyUserInfo user = lobby.getUsers().get(Config.getUserId());
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

