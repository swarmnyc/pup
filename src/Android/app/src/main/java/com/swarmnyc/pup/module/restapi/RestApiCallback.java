package com.swarmnyc.pup.module.restapi;


import com.swarmnyc.pup.EventBus;
import com.swarmnyc.pup.module.service.ServiceCallback;

import java.util.concurrent.atomic.AtomicBoolean;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RestApiCallback<T> implements Callback<T> {
    private AtomicBoolean flag;
    private ServiceCallback<T> callback;

    public RestApiCallback(ServiceCallback<T> callback) {
        this.callback = callback;
    }

    public RestApiCallback(AtomicBoolean flag, ServiceCallback<T> callback) {
        this.flag = flag;
        this.callback = callback;
    }

    @Override
    public void success(T t, Response response) {
        if (flag != null)
            flag.set(false);

        if (callback != null)
            callback.success(t);
    }

    @Override
    public void failure(RetrofitError error) {
        if (flag != null)
            flag.set(false);

        if (callback != null)
            callback.failure(null);

        EventBus.getBus().post(error);
    }
}

