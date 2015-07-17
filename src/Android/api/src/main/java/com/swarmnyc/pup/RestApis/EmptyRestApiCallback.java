package com.swarmnyc.pup.RestApis;

import com.swarmnyc.pup.Services.ServiceCallback;

import retrofit.client.Response;

public class EmptyRestApiCallback extends RestApiCallback<String> {

    public EmptyRestApiCallback(ServiceCallback callback) {
        super(callback);
    }

    @Override
    public void success(String s, Response response) {
        super.success(null, response);
    }
}
