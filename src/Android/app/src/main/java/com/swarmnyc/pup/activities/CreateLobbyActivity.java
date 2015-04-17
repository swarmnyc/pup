package com.swarmnyc.pup.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.swarmnyc.pup.LobbyService;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.PuPCallback;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.models.GamePlatform;
import com.swarmnyc.pup.models.Lobby;
import com.swarmnyc.pup.models.PlayStyle;
import com.swarmnyc.pup.models.SkillLevel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.client.Response;

public class CreateLobbyActivity extends ActionBarActivity {

    public static int REQUEST_CODE_CREATE_LOBBY = 56229;

    @InjectView(R.id.text_gameId)
    TextView gameIdText;

    @InjectView(R.id.text_name)
    TextView nameText;

    @InjectView(R.id.spinner_play_style)
    Spinner playStyleSpinner;

    @InjectView(R.id.spinner_skill_level)
    Spinner skillLevelSpinner;

    @InjectView(R.id.spinner_platform)
    Spinner platformSpinner;

    @Inject
    LobbyService lobbyService;

    @OnClick(R.id.btn_submit)
    void onSubmitButtonClicked() {
        Lobby lobby = new Lobby();
        lobby.setGameId(gameIdText.getText().toString());
        lobby.setName(nameText.getText().toString());
        lobby.setPlayStyle(PlayStyle.valueOf((String) playStyleSpinner.getSelectedItem()));
        lobby.setSkillLevel(SkillLevel.valueOf((String) skillLevelSpinner.getSelectedItem()));
        lobby.setPlatform(GamePlatform.valueOf((String) platformSpinner.getSelectedItem()));
        lobby.setStartTime(new Date());

        lobbyService.create(lobby, new PuPCallback<Lobby>() {
            @Override
            public void success(Lobby lobby, Response response) {
                CreateLobbyActivity.this.setResult(Activity.RESULT_OK);
                CreateLobbyActivity.this.finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);

        ButterKnife.inject(this);
        PuPApplication.getInstance().getComponent().inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // GamePlatform
        List<String> gpList = new ArrayList<>();
        for (GamePlatform gp : GamePlatform.values()) {
                gpList.add(gp.toString());
        }

        ArrayAdapter<String> gpDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, gpList);
        gpDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        platformSpinner.setAdapter(gpDataAdapter);

        // PlayStyle
        List<String> psList = new ArrayList<>();
        for (PlayStyle ps : PlayStyle.values()) {
            psList.add(ps.toString());
        }

        ArrayAdapter<String> psDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, psList);
        psDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playStyleSpinner.setAdapter(psDataAdapter);

        // SkillLevel
        List<String> slList = new ArrayList<>();
        for (SkillLevel ps : SkillLevel.values()) {
            slList.add(ps.toString());
        }

        ArrayAdapter<String> slDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, slList);
        slDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        skillLevelSpinner.setAdapter(slDataAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_lobby, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
