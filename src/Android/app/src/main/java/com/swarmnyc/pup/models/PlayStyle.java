package com.swarmnyc.pup.models;

public enum PlayStyle
{
    Serious (1),
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
