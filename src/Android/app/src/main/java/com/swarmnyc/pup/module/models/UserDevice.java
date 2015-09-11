package com.swarmnyc.pup.module.models;

public class UserDevice {
    public String platform = "Android";
    public String token;

    public UserDevice(String token) {
        this.token = token;
    }
}
