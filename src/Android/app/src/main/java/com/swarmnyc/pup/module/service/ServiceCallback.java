package com.swarmnyc.pup.module.service;
public abstract class ServiceCallback<T> {
    public abstract void success(T value);
    public void failure(String message){}
}

