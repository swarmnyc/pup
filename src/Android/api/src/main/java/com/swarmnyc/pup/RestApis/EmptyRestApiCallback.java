package com.swarmnyc.pup.RestApis;

import com.swarmnyc.pup.Services.ServiceCallback;

import java.util.concurrent.atomic.AtomicBoolean;

import retrofit.client.Response;

public class EmptyRestApiCallback extends RestApiCallback<String> {

    public EmptyRestApiCallback(ServiceCallback<String> callback) {
        super(callback);
    }

    public EmptyRestApiCallback(AtomicBoolean flag, ServiceCallback<String> callback) {
        super(flag, callback);
    }

    @Override
    public void success(String s, Response response) {
        super.success(null, response);
    }
}
