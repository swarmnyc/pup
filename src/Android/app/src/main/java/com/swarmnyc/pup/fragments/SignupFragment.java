package com.swarmnyc.pup.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swarmnyc.pup.R;
import com.swarmnyc.pup.activities.AuthActivity;
import com.swarmnyc.pup.components.GoogleOAuth;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupFragment extends Fragment {
    private AuthActivity activity;
    public SignupFragment() {
    }

    @OnClick(R.id.btn_switch)
    public void onSwitchBtnClicked() {
        activity.changeFragment(new LoginFragment());
    }

    @OnClick(R.id.btn_google_oauth)
    public void onGoogleOAuthClicked() {
        GoogleOAuth.startGetAccount(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.inject(this, view);

        return view;
    }
}
