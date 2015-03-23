package com.swarmnyc.pup.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.components.PuPRestClient;
import com.swarmnyc.pup.models.PlayStyle;
import com.swarmnyc.pup.models.SkillLevel;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

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

    @OnClick(R.id.btn_submit)
    void onSubmitButtonClicked() {
        RequestParams data = new RequestParams();
        data.put("gameId", gameIdText.getText().toString());
        data.put("name", nameText.getText().toString());
        data.put("playStyle", PlayStyle.valueOf((String) playStyleSpinner.getSelectedItem()).getValue());
        data.put("skillLevel", SkillLevel.valueOf((String) skillLevelSpinner.getSelectedItem()).getValue());
        data.put("startTimeUtc", "2015-03-22T16:45:39.169Z");

        PuPRestClient.post("Lobby", data, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                CreateLobbyActivity.this.setResult(Activity.RESULT_OK);
                CreateLobbyActivity.this.finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("Rest", "Create Lobby failed", error);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);

        ButterKnife.inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<String> psList = new ArrayList<>();
        for (PlayStyle ps : PlayStyle.values()) {
            psList.add(ps.toString());
        }

        ArrayAdapter<String> psDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, psList);
        psDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playStyleSpinner.setAdapter(psDataAdapter);

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
