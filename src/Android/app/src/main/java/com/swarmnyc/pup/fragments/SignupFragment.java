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
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.components.GoogleOAuth;
import com.swarmnyc.pup.components.Navigator;
import com.swarmnyc.pup.viewmodels.UserRegisterResult;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.client.Response;

public class SignupFragment extends Fragment {
    @Inject
    UserService userService;

    @InjectView(R.id.text_email)
    EditText emailText;

    @InjectView(R.id.text_password)
    EditText passwordText;

    @OnClick(R.id.btn_switch)
    public void switchFragment() {
        Navigator.To(new LoginFragment());
    }

    @OnClick(R.id.btn_submit)
    public void userRegister() {
        userService.register(emailText.getText().toString(), passwordText.getText().toString(),new PuPCallback<UserRegisterResult>() {
            @Override
            public void success(UserRegisterResult userRegisterResult, Response response) {
                Navigator.To(new LobbyListFragment());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.getInstance().hideToolbar();
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.inject(this, view);
        return view;
    }
}
