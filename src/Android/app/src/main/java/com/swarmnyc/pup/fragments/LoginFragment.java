package com.swarmnyc.pup.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.swarmnyc.pup.BuildConfig;
import com.swarmnyc.pup.PuPCallback;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.UserService;
import com.swarmnyc.pup.activities.AuthActivity;
import com.swarmnyc.pup.components.GoogleOAuth;
import com.swarmnyc.pup.viewmodels.UserLoginResult;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.client.Response;

public class LoginFragment extends Fragment {
    private AuthActivity activity;

    public LoginFragment() {
    }

    @Inject
    UserService userService;

    @InjectView(R.id.text_email)
    public EditText emailText;

    @InjectView(R.id.text_password)
    public EditText passwordText;

    @OnClick(R.id.btn_switch)
    public void onSwitchBtnClicked() {
        activity.changeFragment(new SignupFragment());
    }

    @OnClick(R.id.btn_google_oauth)
    public void onGoogleOAuthClicked() {
        GoogleOAuth.startGetAccount(activity);
    }

    @OnClick(R.id.btn_submit)
    public void onSubmitBtnClicked() {
        userService.login(emailText.getText().toString(), passwordText.getText().toString(), new PuPCallback<UserLoginResult>() {
            @Override
            public void success(UserLoginResult userLoginResult, Response response) {
                activity.finishAuth();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, view);

        if (BuildConfig.DEBUG) {
            emailText.setText("test@swarmnyc.com");
            passwordText.setText("123456");
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AuthActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
