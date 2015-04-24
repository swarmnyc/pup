package com.swarmnyc.pup.components;

public interface Action<T> {
    void call(T value);
}
