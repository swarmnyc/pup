package com.swarmnyc.pup.module.restapi;

import com.swarmnyc.pup.module.service.ServiceCallback;

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
