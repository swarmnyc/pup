package com.swarmnyc.pup.fragments;

import android.app.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.swarmnyc.pup.BuildConfig;
import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.RestApis.RestApiCallback;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.RestApis.UserRestApi;
import com.swarmnyc.pup.activities.AuthActivity;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.components.GoogleOAuth;
import com.swarmnyc.pup.models.LoggedInUser;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginFragment extends Fragment {
    private AuthActivity activity;

    public LoginFragment() {
    }

    @Inject
    UserRestApi userRestApi;

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
        userRestApi.login(emailText.getText().toString(), passwordText.getText().toString(), new RestApiCallback<LoggedInUser>() {
            @Override
            public void success(LoggedInUser user, Response response) {
                User.login(user);
                activity.finishAuth();
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                Toast.makeText(LoginFragment.this.activity, "Login Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.getInstance().hideToolbar();
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, view);
        PuPApplication.getInstance().getComponent().inject(this);

        if (BuildConfig.DEBUG) {
            emailText.setText("hello@swarmnyc.com");
            passwordText.setText("Abc1234");
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
