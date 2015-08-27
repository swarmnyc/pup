package com.swarmnyc.pup.utils;

public interface Action<T> {
    void call(T value);
}
