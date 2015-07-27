package com.swarmnyc.pup.Services;
public abstract class ServiceCallback<T> {
    public abstract void success(T value);
    public void failure(String message){}
}

