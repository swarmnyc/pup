package com.swarmnyc.pup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.swarmnyc.pup.PuPApplication;
import com.swarmnyc.pup.PuPCallback;
import com.swarmnyc.pup.R;
import com.swarmnyc.pup.User;
import com.swarmnyc.pup.UserService;
import com.swarmnyc.pup.components.GoogleOAuth;
import com.swarmnyc.pup.fragments.LoginFragment;
import com.swarmnyc.pup.models.LoggedInUser;

import javax.inject.Inject;

import retrofit.RetrofitError;
import retrofit.client.Response;


public class AuthActivity extends ActionBarActivity {
    private static final String TAG = "AuthActivity";

    public AuthActivity() {
    }

    @Inject
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PuPApplication.getInstance().getComponent().inject(this);
        setContentView(R.layout.activity_auth);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new LoginFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void changeFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GoogleOAuth.REQUEST_CODE_GOOGLE_AUTH) {
            GoogleOAuth.handleResult(this, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void externalLogin(String provider, String email, String token) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
        } else {
            userService.externalLogin(provider, email, token, new PuPCallback<LoggedInUser>() {
                @Override
                public void success(LoggedInUser loggedInUser, Response response) {
                    User.login(loggedInUser);
                    finishAuth();
                }

                @Override
                public void failure(RetrofitError error) {
                    super.failure(error);
                    Toast.makeText(AuthActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void finishAuth() {
        setResult(MainActivity.REQUEST_RESULT_CODE_RELOAD);
        this.finish();
    }
}
