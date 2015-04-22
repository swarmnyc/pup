package com.swarmnyc.pup.RestApis;


import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class PuPRestApiCallback<T> implements Callback<T> {
    @Override
    public void failure(RetrofitError error) {
        error.printStackTrace();
    }
}

