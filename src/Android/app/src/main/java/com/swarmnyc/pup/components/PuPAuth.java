package com.swarmnyc.pup.components;

import android.text.TextUtils;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.swarmnyc.pup.Config;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public final class PuPAuth {
    private PuPAuth() {
    }

    public static void login(String email, String password, LoginCallback callback) throws Exception {
        if (callback == null || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            throw new Exception("parameters can't be null");
        }

        RequestParams params = new RequestParams();
        params.add("client_id", "PuP");
        params.add("grant_type", "password");
        params.add("username", email);
        params.add("password", password);

        PuPRestClient.post("Login", params, new LoginHttpResponseHandler(callback));
    }

    public static void externalLogin(String provider, String email, String token, LoginCallback callback) throws Exception {
        if (callback == null || TextUtils.isEmpty(email) || TextUtils.isEmpty(token)) {
            throw new Exception("parameters can't be null");
        }

        RequestParams params = new RequestParams();
        params.add("provider", provider);
        params.add("email", email);
        params.add("token", token);

        PuPRestClient.post("ExternalLogin", params, new LoginHttpResponseHandler(callback));
    }

    private static class LoginHttpResponseHandler extends JsonHttpResponseHandler{
        private LoginCallback callback;

        private LoginHttpResponseHandler(LoginCallback callback) {
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
            callback.onFinished(false);
        }
    }

    public interface LoginCallback {
        void onFinished(boolean result);
    }
}
