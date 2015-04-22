package com.swarmnyc.pup.Services.Filter;

import com.swarmnyc.pup.models.GamePlatform;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class GameFilter extends BaseFilter {
    HashSet<GamePlatform> platformList = new HashSet<>();

    public void addGamePlatform(GamePlatform platform) {
        platformList.add(platform);
    }

    public void remvoeGamePlatform(GamePlatform platform) {
        platformList.remove(platform);
    }

    public HashSet<GamePlatform> getPlatformList() {
        return platformList;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();

        return map;
    }
}
