package com.swarmnyc.pup.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.swarmnyc.pup.RestApis.GameRestApi;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.Services.GameService;
import com.swarmnyc.pup.fragments.CreateLobbyGameFilterFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class CreateLobbyActivity extends ActionBarActivity {

    public static int REQUEST_CODE_CREATE_LOBBY = 56229;

    @Inject
    GameService gameService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);

        ButterKnife.inject(this);
        PuPApplication.getInstance().getComponent().inject(this);
//getSupportFragmentManager().popBackStack("A",);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new CreateLobbyGameFilterFragment())
                .commit();
    }
}
