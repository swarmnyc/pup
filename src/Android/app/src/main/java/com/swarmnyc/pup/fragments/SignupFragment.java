package com.swarmnyc.pup.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.swarmnyc.pup.R;
import com.swarmnyc.pup.activities.AuthActivity;
import com.swarmnyc.pup.components.GoogleOAuth;
import com.swarmnyc.pup.components.PuPAuth;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SignupFragment extends Fragment {
    private AuthActivity activity;

    public SignupFragment() {
    }

    @InjectView(R.id.text_email)
    EditText emailText;

    @InjectView(R.id.text_password)
    EditText passwordText;

    @OnClick(R.id.btn_switch)
    public void switchFragment() {
        activity.changeFragment(new LoginFragment());
    }

    @OnClick(R.id.btn_submit)
    public void userRegister() {
        try {
            PuPAuth.register(emailText.getText().toString(), passwordText.getText().toString(), new PuPAuth.AuthCallback() {
                @Override
                public void onFinished(boolean result) {
                    if (result) {
                        activity.finishAuth();
                    } else {
                        Toast.makeText(activity, "Login Failed", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_google_oauth)
    public void googleOAuth() {
        GoogleOAuth.startGetAccount(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AuthActivity) activity;
    }
}
