package com.swarmnyc.pup.module.models;

public class PuPTag {
    String key;
    String value;

    public PuPTag()
    {
    }

    public PuPTag( final String key, final String value )
    {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
