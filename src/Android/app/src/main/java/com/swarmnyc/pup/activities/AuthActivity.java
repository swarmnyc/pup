package com.swarmnyc.pup.activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.*;
import android.view.MenuItem;

import com.swarmnyc.pup.R;
import com.swarmnyc.pup.components.GoogleOAuth;
import com.swarmnyc.pup.fragments.*;


public class AuthActivity extends ActionBarActivity {
    private static final String TAG = "AuthActivity";

    public AuthActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getFragmentManager()
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
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GoogleOAuth.REQUEST_CODE_GOOGLE_AUTH){
            GoogleOAuth.handleResult(this, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void finishAuth() {
        setResult(MainActivity.REQUEST_RESULT_CODE_RELOAD);
        this.finish();
    }
}
