package com.swarmnyc.pup.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannedString;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.swarmnyc.pup.RestApis.RestApiCallback;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.RestApis.UserRestApi;
import com.swarmnyc.pup.activities.MainActivity;
import com.swarmnyc.pup.components.Navigator;
import com.swarmnyc.pup.viewmodels.UserRegisterResult;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.client.Response;

public class SignupFragment extends Fragment {
    @Inject
    UserRestApi userRestApi;

    @InjectView(R.id.text_email)
    EditText emailText;

    @InjectView(R.id.text_password)
    EditText passwordText;

    @InjectView(R.id.checkbox_tos)
    CheckBox tosCheckbox;

    @OnClick(R.id.btn_join)
    public void userRegister() {
        if (tosCheckbox.isChecked()) {
            userRestApi.register(emailText.getText().toString(), passwordText.getText().toString(), new RestApiCallback<UserRegisterResult>() {
                @Override
                public void success(UserRegisterResult userRegisterResult, Response response) {
                    Navigator.ToLobbyList();
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.getInstance().hideToolbar();
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.inject(this, view);

        SpannedString ss = (SpannedString)tosCheckbox.getText();
        URLSpan[] spans =  ss.getSpans(0,ss.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start=ss.getSpanStart(span);
            int end=ss.getSpanEnd(span);
            int flags=ss.getSpanFlags(span);

        }

        return view;
    }
}
