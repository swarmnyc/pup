package com.swarmnyc.pup;

import retrofit.client.Response;

public abstract class PuPEmptyCallback extends PuPCallback<String> {

    public abstract void success(Response response);

    @Override
    public void success(String s, Response response) {
        success(response);
    }
}
