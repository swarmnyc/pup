package com.swarmnyc.pup.RestApis;

import retrofit.client.Response;

public abstract class EmptyRestApiCallback extends RestApiCallback<String> {

    public abstract void success(Response response);

    @Override
    public void success(String s, Response response) {
        success(response);
    }
}
