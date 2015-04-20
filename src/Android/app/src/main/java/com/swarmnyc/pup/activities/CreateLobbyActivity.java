package com.swarmnyc.pup.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.swarmnyc.pup.GameService;
import com.swarmnyc.pup.LobbyService;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.PuPCallback;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.fragments.CreateLobbyGameFilterFragment;
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
