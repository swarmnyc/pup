package com.swarmnyc.pup.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swarmnyc.pup.GameService;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.activities.MainActivity;

import javax.inject.Inject;

public class CreateLobbyGameFilterFragment extends Fragment {
    @Inject
    GameService gameService;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        PuPApplication.getInstance().getComponent().inject(this);
        MainActivity.getInstance().hideToolbar();
        return view;
    }
}
