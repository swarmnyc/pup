package com.swarmnyc.pup.models;

import com.google.gson.annotations.SerializedName;

public enum PlayStyle
{
    @SerializedName("1")
    Serious (1),
    @SerializedName("2")
    Casual (2);

    private final int value;

    PlayStyle(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PlayStyle get(int playStyle) {
        switch (playStyle){
            case 1:
                return Serious;
            case 2:
            default:
                return Casual;
        }
    }
}
