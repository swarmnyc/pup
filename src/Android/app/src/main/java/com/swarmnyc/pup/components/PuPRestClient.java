package com.swarmnyc.pup.components;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.swarmnyc.pup.Config;
import com.swarmnyc.pup.R;

public final class PuPRestClient {
    private static final String BASE_URL = Config.getConfigString(R.string.PuP_API_Url);

    private PuPRestClient() {
    }

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static RequestHandle get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        ensureHeaders();
        return client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static RequestHandle post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        ensureHeaders();

       return client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static void ensureHeaders() {
        if (Config.isLoggedIn()){
            client.addHeader("Authorization","Bearer " + Config.getUserToken());
        } else {
            client.removeHeader("Authorization");
        }
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
