package com.swarmnyc.pup.models;

public enum SkillLevel {
    Newbie (1),
    Intermediate (2),
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
