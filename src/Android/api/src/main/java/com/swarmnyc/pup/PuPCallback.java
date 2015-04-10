package com.swarmnyc.pup;


import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class PuPCallback<T> implements Callback<T> {
    @Override
    public void failure(RetrofitError error) {
        error.printStackTrace();
    }
}

