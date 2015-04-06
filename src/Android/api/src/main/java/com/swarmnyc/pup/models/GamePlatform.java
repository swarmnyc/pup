package com.swarmnyc.pup.models;

import java.util.ArrayList;
import java.util.List;

public enum GamePlatform {
    Unknown (0),
    Windows (10),
    Mac (20),
    iOS (21),
    Xbox (30),
    Xbox360 (31),
    XboxOne (32),
    PS1 (40),
    PS2 (41),
    PS3 (42),
    PS4 (43),
    Wii (50),
    WiiU (51),
    Steam (60),
    Android (70);

    private final int value;

    GamePlatform(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static GamePlatform get(int value) {
        switch (value){
            case 10:
                return Windows;
            case 20:
                return Mac;
            case 21:
                return Xbox;
            case 30:
                return Xbox360;
            case 32:
                return XboxOne;
            case 40:
                return PS1;
            case 41:
                return PS2;
            case 42:
                return PS3;
            case 43:
                return PS4;
            case 50:
                return Wii;
            case 51:
                return WiiU;
            case 60:
                return Steam;
            case 70:
                return Android;
            default:
                return Unknown;
        }
    }

    /*public static List<GamePlatform> FromJsonArray(JSONArray array) throws JSONException {
        List<GamePlatform> platforms = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            platforms.add(GamePlatform.get(array.getInt(i)));
        }

        return platforms;
    }*/
}
