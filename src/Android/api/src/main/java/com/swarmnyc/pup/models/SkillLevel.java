package com.swarmnyc.pup.models;

import com.google.gson.annotations.SerializedName;

public enum SkillLevel {
    @SerializedName("1")
    Newbie (1),
    @SerializedName("2")
    Intermediate (2),
    @SerializedName("3")
    Pro(3);

    private final int value;

    SkillLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SkillLevel get(int skillLevel) {
        switch (skillLevel){

            case 2:
                return Intermediate;
            case 3:
                return Pro;
            case 1:
            default:
                return Newbie;
        }
    }
}
