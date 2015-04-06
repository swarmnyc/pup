package com.swarmnyc.pup.components;

import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.swarmnyc.pup.Config;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public final class PuPAuth {
    private static String TAG = PuPAuth.class.getSimpleName();

    private PuPAuth() {
    }

    public static void login(String email, String password, AuthCallback callback) throws Exception {
        if (callback == null || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            throw new Exception("parameters can't be null");
        }

        RequestParams params = new RequestParams();
        params.add("client_id", "PuP");
        params.add("grant_type", "password");
        params.add("username", email);
        params.add("password", password);

        PuPRestClient.post("Login", params, new AuthHttpResponseHandler(callback));
    }

    public static void externalLogin(String provider, String email, String token, AuthCallback callback) throws Exception {
        if (callback == null || TextUtils.isEmpty(email) || TextUtils.isEmpty(token)) {
            throw new Exception("parameters can't be null");
        }

        RequestParams params = new RequestParams();
        params.add("provider", provider);
        params.add("email", email);
        params.add("token", token);

        PuPRestClient.post("ExternalLogin", params, new AuthHttpResponseHandler(callback));
    }

    public static void register(String email, String password, AuthCallback callback) throws Exception {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            throw new Exception("parameters can't be null");
        }

        RequestParams params = new RequestParams();
        params.add("email", email);
        params.add("password", password);

        PuPRestClient.post("User/Register", params, new AuthHttpResponseHandler(callback));
    }

    private static class AuthHttpResponseHandler extends JsonHttpResponseHandler {
        private AuthCallback callback;

        private AuthHttpResponseHandler(AuthCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                Config.setUser(response.getString("userId"), response.getString("access_token"), response.getLong("expires_in"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            callback.onFinished(true);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            Log.w(TAG, throwable);
            callback.onFinished(false);
        }
    }

    public interface AuthCallback {
        void onFinished(boolean result);
    }
}
