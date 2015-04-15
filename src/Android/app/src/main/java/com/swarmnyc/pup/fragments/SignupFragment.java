package com.swarmnyc.pup.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.swarmnyc.pup.PuPCallback;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.UserService;
import com.swarmnyc.pup.activities.AuthActivity;
import com.swarmnyc.pup.components.GoogleOAuth;
import com.swarmnyc.pup.viewmodels.UserRegisterResult;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.client.Response;

public class SignupFragment extends Fragment {
    private AuthActivity activity;

    public SignupFragment() {
    }

    @Inject
    UserService userService;

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
        userService.register(emailText.getText().toString(), passwordText.getText().toString(),new PuPCallback<UserRegisterResult>() {
            @Override
            public void success(UserRegisterResult userRegisterResult, Response response) {
                activity.finishAuth();
            }
        });
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
