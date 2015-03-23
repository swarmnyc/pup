package com.swarmnyc.pup.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.swarmnyc.pup.Config;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.activities.CreateLobbyActivity;
import com.swarmnyc.pup.activities.LobbyActivity;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.components.PuPRestClient;
import com.swarmnyc.pup.models.Lobby;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class LobbyListFragment extends Fragment {
    private MainActivity activity;

    public LobbyListFragment() {
    }

    @InjectView(R.id.list_lobby)
    public ListView lobbyListView;

    @InjectView(R.id.btn_create_lobby)
    public Button createLobbyButton;

    @OnClick(R.id.btn_create_lobby)
    public void onCreateLobbyButtonClicked() {
        this.startActivityForResult(new Intent(this.activity, CreateLobbyActivity.class), CreateLobbyActivity.REQUEST_CODE_CREATE_LOBBY);
    }

    LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lobby_list, container, false);
        ButterKnife.inject(this, view);
        setHasOptionsMenu(true);
        this.inflater = inflater;
        lobbyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Lobby lobby = (Lobby) parent.getAdapter().getItem(position);
                Intent intent = new Intent(LobbyListFragment.this.activity, LobbyActivity.class);
                intent.putExtra("lobbyId", lobby.getId());
                LobbyListFragment.this.activity.startActivity(intent);
            }
        });

        createLobbyButton.setVisibility(Config.isLoggedIn() ? View.VISIBLE : View.INVISIBLE);

        reloadData();

        return view;
    }

    private void reloadData() {
        //TODO： Change to better code
        PuPRestClient.get("Lobby", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    List<Lobby> lobbies = Lobby.FromJsonArray(response);
                    ArrayAdapter<Lobby> adapter = new ArrayAdapter<Lobby>(activity, android.R.layout.simple_list_item_1, lobbies) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            if (convertView == null) {
                                convertView = inflater.inflate(R.layout.item_lobby, null);

                                ((TextView) convertView.findViewById(R.id.text_name)).setText(this.getItem(position).getName());
                                //((TextView) convertView.findViewById(R.id.text_start_time)).setText(this.getItem(position).getStartTime().toString());
                            }

                            return convertView;
                        }
                    };

                    lobbyListView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("Rest", "Get Robby Faild", throwable);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                reloadData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
