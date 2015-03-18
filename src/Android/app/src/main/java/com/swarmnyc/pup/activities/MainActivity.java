package com.swarmnyc.pup.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.swarmnyc.pup.Config;
import com.swarmnyc.pup.R;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";
    public static final int REQUEST_RESULT_CODE_RELOAD = 735623;
    public static final int REQUEST_CODE_AUTH = 2884;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
/*            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new Fragment())
                    .commit();*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (Config.isLoggedIn()){
            getMenuInflater().inflate(R.menu.menu_main_user, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main_guest, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_logoff:
                Config.removeUser();
                reload();
                return true;
            case R.id.menu_login:
                this.startActivityForResult(new Intent(this, AuthActivity.class), REQUEST_CODE_AUTH);
                return true;
            case R.id.menu_profile:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void reload() {
        invalidateOptionsMenu();
        //finish();
        //startActivity(getIntent());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_AUTH && resultCode == REQUEST_RESULT_CODE_RELOAD){
            reload();
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
