package com.swarmnyc.pup.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public enum GamePlatform {
    @SerializedName("0")
    Unknown (0),
    @SerializedName("10")
    Windows (10),
    @SerializedName("20")
    Mac (20),
    @SerializedName("21")
    iOS (21),
    @SerializedName("30")
    Xbox (30),
    @SerializedName("31")
    Xbox360 (31),
    @SerializedName("32")
    XboxOne (32),
    @SerializedName("40")
    PS1 (40),
    @SerializedName("41")
    PS2 (41),
    @SerializedName("42")
    PS3 (42),
    @SerializedName("43")
    PS4 (43),
    @SerializedName("50")
    Wii (50),
    @SerializedName("51")
    WiiU (51),
    @SerializedName("60")
    Steam (60),
    @SerializedName("70")
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
