package com.swarmnyc.pup.components;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.swarmnyc.pup.activities.AuthActivity;

import org.w3c.dom.Text;

public final class GoogleOAuth {
    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    public static final int REQUEST_CODE_GOOGLE_AUTH = 42884;

    private GoogleOAuth(){

    }

    public static void startGetAccount(Activity activity){
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false, null, null, null, null);
        
        activity.startActivityForResult(intent, REQUEST_CODE_GOOGLE_AUTH);
    }

    public static void handleResult(Activity activity, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
           String email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
           AuthTask task = new AuthTask();
           task.execute(activity, email);
        }
    }

    private static class GoogleAuthTaskResult{
        public AuthActivity activity;
        public String email;
        public String token;
    }

    private static class AuthTask extends AsyncTask<Object, Void, GoogleAuthTaskResult> {

        @Override
        protected GoogleAuthTaskResult doInBackground(Object... params) {
            final AuthActivity activity = (AuthActivity)params[0];
            GoogleAuthTaskResult result = null;
            String email = (String)params[1];
            result = new GoogleAuthTaskResult();
            result.activity = activity;
            try {
                String token = GoogleAuthUtil.getToken(activity, email, SCOPE);

                result.email = email;
                result.token = token;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(final GoogleAuthTaskResult result) {
            if (TextUtils.isEmpty(result.email)){
                Toast.makeText(result.activity, "Login failed", Toast.LENGTH_SHORT).show();
            }else{
                try {
                    PuPAuth.externalLogin("Google", result.email, result.token, new PuPAuth.LoginCallback() {
                        @Override
                        public void onFinished(boolean authResult) {
                            if (authResult){
                                result.activity.finishAuth();
                            } else {
                                Toast.makeText(result.activity, "Login failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(result.activity, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
